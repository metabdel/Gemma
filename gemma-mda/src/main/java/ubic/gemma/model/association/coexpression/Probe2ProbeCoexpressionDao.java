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
package ubic.gemma.model.association.coexpression;

import java.util.Collection;
import java.util.HashMap;

import ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionDaoImpl.ProbeLink;
import ubic.gemma.model.expression.bioAssayData.DesignElementDataVector;
import ubic.gemma.model.expression.experiment.BioAssaySet;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Gene;

/**
 * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression
 */
public interface Probe2ProbeCoexpressionDao extends ubic.gemma.model.association.RelationshipDao {
    /**
     * Loads an instance of ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression from the persistent store.
     */
    public ubic.gemma.model.association.Relationship load( java.lang.Long id );

    /**
     * <p>
     * Does the same thing as {@link #load(java.lang.Long)} with an additional flag called <code>transform</code>. If
     * this flag is set to <code>TRANSFORM_NONE</code> then the returned entity will <strong>NOT</strong> be
     * transformed. If this flag is any of the other constants defined in this class then the result <strong>WILL
     * BE</strong> passed through an operation which can optionally transform the entity (into a value object for
     * example). By default, transformation does not occur.
     * </p>
     * 
     * @param id the identifier of the entity to load.
     * @return either the entity or the object transformed from the entity.
     */
    public Object load( int transform, java.lang.Long id );

    /**
     * Updates the <code>probe2ProbeCoexpression</code> instance in the persistent store.
     */
    public void update( ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression probe2ProbeCoexpression );

    /**
     * Updates all instances in the <code>entities</code> collection in the persistent store.
     */
    public void update( java.util.Collection entities );

    /**
     * Removes the instance of ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression from the persistent
     * store.
     */
    public void remove( ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression probe2ProbeCoexpression );

    /**
     * Removes the instance of ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression having the given
     * <code>identifier</code> from the persistent store.
     */
    public void remove( java.lang.Long id );

    /**
     * Removes all entities in the given <code>entities<code> collection.
     */
    public void remove( java.util.Collection entities );

    /**
     * 
     */
    public java.util.Collection getVectorsForLinks( ubic.gemma.model.genome.Gene gene,
            java.util.Collection<ExpressionExperiment> ees );

    /**
     * <p>
     * Removes the all the probe2probeCoexpression links for a given expression experiment
     * </p>
     */
    public void deleteLinks( ubic.gemma.model.expression.experiment.ExpressionExperiment ee );

    /**
     * <p>
     * Get the total number of probe2probe coexpression links for the given experiment.
     * </p>
     */
    public java.lang.Integer countLinks(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Given a collection of Genes, a collection of EE's return a Map of Genes to a collection of
     * DesignElementDataVectors that are coexpressed
     * </p>
     */
    public java.util.Map<Gene, DesignElementDataVector> getVectorsForLinks( java.util.Collection<Gene> genes,
            java.util.Collection<ExpressionExperiment> ees );

    /**
     * <p>
     * get the probe coexpression by using native sql query.
     * </p>
     */
    public java.util.Collection getProbeCoExpression(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment,
            java.lang.String taxonCommonName, boolean useWorkingTable );

    /**
     * <p>
     * Create working table if links to use in shuffled-link experiments.
     * </p>
     */
    public void prepareForShuffling( java.util.Collection<ExpressionExperiment> ees, java.lang.String taxon,
            boolean filterNonSpecific );

    /**
     * <p>
     * Return a list of all ExpressionExperiments in which the given gene was tested for coexpression in, among the
     * given ExpressionExperiments. A gene was tested if any probe for that gene passed filtering criteria during
     * analysis. It is assumed that in the database there is only one analysis per ExpressionExperiment. The boolean
     * parameter filterNonSpecific can be used to exclude ExpressionExperiments in which the gene was detected by only
     * probes predicted to be non-specific for the gene.
     * </p>
     */
    public java.util.Collection getExpressionExperimentsLinkTestedIn( ubic.gemma.model.genome.Gene gene,
            java.util.Collection<BioAssaySet> expressionExperiments, boolean filterNonSpecific );

    /**
     * <p>
     * Return a map of genes in genesB to all ExpressionExperiments in which the given set of pairs of genes was tested
     * for coexpression in, among the given ExpressionExperiments. A gene was tested if any probe for that gene passed
     * filtering criteria during analysis. It is assumed that in the database there is only one analysis per
     * ExpressionExperiment. The boolean parameter filterNonSpecific can be used to exclude ExpressionExperiments in
     * which one or both of the genes were detected by only probes predicted to be non-specific for the gene.
     * </p>
     */
    public java.util.Map getExpressionExperimentsLinkTestedIn( ubic.gemma.model.genome.Gene geneA,
            java.util.Collection<Long> genesB, java.util.Collection<BioAssaySet> expressionExperiments,
            boolean filterNonSpecific );

    /**
     * 
     */
    public java.util.List create( java.util.List links );

    /**
     * <p>
     * Retrieve all genes that were included in the link analysis for the experiment.
     * </p>
     */
    public java.util.Collection getGenesTestedBy(
            ubic.gemma.model.expression.experiment.BioAssaySet expressionExperiment, boolean filterNonSpecific );

    /**
     * 
     */
    public java.util.Map getExpressionExperimentsTestedIn( java.util.Collection<Long> geneIds,
            java.util.Collection<Long> experiments, boolean filterNonSpecific );

    public Collection<Long> validateProbesInCoexpression( Collection<Long> queryProbeIds,
            Collection<Long> coexpressedProbeIds, ExpressionExperiment ee, String taxon );
    
    /**
     * 
     * Returns the top coexpressed links under a given threshold for a given experiment up to a given limit. 
     * If the limit is null then all results under the threshold will be returned. 
     * 
     * @param ee
     * @param threshold
     * @param limit
     * @return
     */
    public Collection<ProbeLink> getTopCoexpressedLinks( ExpressionExperiment ee, double threshold, Integer limit );


}
