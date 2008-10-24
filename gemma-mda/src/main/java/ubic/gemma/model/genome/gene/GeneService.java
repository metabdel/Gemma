/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2007 University of British Columbia
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
//
// Attention: Generated code! Do not modify by hand!
// Generated by: SpringService.vsl in andromda-spring-cartridge.
//
package ubic.gemma.model.genome.gene;

import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.PredictedGene;
import ubic.gemma.model.genome.ProbeAlignedRegion;

/**
 * 
 */
public interface GeneService extends ubic.gemma.model.common.AuditableService {

    /**
     * 
     */
    public void update( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public void remove( java.lang.String officialName );

    /**
     * 
     */
    public void remove( java.util.Collection<Gene> genes );

    /**
     * 
     */
    public java.util.Collection<Gene> findByOfficialName( java.lang.String officialName );

    /**
     * 
     */
    public java.util.Collection findAllQtlsByPhysicalMapLocation(
            ubic.gemma.model.genome.PhysicalLocation physicalMapLocation );

    /**
     * 
     */
    public java.util.Collection<Gene> findByOfficialSymbol( java.lang.String officialSymbol );

    /**
     * 
     */
    public java.util.Collection<Gene> findByOfficialSymbolInexact( java.lang.String officialSymbol );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene findOrCreate( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public java.util.Collection<Gene> loadAll();

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene create( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public java.util.Collection<Gene> create( java.util.Collection<Gene> genes );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene load( long id );

    /**
     * 
     */
    public java.util.Collection<Gene> findByAlias( java.lang.String search );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene find( ubic.gemma.model.genome.Gene gene );

    /**
     * <p>
     * Function to get coexpressed genes given a gene and a collection of expressionExperiments. Returns the value
     * object:: CoexpressionCollectionValueObject
     * </p>
     */
    public java.lang.Object getCoexpressedGenes( ubic.gemma.model.genome.Gene gene, java.util.Collection ees,
            java.lang.Integer stringency, boolean knownGenesOnly );

    /**
     * 
     */
    public java.lang.Integer countAll();

    /**
     * <p>
     * Gets all the genes for a given taxon
     * </p>
     */
    public java.util.Collection<Gene> getGenesByTaxon( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * load all genes specified by the given ids.
     * </p>
     */
    public java.util.Collection<Gene> loadMultiple( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets all the microRNA for a given taxon. Note query could be slow or inexact due to use of wild card searching of
     * the genes description
     * </p>
     */
    public java.util.Collection getMicroRnaByTaxon( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Returns a list of compositeSequences associated with the given gene and array design
     * </p>
     */
    public java.util.Collection<CompositeSequence> getCompositeSequences( ubic.gemma.model.genome.Gene gene,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

//    /**
//     * 
//     */
//    public java.util.Map getCompositeSequenceMap( java.util.Collection genes );

    /**
     * 
     */
    public long getCompositeSequenceCountById( java.lang.Long id );

    /**
     * Return probes for a gene id.
     */
    public java.util.Collection<CompositeSequence> getCompositeSequencesById( java.lang.Long id );

    /**
     * <p>
     * Returns a CoexpressionCollection similar to getCoexpressedGenes() but for multiple input genes.
     * </p>
     */
    public java.lang.Object getMultipleCoexpressionResults( java.util.Collection<Gene> genes,
            java.util.Collection<ExpressionExperiment> ees, java.lang.Integer stringency );

    /**
     * <p>
     * Returns a collection of all ProbeAlignedRegion's for the specfied taxon
     * </p>
     */
    public java.util.Collection<ProbeAlignedRegion> loadProbeAlignedRegions( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Returns a collection of geneImpls for the specified taxon. Ie not probe aligned regions and predicted genes
     * </p>
     */
    public java.util.Collection<Gene> loadKnownGenes( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Returns a collection of Predicted Genes for the specified taxon
     * </p>
     */
    public java.util.Collection<PredictedGene> loadPredictedGenes( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Returns a Collection of Genes. Not ProbeAlignedRegions, Not PredictedGenes, just straight up known genes that
     * didn't have any specificty problems (ie all the probes were clean).
     * </p>
     */
    public java.util.Collection<Gene> getCoexpressedKnownGenes( ubic.gemma.model.genome.Gene gene,
            java.util.Collection<ExpressionExperiment> ees, java.lang.Integer stringency );

    /**
     * 
     */
    public void thaw( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene findByOfficialSymbol( java.lang.String symbol,
            ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public void thawLite( java.util.Collection<Gene> genes );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene findByAccession( java.lang.String accession,
            ubic.gemma.model.common.description.ExternalDatabase source );

    /**
     * 
     */
    public ubic.gemma.model.genome.Gene findByNCBIId( java.lang.String accession );

}
