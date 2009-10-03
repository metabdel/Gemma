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
import ubic.gemma.model.expression.experiment.BioAssaySet;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * 
 */
public interface Probe2ProbeCoexpressionService {

    /**
     * <p>
     * Creates a probe2probeCoexpressionService. handles all differnt types of probe2probeCoexpression. Mouse, human,
     * rat
     * </p>
     */
    public ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression create(
            ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression p2pCoexpression );

    /**
     * <p>
     * Adds a collection of probe2probeCoexpression objects at one time to the DB, in the order given.
     * </p>
     */
    public java.util.List create( java.util.List p2pExpressions );

    /**
     * 
     */
    public void delete( ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression toDelete );

    /**
     * 
     */
    public void delete( java.util.Collection deletes );

    /**
     * <p>
     * Given a Gene, a collection of EE's returns a collection of all the designElementDataVectors that were coexpressed
     * under the said given conditions.
     * </p>
     */
    public java.util.Collection getVectorsForLinks( ubic.gemma.model.genome.Gene gene, java.util.Collection ees );

    /**
     * <p>
     * removes all the probe2probeCoexpression links for the given expression experiment
     * </p>
     */
    public void deleteLinks( ubic.gemma.model.expression.experiment.ExpressionExperiment ee );

    /**
     * 
     */
    public java.lang.Integer countLinks(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Returns a map of Genes to a Collection of DesignElementDataVectors for genes coexpressed with the gene (and
     * including the gene).
     * </p>
     */
    public java.util.Map getVectorsForLinks( java.util.Collection genes, java.util.Collection ees );

    /**
     * <p>
     * Create a working table containing links by removing redundant and (optionally) non-specific probes from
     * PROBE_CO_EXPRESSION. Results are stored in a species-specific temporary table managed by this method.
     * </p>
     */
    public void prepareForShuffling( java.util.Collection ees, java.lang.String taxon, boolean filterNonSpecific );

    /**
     * <p>
     * get the co-expression by using native sql query
     * </p>
     */
    public java.util.Collection getProbeCoExpression(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment, java.lang.String taxon,
            boolean useWorkingTable );

    
    /**
     * <p>
     * get the co-expression by using native sql query but doesn't use a temporary DB table.
     * </p>
     */
    public java.util.Collection<ProbeLink> getProbeCoExpression(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment, java.lang.String taxon);
    /**
     * <p>
     * Return a list of all ExpressionExperiments in which the given gene was tested for coexpression in, among the
     * given ExpressionExperiments. A gene was tested if any probe for that gene passed filtering criteria during
     * analysis. It is assumed that in the database there is only one analysis per ExpressionExperiment. The boolean
     * parameter filterNonSpecific can be used to exclude ExpressionExperiments in which the gene was detected by only
     * probes predicted to be non-specific for the gene.
     * </p>
     */
    public java.util.Collection<BioAssaySet> getExpressionExperimentsLinkTestedIn( ubic.gemma.model.genome.Gene gene,
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
    public java.util.Map<Long, Collection<BioAssaySet>> getExpressionExperimentsLinkTestedIn(
            ubic.gemma.model.genome.Gene geneA, java.util.Collection<Long> genesB,
            java.util.Collection<BioAssaySet> expressionExperiments, boolean filterNonSpecific );

    /**
     * <p>
     * Retrieve all genes that were included in the link analysis for the experiment.
     * </p>
     */
    public java.util.Collection<Long> getGenesTestedBy(
            ubic.gemma.model.expression.experiment.BioAssaySet expressionExperiment, boolean filterNonSpecific );

    /**
     * 
     */
    public java.util.Map<Long, Collection<BioAssaySet>> getExpressionExperimentsTestedIn(
            java.util.Collection<Long> geneIds, java.util.Collection<BioAssaySet> experiments, boolean filterNonSpecific );
    
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
