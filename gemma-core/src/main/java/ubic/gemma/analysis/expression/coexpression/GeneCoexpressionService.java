/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
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
package ubic.gemma.analysis.expression.coexpression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.gemma.model.analysis.expression.coexpression.CoexpressedGenesDetails;
import ubic.gemma.model.analysis.expression.coexpression.CoexpressionCollectionValueObject;
import ubic.gemma.model.analysis.expression.coexpression.CoexpressionValueObject;
import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysis;
import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysisService;
import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionVirtualAnalysis;
import ubic.gemma.model.association.coexpression.Gene2GeneCoexpression;
import ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService;
import ubic.gemma.model.common.Securable;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.ExpressionExperimentValueObject;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;
import ubic.gemma.ontology.GeneOntologyService;
import ubic.gemma.ontology.OntologyTerm;
import ubic.gemma.util.CountingMap;
import ubic.gemma.util.GemmaLinkUtils;

/**
 * Provides access to Gene2Gene and Probe2Probe links. The use of this service provides 'high-level' access to
 * functionality in the Gene2GeneCoexpressionService and the ProbeLinkCoexpressionAnalyzer.
 * 
 * @author paul
 * @version $Id$
 * @spring.bean id="geneCoexpressionService"
 * @spring.property name="taxonService" ref="taxonService"
 * @spring.property name="gene2GeneCoexpressionService" ref="gene2GeneCoexpressionService"
 * @spring.property name="geneCoexpressionAnalysisService" ref="geneCoexpressionAnalysisService"
 * @spring.property name = "geneOntologyService" ref="geneOntologyService"
 * @spring.property name = "expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name = "probeLinkCoexpressionAnalyzer" ref="probeLinkCoexpressionAnalyzer"
 */
public class GeneCoexpressionService {

    /**
     * How many genes to fill in the "expression experiments tested in" and "go overlap" info for.
     */
    private static final int NUM_GENES_TO_DETAIL = 100;

    private static Log log = LogFactory.getLog( GeneCoexpressionService.class.getName() );

    private Gene2GeneCoexpressionService gene2GeneCoexpressionService;
    private TaxonService taxonService;
    private GeneCoexpressionAnalysisService geneCoexpressionAnalysisService;
    private GeneOntologyService geneOntologyService;
    private ExpressionExperimentService expressionExperimentService;
    private ProbeLinkCoexpressionAnalyzer probeLinkCoexpressionAnalyzer;

    /**
     * @return collection of the available canned analyses, for all taxa.
     */
    public Collection<CannedAnalysisValueObject> getCannedAnalyses() {
        Collection<CannedAnalysisValueObject> analyses = new ArrayList<CannedAnalysisValueObject>();
        for ( Object o : taxonService.loadAll() ) {
            Taxon taxon = ( Taxon ) o;
            for ( Object p : geneCoexpressionAnalysisService.findByTaxon( taxon ) ) {
                GeneCoexpressionAnalysis analysis = ( GeneCoexpressionAnalysis ) p;
                CannedAnalysisValueObject cavo = new CannedAnalysisValueObject();
                cavo.setId( analysis.getId() );
                cavo.setName( analysis.getName() );
                cavo.setDescription( analysis.getDescription() );
                cavo.setTaxon( taxon );
                if ( analysis instanceof GeneCoexpressionVirtualAnalysis ) {
                    cavo.setVirtual( true );
                }

                /*
                 * FIXME this number isn't right if there are 'troubled' data sets we filter out.
                 */
                cavo.setNumDatasets( geneCoexpressionAnalysisService.getNumDatasetsAnalyzed( analysis ) );

                analyses.add( cavo );
            }
        }
        return analyses;
    }

    /**
     * @param cannedAnalysisId
     * @param eeIds Experiments to limit the results to
     * @param queryGenes
     * @param stringency
     * @param queryGenesOnly return links among the query genes only.
     * @return
     */
    public CoexpressionMetaValueObject getFilteredCannedAnalysisResults( Long cannedAnalysisId, Collection<Long> eeIds,
            Collection<Gene> queryGenes, int stringency, boolean queryGenesOnly ) {

        GeneCoexpressionAnalysis analysis = ( GeneCoexpressionAnalysis ) geneCoexpressionAnalysisService
                .load( cannedAnalysisId );

        GeneCoexpressionAnalysis analysisToUse = getAnalysis( analysis );
        Map<Gene, Collection<Gene2GeneCoexpression>> gg2gs = getRawCoexpression( queryGenes, stringency,
                queryGenesOnly, analysisToUse );

        Collection<Long> eeIdsFromAnalysis = getIds( analysisToUse.getExperimentsAnalyzed() );
        /*
         * We get this prior to filtering so it matches the vectors stored with the analysis.
         */
        Map<Integer, Long> posToId = GeneLinkCoexpressionAnalyzer.getPositionToIdMap( eeIdsFromAnalysis );

        /*
         * Now we get the data sets we area actually concerned with.
         */
        Collection<Long> eeIdsTouse = null;
        if ( eeIds == null ) {
            eeIdsTouse = eeIdsFromAnalysis;
        } else {
            eeIdsTouse = eeIds;
        }

        List<Long> filteredEeIds = getSortedFilteredIdList( eeIdsTouse );

        // This sort is necessary.(?)
        List<ExpressionExperimentValueObject> eevos = getSortedEEvos( eeIdsTouse );

        /*
         * FIXME I'm lazy and rushed, so I'm using an existing field for this info; probably better to add another field
         * to the value object...
         */
        for ( ExpressionExperimentValueObject eevo : eevos ) {
            eevo.setExternalUri( GemmaLinkUtils.getExpressionExperimentUrl( eevo.getId() ) );
        }

        CoexpressionMetaValueObject result = initValueObject( queryGenes, eevos, true );

        Collection<CoexpressionValueObjectExt> ecvos = new ArrayList<CoexpressionValueObjectExt>();

        Collection<Gene2GeneCoexpression> seen = new HashSet<Gene2GeneCoexpression>();

        // populate the value objects.
        for ( Gene queryGene : gg2gs.keySet() ) {
            CountingMap<Long> supportCount = new CountingMap<Long>();
            Collection<Long> supportingExperimentIds = new HashSet<Long>();
            Collection<Long> datasetsTested = new HashSet<Long>();
            int linksMetPositiveStringency = 0;
            int linksMetNegativeStringency = 0;

            Collection<Gene2GeneCoexpression> g2gs = gg2gs.get( queryGene );

            for ( Gene2GeneCoexpression g2g : g2gs ) {
                Gene foundGene = g2g.getFirstGene().equals( queryGene ) ? g2g.getSecondGene() : g2g.getFirstGene();
                CoexpressionValueObjectExt ecvo = new CoexpressionValueObjectExt();
                ecvo.setQueryGene( queryGene );
                ecvo.setFoundGene( foundGene );

                Collection<Long> testingDatasets = GeneLinkCoexpressionAnalyzer.getTestedExperimentIds( g2g, posToId );
                testingDatasets.retainAll( filteredEeIds ); // necesssary in case any were filtered out (for example, if
                // this is
                // a virtual analysis; or there were 'troubled' ees.
                Collection<Long> supportingDatasets = GeneLinkCoexpressionAnalyzer.getSupportingExperimentIds( g2g,
                        posToId );
                supportingDatasets.retainAll( filteredEeIds ); // necessary in case any were filtered out.

                ecvo.setTestedDatasetVector( getDatasetVector( testingDatasets, filteredEeIds ) );
                ecvo.setSupportingDatasetVector( getDatasetVector( supportingDatasets, filteredEeIds ) );

                int numTestingDatasets = testingDatasets.size();
                int numSupportingDatasets = supportingDatasets.size();

                /*
                 * This check is necessary in case any data sets were filtered out. (i.e., we're not interested in the
                 * full set of data sets that were used in the original analysis.
                 */
                if ( numSupportingDatasets < stringency ) {
                    continue;
                }

                datasetsTested.addAll( testingDatasets );

                if ( g2g.getEffect() < 0 ) {
                    ecvo.setPositiveCorrelationSupport( 0 );
                    ecvo.setNegativeCorrelationSupport( numSupportingDatasets );
                    ++linksMetNegativeStringency;
                } else {
                    ecvo.setPositiveCorrelationSupport( numSupportingDatasets );
                    ecvo.setNegativeCorrelationSupport( 0 );
                    ++linksMetPositiveStringency;
                }
                ecvo.setSupportKey( Math.max( ecvo.getPositiveCorrelationSupport(), ecvo
                        .getNegativeCorrelationSupport() ) );
                ecvo.setNumDatasetsLinkTestedIn( numTestingDatasets );

                for ( Long id : supportingDatasets ) {
                    supportCount.increment( id );
                }

                ecvo.setSortKey();

                /*
                 * This check prevents links from being shown twice when we do "among query genes". We don't skip
                 * entirely so we get the counts for the summary table populated correctly.
                 */
                if ( !seen.contains( g2g ) ) {
                    ecvos.add( ecvo );
                }

                supportingExperimentIds.addAll( supportingDatasets );
                seen.add( g2g );
            }

            CoexpressionSummaryValueObject summary = makeSummary( eevos, datasetsTested, linksMetPositiveStringency,
                    linksMetNegativeStringency );
            result.getSummary().put( queryGene.getOfficialSymbol(), summary );

            generateDatasetSummary( eevos, result, supportCount, supportingExperimentIds, queryGene );

            getGoOverlap( ecvos, queryGene );
        }

        result.getKnownGeneResults().addAll( ecvos );
        return result;
    }

    /**
     * @param queryGenes
     * @param stringency
     * @param queryGenesOnly
     * @param analysisToUse
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<Gene, Collection<Gene2GeneCoexpression>> getRawCoexpression( Collection<Gene> queryGenes,
            int stringency, boolean queryGenesOnly, GeneCoexpressionAnalysis analysisToUse ) {
        Map<Gene, Collection<Gene2GeneCoexpression>> gg2gs = null;
        if ( queryGenesOnly ) {
            gg2gs = gene2GeneCoexpressionService.findInterCoexpressionRelationship( queryGenes, analysisToUse,
                    stringency );
        } else {
            gg2gs = gene2GeneCoexpressionService.findCoexpressionRelationships( queryGenes, analysisToUse, stringency );
        }
        return gg2gs;
    }

    /**
     * @param analysis
     * @return
     */
    private GeneCoexpressionAnalysis getAnalysis( GeneCoexpressionAnalysis analysis ) {
        GeneCoexpressionAnalysis analysisToUse;
        if ( analysis instanceof GeneCoexpressionVirtualAnalysis ) {
            analysisToUse = ( ( GeneCoexpressionVirtualAnalysis ) analysis ).getViewedAnalysis();
        } else {
            analysisToUse = analysis;
        }
        return analysisToUse;
    }

    /**
     * @param cannedAnalysisId
     * @param queryGenes
     * @param stringency
     * @param queryGenesOnly
     * @return
     */
    @SuppressWarnings("unchecked")
    public CoexpressionMetaValueObject getCannedAnalysisResults( Long cannedAnalysisId, Collection<Gene> queryGenes,
            int stringency, boolean queryGenesOnly ) {
        return getFilteredCannedAnalysisResults( cannedAnalysisId, null, queryGenes, stringency, queryGenesOnly );
    }

    /**
     * Perform a "custom" analysis, using an ad-hoc set of expreriments.
     * 
     * @param eeIds
     * @param genes
     * @param stringency
     * @param queryGenesOnly
     * @return
     */
    @SuppressWarnings("unchecked")
    public CoexpressionMetaValueObject getCustomAnalysisResults( Collection<Long> eeIds, Collection<Gene> genes,
            int stringency, boolean queryGenesOnly ) {

        if ( true ) {
            throw new RuntimeException(
                    "We're sorry. Custom analysis is not available at this time. Please select one of the other analysis options." );
        }

        if ( eeIds == null ) eeIds = new HashSet<Long>();
        Collection<ExpressionExperiment> ees = getPossibleExpressionExperiments( genes );

        if ( !eeIds.isEmpty() ) {
            // remove the expression experiments we're not interested in...
            Collection<ExpressionExperiment> eesToRemove = new HashSet<ExpressionExperiment>();
            for ( ExpressionExperiment ee : ees ) {
                if ( !eeIds.contains( ee.getId() ) ) eesToRemove.add( ee );
            }
            ees.removeAll( eesToRemove );
        }

        /*
         * repopulate eeIds with the actual eeIds we'll be searching through and load ExpressionExperimentValueObjects
         * to get summary information about the datasets...
         */
        eeIds.clear();
        for ( ExpressionExperiment ee : ees ) {
            eeIds.add( ee.getId() );
        }
        List<ExpressionExperimentValueObject> eevos = getSortedEEvos( eeIds );

        /*
         * I'm lazy and rushed, so I'm using an existing field for this info; probably better to add another field to
         * the value object...
         */
        for ( ExpressionExperimentValueObject eevo : eevos ) {
            eevo.setExternalUri( GemmaLinkUtils.getExpressionExperimentUrl( eevo.getId() ) );
        }

        CoexpressionMetaValueObject result = initValueObject( genes, eevos, false );

        boolean knownGenesOnly = true; // !SecurityService.isUserAdmin();
        result.setKnownGenesOnly( knownGenesOnly );

        /*
         * TODO this is done just naively right now. allow the user to show only interactions among their genes of
         * interest and filter the results before the time-consuming analysis is done...
         */
        Collection<Long> geneIds = new HashSet<Long>( genes.size() );
        for ( Gene gene : genes ) {
            geneIds.add( gene.getId() );
        }
        for ( Gene queryGene : genes ) {
            /*
             * FIXME instead of using the probeLinkCoexpressionAnalyzer, provide an option to use a canned analysis with
             * a filter.
             */
            CoexpressionCollectionValueObject coexpressions = probeLinkCoexpressionAnalyzer.linkAnalysis( queryGene,
                    ees, stringency, knownGenesOnly, NUM_GENES_TO_DETAIL );

            addExtCoexpressionValueObjects( queryGene, result.getDatasets(), coexpressions.getKnownGeneCoexpression(),
                    stringency, queryGenesOnly, geneIds, result.getKnownGeneResults(), result.getKnownGeneDatasets() );
            addExtCoexpressionValueObjects( queryGene, result.getDatasets(), coexpressions
                    .getPredictedCoexpressionType(), stringency, queryGenesOnly, geneIds, result
                    .getPredictedGeneResults(), result.getPredictedGeneDatasets() );
            addExtCoexpressionValueObjects( queryGene, result.getDatasets(), coexpressions
                    .getProbeAlignedCoexpressionType(), stringency, queryGenesOnly, geneIds, result
                    .getProbeAlignedRegionResults(), result.getProbeAlignedRegionDatasets() );

            CoexpressionSummaryValueObject summary = new CoexpressionSummaryValueObject();
            summary.setDatasetsAvailable( eevos.size() );
            summary.setDatasetsTested( coexpressions.getEesQueryTestedIn().size() );
            summary.setLinksFound( coexpressions.getNumKnownGenes() );
            summary.setLinksMetPositiveStringency( coexpressions.getKnownGeneCoexpression()
                    .getPositiveStringencyLinkCount() );
            summary.setLinksMetNegativeStringency( coexpressions.getKnownGeneCoexpression()
                    .getNegativeStringencyLinkCount() );
            result.getSummary().put( queryGene.getOfficialSymbol(), summary );
        }

        return result;
    }

    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    public void setGene2GeneCoexpressionService( Gene2GeneCoexpressionService gene2GeneCoexpressionService ) {
        this.gene2GeneCoexpressionService = gene2GeneCoexpressionService;
    }

    public void setGeneCoexpressionAnalysisService( GeneCoexpressionAnalysisService geneCoexpressionAnalysisService ) {
        this.geneCoexpressionAnalysisService = geneCoexpressionAnalysisService;
    }

    public void setGeneOntologyService( GeneOntologyService geneOntologyService ) {
        this.geneOntologyService = geneOntologyService;
    }

    public void setProbeLinkCoexpressionAnalyzer( ProbeLinkCoexpressionAnalyzer probeLinkCoexpressionAnalyzer ) {
        this.probeLinkCoexpressionAnalyzer = probeLinkCoexpressionAnalyzer;
    }

    public void setTaxonService( TaxonService taxonService ) {
        this.taxonService = taxonService;
    }

    /**
     * @param queryGene
     * @param eevos
     * @param coexp
     * @param stringency
     * @param queryGenesOnly
     * @param geneIds
     * @param results
     * @param datasetResults
     */
    private void addExtCoexpressionValueObjects( Gene queryGene, List<ExpressionExperimentValueObject> eevos,
            CoexpressedGenesDetails coexp, int stringency, boolean queryGenesOnly, Collection<Long> geneIds,
            Collection<CoexpressionValueObjectExt> results, Collection<CoexpressionDatasetValueObject> datasetResults ) {
        for ( CoexpressionValueObject cvo : coexp.getCoexpressionData( stringency ) ) {
            if ( queryGenesOnly && !geneIds.contains( cvo.getGeneId() ) ) continue;
            CoexpressionValueObjectExt ecvo = new CoexpressionValueObjectExt();
            ecvo.setQueryGene( queryGene );
            ecvo.setFoundGene( new SimpleGene( cvo.getGeneId(), cvo.getGeneName(), cvo.getGeneOfficialName() ) );

            ecvo.setPositiveCorrelationSupport( cvo.getPositiveLinkSupport() );
            ecvo.setNegativeCorrelationSupport( cvo.getNegativeLinkSupport() );
            ecvo.setSupportKey( 10 * Math.max( ecvo.getPositiveCorrelationSupport(), ecvo
                    .getNegativeCorrelationSupport() ) );

            /*
             * this logic is taken from CoexpressionWrapper; I don't understand it, but that's where it comes from...
             */
            if ( !cvo.getExpressionExperiments().isEmpty() ) {
                ecvo.setNonSpecificPositiveLinks( getNonSpecificLinkCount( cvo.getEEContributing2PositiveLinks(), cvo
                        .getNonspecificEE() ) );
                ecvo.setNonSpecificNegativeLinks( getNonSpecificLinkCount( cvo.getEEContributing2NegativeLinks(), cvo
                        .getNonspecificEE() ) );
                ecvo.setHybridizesWithQueryGene( cvo.isHybridizesWithQueryGene() );
                ecvo.setSupportKey( ecvo.getSupportKey() - ecvo.getNonSpecificPositiveLinks()
                        + ecvo.getNonSpecificNegativeLinks() );
            }

            ecvo.setNumDatasetsLinkTestedIn( cvo.getNumDatasetsTestedIn() );

            ecvo.setGoOverlap( cvo.getGoOverlap() != null ? cvo.getGoOverlap().size() : 0 );
            ecvo.setPossibleOverlap( cvo.getPossibleOverlap() );

            Long[] tested = new Long[eevos.size()];
            Long[] supported = new Long[eevos.size()];
            for ( int i = 0; i < eevos.size(); ++i ) {
                ExpressionExperimentValueObject eevo = eevos.get( i );
                tested[i] = ( cvo.getDatasetsTestedIn() != null && cvo.getDatasetsTestedIn().contains( eevo.getId() ) ) ? eevo
                        .getId()
                        : 0;
                supported[i] = ( cvo.getExperimentBitIds() != null && cvo.getExperimentBitIds().contains( eevo.getId() ) ) ? eevo
                        .getId()
                        : 0;
            }
            ecvo.setTestedDatasetVector( tested );
            ecvo.setSupportingDatasetVector( supported );

            ecvo.setSortKey();
            results.add( ecvo );
        }

        results.size(); // for breakpoint

        for ( ExpressionExperimentValueObject eevo : eevos ) {
            if ( !coexp.getExpressionExperimentIds().contains( eevo.getId() ) ) continue;
            ExpressionExperimentValueObject coexpEevo = coexp.getExpressionExperiment( eevo.getId() );
            if ( coexpEevo == null ) continue;
            CoexpressionDatasetValueObject ecdvo = new CoexpressionDatasetValueObject();
            ecdvo.setId( eevo.getId() );
            ecdvo.setQueryGene( queryGene.getOfficialSymbol() );
            ecdvo.setCoexpressionLinkCount( coexp.getLinkCountForEE( coexpEevo.getId() ) );
            ecdvo.setRawCoexpressionLinkCount( coexp.getRawLinkCountForEE( coexpEevo.getId() ) );
            ecdvo.setProbeSpecificForQueryGene( coexpEevo.isProbeSpecificForQueryGene() );
            ecdvo.setArrayDesignCount( eevo.getArrayDesignCount() );
            ecdvo.setBioAssayCount( eevo.getBioAssayCount() );
            datasetResults.add( ecdvo );
        }
    }

    /**
     * @param eevos
     * @param result
     * @param supportCount
     * @param supportingExperimentIds
     * @param queryGene
     */
    private void generateDatasetSummary( List<ExpressionExperimentValueObject> eevos,
            CoexpressionMetaValueObject result, CountingMap<Long> supportCount,
            Collection<Long> supportingExperimentIds, Gene queryGene ) {
        /*
         * generate dataset summary info for this query gene...
         */
        for ( ExpressionExperimentValueObject eevo : eevos ) {
            if ( !supportingExperimentIds.contains( eevo.getId() ) ) continue;
            CoexpressionDatasetValueObject ecdvo = new CoexpressionDatasetValueObject();
            ecdvo.setId( eevo.getId() );
            ecdvo.setQueryGene( queryGene.getOfficialSymbol() );
            ecdvo.setCoexpressionLinkCount( supportCount.get( eevo.getId() ).longValue() );
            ecdvo.setRawCoexpressionLinkCount( null ); // not available
            ecdvo.setProbeSpecificForQueryGene( true ); // only specific probes in these results
            ecdvo.setArrayDesignCount( eevo.getArrayDesignCount() );
            ecdvo.setBioAssayCount( eevo.getBioAssayCount() );
            result.getKnownGeneDatasets().add( ecdvo );
        }
    }

    /**
     * @param presentIds
     * @param allIds
     * @return
     */
    private Long[] getDatasetVector( Collection<Long> presentIds, List<Long> allIds ) {
        Long[] result = new Long[allIds.size()];
        int i = 0;
        for ( Long id : allIds ) {
            result[i++] = presentIds.contains( id ) ? id : 0;
        }
        return result;
    }

    /**
     * @param ecvos
     * @param queryGene
     */
    private void getGoOverlap( Collection<CoexpressionValueObjectExt> ecvos, Gene queryGene ) {
        /*
         * get GO overlap info for this query gene...
         */
        if ( geneOntologyService.isGeneOntologyLoaded() ) {
            int numQueryGeneGoTerms = geneOntologyService.getGOTerms( queryGene ).size();
            Collection<Long> overlapIds = new ArrayList<Long>();
            int i = 0;
            for ( CoexpressionValueObjectExt ecvo : ecvos ) {
                overlapIds.add( ecvo.getFoundGene().getId() );
                if ( i++ > NUM_GENES_TO_DETAIL ) break;
            }
            Map<Long, Collection<OntologyTerm>> goOverlap = geneOntologyService.calculateGoTermOverlap( queryGene,
                    overlapIds );
            for ( CoexpressionValueObjectExt ecvo : ecvos ) {
                ecvo.setPossibleOverlap( numQueryGeneGoTerms );
                Collection<OntologyTerm> overlap = goOverlap.get( ecvo.getFoundGene().getId() );
                ecvo.setGoOverlap( overlap == null ? 0 : overlap.size() );
            }
        }
    }

    /**
     * @param contributingEEs
     * @param nonSpecificEEs
     * @return
     */
    private int getNonSpecificLinkCount( Collection<Long> contributingEEs, Collection<Long> nonSpecificEEs ) {
        int n = 0;
        for ( Long id : contributingEEs ) {
            if ( nonSpecificEEs.contains( id ) ) ++n;
        }
        return n;
    }

    /**
     * @param genes
     * @return
     */
    @SuppressWarnings("unchecked")
    private Collection<ExpressionExperiment> getPossibleExpressionExperiments( Collection<Gene> genes ) {
        Collection<Long> eeIds = new HashSet<Long>();
        for ( Gene g : genes ) {
            eeIds.addAll( expressionExperimentService.findByGene( g ) );
        }
        return eeIds.isEmpty() ? new HashSet<ExpressionExperiment>() : expressionExperimentService.loadMultiple( eeIds );
    }

    /**
     * @param eeIds
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<ExpressionExperimentValueObject> getSortedEEvos( Collection<Long> eeIds ) {
        List<ExpressionExperimentValueObject> eevos = new ArrayList<ExpressionExperimentValueObject>(
                expressionExperimentService.loadValueObjects( eeIds ) );
        Collections.sort( eevos, new Comparator<ExpressionExperimentValueObject>() {
            public int compare( ExpressionExperimentValueObject eevo1, ExpressionExperimentValueObject eevo2 ) {
                return eevo1.getId().compareTo( eevo2.getId() );
            }
        } );
        return eevos;
    }

    /**
     * Remove data sets that are 'troubled' and sort the list.
     * 
     * @param datasets
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Long> getSortedFilteredIdList( Collection<Long> datasets ) {

        removeTroubledEes( datasets );

        List<Long> ids = new ArrayList<Long>( datasets );
        Collections.sort( ids );
        return ids;
    }

    /**
     * @param datasets
     */
    private List<Long> getIds( Collection<ExpressionExperiment> datasets ) {
        List<Long> ids = new ArrayList<Long>( datasets.size() );
        for ( Securable dataset : datasets ) {
            ids.add( dataset.getId() );
        }
        return ids;
    }

    /**
     * @param genes
     * @param eevos
     * @param isCannedF
     * @return
     */
    private CoexpressionMetaValueObject initValueObject( Collection<Gene> genes,
            List<ExpressionExperimentValueObject> eevos, boolean isCanned ) {
        CoexpressionMetaValueObject result = new CoexpressionMetaValueObject();
        result.setQueryGenes( new ArrayList<Gene>( genes ) );
        result.setDatasets( eevos );
        result.setIsCannedAnalysis( isCanned );
        result.setKnownGeneDatasets( new ArrayList<CoexpressionDatasetValueObject>() );
        result.setKnownGeneResults( new ArrayList<CoexpressionValueObjectExt>() );
        result.setPredictedGeneDatasets( new ArrayList<CoexpressionDatasetValueObject>() );
        result.setPredictedGeneResults( new ArrayList<CoexpressionValueObjectExt>() );
        result.setProbeAlignedRegionDatasets( new ArrayList<CoexpressionDatasetValueObject>() );
        result.setProbeAlignedRegionResults( new ArrayList<CoexpressionValueObjectExt>() );
        result.setSummary( new HashMap<String, CoexpressionSummaryValueObject>() );
        return result;
    }

    /**
     * @param eevos
     * @param datasetsTested
     * @param linksMetPositiveStringency
     * @param linksMetNegativeStringency
     * @return
     */
    private CoexpressionSummaryValueObject makeSummary( List<ExpressionExperimentValueObject> eevos,
            Collection<Long> datasetsTested, int linksMetPositiveStringency, int linksMetNegativeStringency ) {
        CoexpressionSummaryValueObject summary = new CoexpressionSummaryValueObject();
        summary.setDatasetsAvailable( eevos.size() );
        summary.setDatasetsTested( datasetsTested.size() );
        summary.setLinksFound( linksMetPositiveStringency + linksMetNegativeStringency );
        summary.setLinksMetPositiveStringency( linksMetPositiveStringency );
        summary.setLinksMetNegativeStringency( linksMetNegativeStringency );
        return summary;
    }

    /**
     * FIXME partly duplicates code from ExpressionExperimentManipulatingCLI
     * 
     * @param ees
     */
    @SuppressWarnings("unchecked")
    private void removeTroubledEes( Collection<Long> ees ) {
        if ( ees == null || ees.size() == 0 ) {
            log.warn( "No experiments to remove troubled from" );
            return;
        }

        int size = ees.size();
        final Map<Long, AuditEvent> trouble = expressionExperimentService.getLastTroubleEvent( ees );
        CollectionUtils.filter( ees, new Predicate() {
            public boolean evaluate( Object id ) {
                boolean hasTrouble = trouble.containsKey( id );
                return !hasTrouble;
            }
        } );
        int newSize = ees.size();
        if ( newSize != size ) {
            assert newSize < size;
            log.info( "Removed " + ( size - newSize ) + " experiments with 'trouble' flags, leaving " + newSize );
        }
    }

    /*
     * I assume the reason Genes weren't being loaded before is that it was too time consuming, so we'll do this
     * instead...
     */
    private static class SimpleGene extends Gene {
        public SimpleGene( Long id, String name, String officialName ) {
            super();
            this.setId( id );
            this.setOfficialSymbol( name );
            this.setOfficialName( officialName );
        }
    }

}
