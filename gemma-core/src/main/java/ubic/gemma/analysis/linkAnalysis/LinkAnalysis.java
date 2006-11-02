package ubic.gemma.analysis.linkAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import corejava.Format;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.ObjectArrayList;
import ubic.basecode.bio.geneset.GeneAnnotations;
import ubic.basecode.dataStructure.Link;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;
import ubic.basecode.datafilter.AffymetrixProbeNameFilter;
import ubic.basecode.datafilter.Filter;
import ubic.basecode.math.CorrelationStats;
import ubic.basecode.math.Stats;
import ubic.gemma.model.association.coexpression.HumanProbeCoExpression;
import ubic.gemma.model.association.coexpression.MouseProbeCoExpression;
import ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression;
import ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService;
import ubic.gemma.model.association.coexpression.RatProbeCoExpression;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVectorService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.gene.GeneService;

/*
* @author xiangwan
*/

public class LinkAnalysis {
	private MatrixRowPairAnalysis metricMatrix;
    private DoubleArrayList cdf;
    private ObjectArrayList keep;
    private DoubleMatrixNamed dataMatrix = null;
    private Collection<DesignElementDataVector> dataVectors = null;
    private Probe2ProbeCoexpressionService ppService = null;
    private DesignElementDataVectorService deService = null;
	private HashMap<String,String> probeToGeneMap = null;
	private HashMap<String,Set> geneToProbeMap = null;
	private Map p2v = null;
    private Taxon taxon = null;
    
    private int uniqueItems = 0;
    private double upperTailCut;
    private double lowerTailCut;
    private Format form;
    private DbManager dbManager = null;
    
	private String metric = "pearson";
    private double tooSmallToKeep = 0.5;
    private boolean absoluteValue = false;
    private double fwe = 0.01;
    private double cdfCut = 0.01; // 1.0 means, keep everything.
    private double minPresentFraction = 0.3;
    private double lowExpressionCut = 0.3;
    private double binSize = 0.01;
    private boolean useDB = false;
    
    private boolean minPresentFractionIsSet = false;
    private boolean lowExpressionCutIsSet = false;
    
	private String localHome = "c:";
	
	protected static final Log log = LogFactory.getLog(LinkAnalysis.class);
    
    public LinkAnalysis()
    {
    	form = new Format( "%.4g" );
    }
	public void initDB()
	{
        if(useDB)
        {
    		try{
    			dbManager = new DbManager("tmm");
    		}catch(SQLException e)
    		{
    			System.err.print("Errors in Connecting the Database");
    			e.printStackTrace();
    		}
        }
	}
	private void filter()
	{
        Filter x = new AffymetrixProbeNameFilter();
        DoubleMatrixNamed r = ( DoubleMatrixNamed ) x.filter( this.dataMatrix );
        this.dataMatrix = r;
        System.err.println(this.dataMatrix);
        this.uniqueItems = this.dataMatrix.rows();
	}	
	private void calculateDistribution()
	{
        if ( metric.equals("pearson" ) ) {
            metricMatrix = MatrixRowPairAnalysisFactory.pearson( this.dataMatrix, this.tooSmallToKeep );
        } else if (metric.equals( "spearmann" ) ) {
            // metricMatrix = MatrixRowPairAnalysisFactory.spearman(dataMatrix, tooSmallToKeep);
        }
        
		metricMatrix.setDuplicateMap(geneToProbeMap, probeToGeneMap);

		metricMatrix.setUseAbsoluteValue( this.absoluteValue);
        metricMatrix.calculateMetrics();
        System.err.println( "Completed first pass over the data. Cached " + metricMatrix.numCached()
                + " values in the correlation matrix with values over " + this.tooSmallToKeep );

	}   
	public void chooseCutPoints() 
	{

        if ( cdfCut <= 0.0 ) {
            upperTailCut = 1.0;
            lowerTailCut = -1.0;
            return;
        }

        if ( cdfCut >= 1.0 ) {
            upperTailCut = 0.0;
            lowerTailCut = 0.0;
            return;
        }

        double cdfTailCut = cdfCut;
        double cdfUpperCutScore = 0.0;
        double cdfLowerCutScore = 0.0;

        // find the lower tail cutpoint, if we have to.
        if ( !this.absoluteValue ) {
            cdfTailCut /= 2.0;
            // find the lower cut point. Roundoff could be a problem...really need two cdfs or do it directly from
            // histogram.
            	for (  int i = 0; i < cdf.size(); i++  ) 
            	{
            		if ( 1.0 - cdf.get( i ) >= cdfTailCut )
            		{
            			cdfLowerCutScore = metricMatrix.getHistogram().xAxis().binUpperEdge(i == cdf.size() ? i : i + 1);
            			break;
            		}
            	}
            	System.err.println( form.format( cdfLowerCutScore ) + " is the lower cdf cutpoint at " + cdfTailCut );
        }

        // find the upper cut point.
        for ( int i = cdf.size() - 1; i >= 0; i--) {
            if ( cdf.get( i ) >= cdfTailCut ) {
                cdfUpperCutScore = metricMatrix.getHistogram().xAxis().binLowerEdge(i);
                break;
            }
        }


        System.err.println( form.format( cdfUpperCutScore ) + " is the upper cdf cutpoint at " + cdfTailCut );

        // get the cutpoint based on statistical signficance.
        double maxP = 1.0;
        double scoreAtP = 0.0;
        if ( fwe != 0.0 ) {
            maxP = fwe / uniqueItems; // bonferroni.
            scoreAtP = CorrelationStats.correlationForPvalue( maxP, this.dataMatrix.columns() );
            System.err.println( "Minimum correlation to get " + form.format( maxP ) + " is about "
                    + form.format( scoreAtP ) + " for " + uniqueItems + " unique items (if all " + this.dataMatrix.columns()
                    + " items are present)" );
        }
        this.metricMatrix.setPValueThreshold( maxP ); // this is the corrected value.

        upperTailCut = Math.max( scoreAtP, cdfUpperCutScore );
        System.err.println( "Final upper cut is " + form.format( upperTailCut ) );

        if ( !this.absoluteValue ) {
            lowerTailCut = Math.min( -scoreAtP, cdfLowerCutScore );
            System.err.println( "Final lower cut is " + form.format( lowerTailCut ) );
        }
        
        metricMatrix.setUpperTailThreshold( upperTailCut );
        if ( absoluteValue ) {
            metricMatrix.setLowerTailThreshold( upperTailCut );
        } else {
            metricMatrix.setLowerTailThreshold( lowerTailCut );
        }

    }

	private void getLinks()
	{
        cdf = Stats.cdf( metricMatrix.getHistogramArrayList() );
        chooseCutPoints();
        metricMatrix.calculateMetrics();
        keep = metricMatrix.getKeepers();
        System.err.println( "Selected " + keep.size() + " values to keep" );
	}
/*
	@SuppressWarnings("unchecked")
	private Collection<String> getActiveProbeIdSet() {
		Collection probeIdSet = new HashSet<String>();
        for ( String rowName : ( Collection<String> ) this.dataMatrix.getRowNames() ) 
        	probeIdSet.add(rowName);
        return probeIdSet;
	}
*/
	@SuppressWarnings("unchecked")
	private void init()
	{
        this.initDB();
/*
		try {
			this.geneAnnotations = new GeneAnnotations(this.localHome+"/TestData/" + this.geneAnnotationFile, rowsToUse, null,
					null);
		} catch (IOException e) {
			log.error("Error in reading GO File");
		}
		this.uniqueItems = this.geneAnnotations.numGenes();
*/
		this.p2v = new HashMap<String,DesignElementDataVector>();

		this.probeToGeneMap = new HashMap<String, String>();
		this.geneToProbeMap = new HashMap<String, Set>();

		for(DesignElementDataVector vector:this.dataVectors)
		{
			/***Initialize the map between probe and designElementDataVector***/
			String probeName = vector.getDesignElement().getName();
			p2v.put(probeName,vector);
			/***Initialize the map between probe and gene 1-1 mapping***/
			Collection<Gene> geneSet = this.deService.getGenes(vector);
			String geneName = null;
			if(geneSet != null && !geneSet.isEmpty())
				geneName = geneSet.iterator().next().getName();
			else
				continue;
			this.probeToGeneMap.put(probeName,geneName);
			
			/***Initialize the map between gene and probeSet 1-n mapping***/
			Set probeSet = (Set)this.geneToProbeMap.get(geneName);
			if(probeSet == null)
			{
				Set tmpSet = new HashSet();
				tmpSet.add(probeName);
				this.geneToProbeMap.put(geneName, tmpSet);
			}
			else
				probeSet.add(probeName);
		}
		assert(geneToProbeMap.size() != 0);
		this.uniqueItems = geneToProbeMap.size();
//		this.uniqueItems = 10000;
		double scoreP = CorrelationStats.correlationForPvalue(this.fwe/this.uniqueItems, this.dataMatrix.columns())- 0.001;
		if(scoreP > this.tooSmallToKeep)
			this.tooSmallToKeep = scoreP;
	}
	private void saveLinks()
	{
		try{
			/*********Find the dataVector for each probe first****/
            int c = dataMatrix.columns();
            int[] p1vAr = new int[keep.size()];
            int[] p2vAr = new int[keep.size()];
            int[] cbAr = new int[keep.size()];
            int[] pbAr = new int[keep.size()];
            System.err.println( "Ready to submit to Gemd database." );
            StopWatch watch = new StopWatch();
            watch.start();
            Collection <Probe2ProbeCoexpression> p2plinks = new HashSet<Probe2ProbeCoexpression>();
            for ( int i = 0, n = keep.size(); i < n; i++ ) {
                Link m = ( Link ) keep.get( i );
                double w = m.getWeight();

                String p1 = dataMatrix.getRowName( m.getx() );
                String p2 = dataMatrix.getRowName( m.gety() );

                DesignElementDataVector v1 = (DesignElementDataVector)p2v.get(p1);
                DesignElementDataVector v2 = (DesignElementDataVector)p2v.get(p2);
                p1vAr[i] = new Long(v1.getId()).intValue();
                p2vAr[i] = new Long(v2.getId()).intValue();
                cbAr[i] = CorrelationStats.correlAsByte( w );
                pbAr[i] = CorrelationStats.pvalueAsByte( w, c );
                if(this.useDB)
                {
                	Probe2ProbeCoexpression ppCoexpression = null;
                	
                    if ( taxon.getCommonName().equals( "mouse" ) ) {
                        ppCoexpression = MouseProbeCoExpression.Factory.newInstance();
                    } else if ( taxon.getCommonName().equals( "rat" ) ) {
                    	ppCoexpression = RatProbeCoExpression.Factory.newInstance();
                    } else if ( taxon.getCommonName().equals( "human" ) ) {
                    	ppCoexpression = HumanProbeCoExpression.Factory.newInstance();
                    }
                    ppCoexpression.setFirstVector(v1);
                    ppCoexpression.setSecondVector(v2);
                    ppCoexpression.setScore(w);
                    ppCoexpression.setPvalue(CorrelationStats.pvalue(w,c));
                    ppCoexpression.setQuantitationType(v1.getQuantitationType());
                    p2plinks.add(ppCoexpression);
                    //Probe2ProbeCoexpression ppCo = this.ppService.create(ppCoexpression);
                }
            }
            this.ppService.create(p2plinks);
            watch.stop();
            System.err.println( "The time for inserting " + this.keep.size() + " link:" + watch.getTime() );
/**
            System.err.println( "Ready to submit to tmm database." );
            if(this.useDB)
            {
            	LinkInserter li = new LinkInserter( this.dbManager.getDbHandle() );
            	int dataSetId = 0;
            	int rd = li.insertBulkLink( dataSetId, p1vAr, p2vAr, cbAr, pbAr );
            	System.err.println( "Inserted " + rd + " links into the database" );
            }
*/
		}catch(Exception e)
		{
			System.err.println("Errors when inserting the links into database");
			e.printStackTrace();
		}
		
	}
	public void analysis()
	{
		assert this.dataMatrix != null;
		assert this.dataVectors != null;
		assert this.ppService != null;
		assert this.taxon != null;
		assert this.deService != null;
		System.err.println("Taxon: "+this.taxon.getCommonName());
		
		this.init();
		this.outputOptions();
		this.calculateDistribution();
		this.getLinks();
		this.saveLinks();
	}
	public void outputOptions()
	{
		System.err.println("Current Setting");
		System.err.println("AbsouteValue Setting:"+this.absoluteValue);
		System.err.println("BinSize:"+this.binSize);
		System.err.println("cdfCut:"+this.cdfCut);
		System.err.println("catchCut:"+this.tooSmallToKeep);
		System.err.println("Unique Items:"+this.uniqueItems);
		System.err.println("fwe:"+this.fwe);
		System.err.println("useDB:"+this.useDB);
		System.err.println("lowExpressionCut:"+this.lowExpressionCut);
		System.err.println("minPresentationCut:"+this.minPresentFraction);
	}

	public void setAbsoluteValue() {
		this.absoluteValue = true;
	}
	public void setBinSize(double binSize) {
		this.binSize = binSize;
	}
	public void setCdfCut(double cdfCut) {
		this.cdfCut = cdfCut;
	}
	public void setFwe(double fwe) {
		this.fwe = fwe;
	}
	public void setDataMatrix(DoubleMatrixNamed paraDataMatrix) {
		this.dataMatrix = paraDataMatrix;
	}
	public void setTaxon(Taxon taxon)
	{
		this.taxon = taxon;
	}
	public void setPPService(Probe2ProbeCoexpressionService ppService) {
		this.ppService = ppService;
	}
	public void setDEService(DesignElementDataVectorService deService) {
		this.deService = deService;
	}
	public void setDataVector(Collection <DesignElementDataVector> vectors) {
		this.dataVectors = vectors;
	}
	public void setLowExpressionCut(double lowExpressionCut) {
		this.lowExpressionCut = lowExpressionCut;
		this.lowExpressionCutIsSet = true;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public void setMinPresentFraction(double minPresentFraction) {
		this.minPresentFraction = minPresentFraction;
		this.minPresentFractionIsSet = true;
	}
	public void setTooSmallToKeep(double tooSmallToKeep) {
		this.tooSmallToKeep = tooSmallToKeep;
	}
	public void setUseDB() {
		this.useDB = true;
	}
	public void setHomeDir(String paraLocalHome){
		this.localHome = paraLocalHome;
	}
	public void writeDataIntoFile(String paraFileName)
	{
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(this.localHome+"/TestResult/"
					+ paraFileName));
		} catch (IOException e) {
			log.error("File for output expression data "
					+ this.localHome+"/TestResult/" + paraFileName
					+ "could not be opened");
		}
		try {
			int cols = this.dataMatrix.columns();
			for(int i = 0; i < cols; i++)
			{
				writer.write("\t" + this.dataMatrix.getColName(i));
			}
			writer.write("\n");
			int rows = this.dataMatrix.rows();
			for(int i = 0; i < rows; i++)
			{
				writer.write(this.dataMatrix.getRowName(i));
				double rowData[] = this.dataMatrix.getRow(i);
				for(int j = 0; j < rowData.length; j++)
					writer.write("\t"+rowData[j]);
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			log.error("Error in write data into file");
		}
	}

}
