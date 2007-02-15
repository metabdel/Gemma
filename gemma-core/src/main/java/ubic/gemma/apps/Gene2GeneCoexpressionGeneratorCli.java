/*
 * The Gemma project.
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubic.gemma.apps;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import ubic.gemma.model.analysis.Analysis;
import ubic.gemma.model.analysis.AnalysisService;
import ubic.gemma.model.analysis.Investigation;
import ubic.gemma.model.association.coexpression.Gene2GeneCoexpression;
import ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService;
import ubic.gemma.model.association.coexpression.HumanGeneCoExpression;
import ubic.gemma.model.association.coexpression.MouseGeneCoExpression;
import ubic.gemma.model.association.coexpression.OtherGeneCoExpression;
import ubic.gemma.model.association.coexpression.RatGeneCoExpression;
import ubic.gemma.model.coexpression.CoexpressionCollectionValueObject;
import ubic.gemma.model.coexpression.CoexpressionValueObject;
import ubic.gemma.model.common.protocol.Protocol;
import ubic.gemma.model.common.protocol.ProtocolService;
import ubic.gemma.model.expression.analysis.ExpressionAnalysis;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;
import ubic.gemma.model.genome.gene.GeneService;
import ubic.gemma.util.AbstractSpringAwareCLI;

/**
 * @author klc
 */

public class Gene2GeneCoexpressionGeneratorCli extends AbstractSpringAwareCLI {

    private static final int DEFAULT_STRINGINCY = 2;
    // Used Services
    ExpressionExperimentService eeS;
    GeneService geneS;
    TaxonService taxonS;
    Gene2GeneCoexpressionService gene2geneS;
    AnalysisService analysisS;
    ProtocolService protocolS;

    Collection<ExpressionExperiment> toUseEE;
    Collection<Gene> toUseGenes;
    Taxon toUseTaxon;
    Analysis toUseAnalysis;
    int toUseStringency;

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractCLI#buildOptions()
     */

    @SuppressWarnings("static-access")
    @Override
    protected void buildOptions() {

        Option geneFileOption = OptionBuilder.hasArg().withArgName( "Gene List File Name" ).withDescription(
                "A text file that contains a list of gene symbols.  a new gene symbon on each line" ).withLongOpt(
                "geneFile" ).create( 'g' );

        Option expExperimentFileOption = OptionBuilder
                .hasArg()
                .withArgName( "Expression Experiment List File Name" )
                .withDescription(
                        "A text file that contains a list of expression experiments. Each line of the file contains the short name or the name of the expressionExperiment" )
                .withLongOpt( "eeFile" ).create( 'e' );

        Option taxonOption = OptionBuilder.hasArg().withArgName( "Taxon" ).withDescription( "The taxon to use" )
                .withLongOpt( "taxon.  Use the common name." ).create( 't' );

        Option stringencyOption = OptionBuilder.hasArg().withArgName( "Stringency" ).withDescription(
                "The stringency value: Defaults to 2" ).withLongOpt( "stringency" ).create( 's' );

        geneFileOption.setRequired( true );
        expExperimentFileOption.setRequired( true );
        taxonOption.setRequired( true );

        addOption( geneFileOption );
        addOption( expExperimentFileOption );
        addOption( taxonOption );
        addOption( stringencyOption );

    }

    public static void main( String[] args ) {
        Gene2GeneCoexpressionGeneratorCli p = new Gene2GeneCoexpressionGeneratorCli();
        try {
            Exception ex = p.doWork( args );
            if ( ex != null ) {
                ex.printStackTrace();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.util.AbstractCLI#doWork(java.lang.String[])
     */
    @Override
    protected Exception doWork( String[] args ) {
        Exception err = processCommandLine( "Gene 2 Gene Coexpression Caching tool ", args );
        if ( err != null ) return err;

        Collection<Gene> processedGenes = new HashSet<Gene>();

        for ( Gene gene : toUseGenes ) {

            CoexpressionCollectionValueObject coexpressions = ( CoexpressionCollectionValueObject ) geneS
                    .getCoexpressedGenes( gene, toUseEE, toUseStringency );

            persistCoexpressions( gene, coexpressions, processedGenes );

            processedGenes.add( gene );
        }

        return null;
    }

    /**
     * @param toPersist
     * @param alreadyPersisted
     */
    protected void persistCoexpressions( Gene firstGene, CoexpressionCollectionValueObject toPersist,
            final Collection<Gene> alreadyPersisted ) {

        Gene2GeneCoexpression g2gCoexpression;

        if ( toUseTaxon.getCommonName().equalsIgnoreCase( "mouse" ) )
            g2gCoexpression = MouseGeneCoExpression.Factory.newInstance();
        else if ( toUseTaxon.getCommonName().equalsIgnoreCase( "rat" ) )
            g2gCoexpression = RatGeneCoExpression.Factory.newInstance();
        else if ( toUseTaxon.getCommonName().equalsIgnoreCase( "human" ) )
            g2gCoexpression = HumanGeneCoExpression.Factory.newInstance();
        else
            g2gCoexpression = OtherGeneCoExpression.Factory.newInstance();

        log.info( "Persisting Gene2Gene coexpression data to the "+ toUseTaxon.getCommonName() + "GeneCoexpression table" );      
        g2gCoexpression.setSourceAnalysis( toUseAnalysis );

        for ( CoexpressionValueObject co : toPersist.getCoexpressionData() ) {

            Gene secondGene = geneS.load( co.getGeneId() );
            if ( alreadyPersisted.contains( secondGene ) ) continue;

            g2gCoexpression.setFirstGene( firstGene );
            g2gCoexpression.setSecondGene( secondGene );
            g2gCoexpression.setNumDataSets( co.getExpressionExperimentValueObjects().size() );
            g2gCoexpression.setPvalue( co.getCollapsedPValue() );

            if ( co.getNegativeLinkCount() >= toUseStringency ) {
                g2gCoexpression.setEffect( co.getNegitiveScore() );
                gene2geneS.create( g2gCoexpression );
            }

            if ( co.getPositiveLinkCount() >= toUseStringency ) {
                g2gCoexpression.setEffect( co.getPositiveScore() );
                gene2geneS.create( g2gCoexpression );
            }
            
            log.info("Persisted: " + firstGene.getOfficialSymbol() + " --> " + secondGene.getOfficialSymbol() + " ( " + co.getNegitiveScore() + " , +" + co.getPositiveScore() + " )");

            // TODO optimization: this could be cached and done in a collection create after the for loop. Faster at the
            // cost of higher memory requirements.

        }

    }

    /**
     * 
     */

    protected void processOptions() {

        super.processOptions();

        initSpringBeans();

        if ( this.hasOption( 't' ) ) {
            toUseTaxon = taxonS.findByCommonName( this.getOptionValue( 't' ) );
        }

        if ( this.hasOption( 'e' ) ) {
            processEEFile( this.getOptionValue( 'e' ) );
        }

        if ( this.hasOption( 'g' ) ) {
            processGeneFile( this.getOptionValue( 'g' ) );
        }

        toUseStringency = DEFAULT_STRINGINCY;
        if ( this.hasOption( 's' ) ) {
            toUseStringency = Integer.parseInt( this.getOptionValue( 's' ) );
        }

        initAttributes();

    }

    private void processEEFile( String fileName ) {

        Collection<String> eeIds = processFile( fileName );

        for ( String id : eeIds ) {
            ExpressionExperiment ee = eeS.findByName( id );

            if ( ee == null ) ee = eeS.findByShortName( id );

            if ( ee == null ) {
                log.info( "Couldn't find Expression Experiment: " + id );
                continue;
            }

            toUseEE.add( ee );
            log.info( "Expression Expreiment: " + ee.getShortName() + " added to processing list " );
        }

    }

    private void processGeneFile( String fileName ) {

        Collection<String> geneIds = processFile( fileName );

        if ( ( geneIds == null ) || ( geneIds.isEmpty() ) ) {
            log.warn( "No valid genes found.  Unable to process" );
            return;
        }

        for ( String id : geneIds ) {

            Collection<Gene> genes = geneS.findByOfficialSymbol( id );

            if ( ( genes == null ) || ( genes.isEmpty() ) ) genes = geneS.findByOfficialName( id );

            if ( ( genes == null ) || ( genes.isEmpty() ) ) {
                log.info( "Gene with id: " + id + " not found.  Removed from processing list" );
                continue;
            }
            // What to do with a search results that returns more than one gene?
            for ( Gene g : genes ) {

                if ( !toUseTaxon.equals( g.getTaxon() ) ) {
                    log.info( "Gene " + g.getOfficialSymbol() + " with id: " + g.getId()
                            + "removed from processing list. Wrong Taxon. " );
                    continue;
                }

                toUseGenes.add( g );
                log.info( "Gene " + g.getOfficialSymbol() + " with id: " + g.getId() + " added to processing list " );

            }
        }
    }

    private void initSpringBeans() {

        geneS = ( GeneService ) this.getBean( "geneService" );
        eeS = ( ExpressionExperimentService ) this.getBean( "expressionExperimentService" );
        taxonS = ( TaxonService ) this.getBean( "taxonService" );
        gene2geneS = ( Gene2GeneCoexpressionService ) this.getBean( "gene2GeneCoexpressionService" );
        analysisS = ( AnalysisService ) this.getBean( "analysisService" );
        protocolS = ( ProtocolService ) this.getBean( "protocolService" );

        toUseEE = new HashSet<ExpressionExperiment>();
        toUseGenes = new HashSet<Gene>();
    }

    private void initAttributes() {

        toUseAnalysis = ExpressionAnalysis.Factory.newInstance();
        toUseAnalysis.setAnalyzedInvestigation( new HashSet<Investigation>( toUseEE ) );
        toUseAnalysis.setDescription( "Coexpression analysis for " + toUseTaxon.getCommonName() + "using "
                + toUseEE.size() + " expression experiments" );

        Calendar cal = new GregorianCalendar();

        toUseAnalysis.setName( "Generated by: Gene2GeneCoexpressionCli on: " + cal.get( Calendar.YEAR ) + " "
                + cal.get( Calendar.MONTH ) + " " + cal.get( Calendar.DAY_OF_MONTH ) + " "
                + cal.get( Calendar.HOUR_OF_DAY ) + ":" + cal.get( Calendar.MINUTE ) );

        Protocol protocol = Protocol.Factory.newInstance();
        protocol.setName( "Stored Gene2GeneCoexpressions" );
        protocol.setDescription( "Using: " + this.toUseEE.size() + " Expression Experiments,  " + toUseGenes.size()
                + " Genes" );
        protocol = protocolS.findOrCreate( protocol );

        toUseAnalysis.setProtocol( protocol );
        toUseAnalysis = analysisS.create( toUseAnalysis );

    }

}