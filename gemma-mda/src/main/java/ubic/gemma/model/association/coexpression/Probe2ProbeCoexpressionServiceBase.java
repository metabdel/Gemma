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

/**
 * <p>
 * Spring Service base class for <code>ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService</code>,
 * provides access to all services and entities referenced by this service.
 * </p>
 * 
 * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService
 */
public abstract class Probe2ProbeCoexpressionServiceBase implements
        ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService {

    private ubic.gemma.model.association.coexpression.HumanProbeCoExpressionDao humanProbeCoExpressionDao;

    /**
     * Sets the reference to <code>humanProbeCoExpression</code>'s DAO.
     */
    public void setHumanProbeCoExpressionDao(
            ubic.gemma.model.association.coexpression.HumanProbeCoExpressionDao humanProbeCoExpressionDao ) {
        this.humanProbeCoExpressionDao = humanProbeCoExpressionDao;
    }

    /**
     * Gets the reference to <code>humanProbeCoExpression</code>'s DAO.
     */
    protected ubic.gemma.model.association.coexpression.HumanProbeCoExpressionDao getHumanProbeCoExpressionDao() {
        return this.humanProbeCoExpressionDao;
    }

    private ubic.gemma.model.association.coexpression.MouseProbeCoExpressionDao mouseProbeCoExpressionDao;

    /**
     * Sets the reference to <code>mouseProbeCoExpression</code>'s DAO.
     */
    public void setMouseProbeCoExpressionDao(
            ubic.gemma.model.association.coexpression.MouseProbeCoExpressionDao mouseProbeCoExpressionDao ) {
        this.mouseProbeCoExpressionDao = mouseProbeCoExpressionDao;
    }

    /**
     * Gets the reference to <code>mouseProbeCoExpression</code>'s DAO.
     */
    protected ubic.gemma.model.association.coexpression.MouseProbeCoExpressionDao getMouseProbeCoExpressionDao() {
        return this.mouseProbeCoExpressionDao;
    }

    private ubic.gemma.model.association.coexpression.OtherProbeCoExpressionDao otherProbeCoExpressionDao;

    /**
     * Sets the reference to <code>otherProbeCoExpression</code>'s DAO.
     */
    public void setOtherProbeCoExpressionDao(
            ubic.gemma.model.association.coexpression.OtherProbeCoExpressionDao otherProbeCoExpressionDao ) {
        this.otherProbeCoExpressionDao = otherProbeCoExpressionDao;
    }

    /**
     * Gets the reference to <code>otherProbeCoExpression</code>'s DAO.
     */
    protected ubic.gemma.model.association.coexpression.OtherProbeCoExpressionDao getOtherProbeCoExpressionDao() {
        return this.otherProbeCoExpressionDao;
    }

    private ubic.gemma.model.association.coexpression.RatProbeCoExpressionDao ratProbeCoExpressionDao;

    /**
     * Sets the reference to <code>ratProbeCoExpression</code>'s DAO.
     */
    public void setRatProbeCoExpressionDao(
            ubic.gemma.model.association.coexpression.RatProbeCoExpressionDao ratProbeCoExpressionDao ) {
        this.ratProbeCoExpressionDao = ratProbeCoExpressionDao;
    }

    /**
     * Gets the reference to <code>ratProbeCoExpression</code>'s DAO.
     */
    protected ubic.gemma.model.association.coexpression.RatProbeCoExpressionDao getRatProbeCoExpressionDao() {
        return this.ratProbeCoExpressionDao;
    }

    private ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionDao probe2ProbeCoexpressionDao;

    /**
     * Sets the reference to <code>probe2ProbeCoexpression</code>'s DAO.
     */
    public void setProbe2ProbeCoexpressionDao(
            ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionDao probe2ProbeCoexpressionDao ) {
        this.probe2ProbeCoexpressionDao = probe2ProbeCoexpressionDao;
    }

    /**
     * Gets the reference to <code>probe2ProbeCoexpression</code>'s DAO.
     */
    protected ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionDao getProbe2ProbeCoexpressionDao() {
        return this.probe2ProbeCoexpressionDao;
    }

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#create(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression)
     */
    public ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression create(
            final ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression p2pCoexpression ) {
        try {
            return this.handleCreate( p2pCoexpression );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.create(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression p2pCoexpression)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #create(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression)}
     */
    protected abstract ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression handleCreate(
            ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression p2pCoexpression )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#create(java.util.List)
     */
    public java.util.List create( final java.util.List p2pExpressions ) {
        try {
            return this.handleCreate( p2pExpressions );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.create(java.util.List p2pExpressions)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #create(java.util.List)}
     */
    protected abstract java.util.List handleCreate( java.util.List p2pExpressions ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#delete(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression)
     */
    public void delete( final ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression toDelete ) {
        try {
            this.handleDelete( toDelete );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.delete(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression toDelete)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #delete(ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression)}
     */
    protected abstract void handleDelete( ubic.gemma.model.association.coexpression.Probe2ProbeCoexpression toDelete )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#delete(java.util.Collection)
     */
    public void delete( final java.util.Collection deletes ) {
        try {
            this.handleDelete( deletes );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.delete(java.util.Collection deletes)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #delete(java.util.Collection)}
     */
    protected abstract void handleDelete( java.util.Collection deletes ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getVectorsForLinks(ubic.gemma.model.genome.Gene,
     *      java.util.Collection)
     */
    public java.util.Collection getVectorsForLinks( final ubic.gemma.model.genome.Gene gene,
            final java.util.Collection ees ) {
        try {
            return this.handleGetVectorsForLinks( gene, ees );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getVectorsForLinks(ubic.gemma.model.genome.Gene gene, java.util.Collection ees)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #getVectorsForLinks(ubic.gemma.model.genome.Gene, java.util.Collection)}
     */
    protected abstract java.util.Collection handleGetVectorsForLinks( ubic.gemma.model.genome.Gene gene,
            java.util.Collection ees ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#deleteLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    public void deleteLinks( final ubic.gemma.model.expression.experiment.ExpressionExperiment ee ) {
        try {
            this.handleDeleteLinks( ee );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.deleteLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment ee)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #deleteLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment)}
     */
    protected abstract void handleDeleteLinks( ubic.gemma.model.expression.experiment.ExpressionExperiment ee )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#countLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    public java.lang.Integer countLinks(
            final ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment ) {
        try {
            return this.handleCountLinks( expressionExperiment );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.countLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #countLinks(ubic.gemma.model.expression.experiment.ExpressionExperiment)}
     */
    protected abstract java.lang.Integer handleCountLinks(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getVectorsForLinks(java.util.Collection,
     *      java.util.Collection)
     */
    public java.util.Map getVectorsForLinks( final java.util.Collection genes, final java.util.Collection ees ) {
        try {
            return this.handleGetVectorsForLinks( genes, ees );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getVectorsForLinks(java.util.Collection genes, java.util.Collection ees)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #getVectorsForLinks(java.util.Collection, java.util.Collection)}
     */
    protected abstract java.util.Map handleGetVectorsForLinks( java.util.Collection genes, java.util.Collection ees )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#prepareForShuffling(java.util.Collection,
     *      java.lang.String, boolean)
     */
    public void prepareForShuffling( final java.util.Collection ees, final java.lang.String taxon,
            final boolean filterNonSpecific ) {
        try {
            this.handlePrepareForShuffling( ees, taxon, filterNonSpecific );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.prepareForShuffling(java.util.Collection ees, java.lang.String taxon, boolean filterNonSpecific)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for {@link #prepareForShuffling(java.util.Collection, java.lang.String, boolean)}
     */
    protected abstract void handlePrepareForShuffling( java.util.Collection ees, java.lang.String taxon,
            boolean filterNonSpecific ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getProbeCoExpression(ubic.gemma.model.expression.experiment.ExpressionExperiment,
     *      java.lang.String, boolean)
     */
    public java.util.Collection getProbeCoExpression(
            final ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment,
            final java.lang.String taxon, final boolean useWorkingTable ) {
        try {
            return this.handleGetProbeCoExpression( expressionExperiment, taxon, useWorkingTable );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getProbeCoExpression(ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment, java.lang.String taxon, boolean useWorkingTable)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #getProbeCoExpression(ubic.gemma.model.expression.experiment.ExpressionExperiment, java.lang.String, boolean)}
     */
    protected abstract java.util.Collection handleGetProbeCoExpression(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment, java.lang.String taxon,
            boolean useWorkingTable ) throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene,
     *      java.util.Collection, boolean)
     */
    public java.util.Collection getExpressionExperimentsLinkTestedIn( final ubic.gemma.model.genome.Gene gene,
            final java.util.Collection expressionExperiments, final boolean filterNonSpecific ) {
        try {
            return this.handleGetExpressionExperimentsLinkTestedIn( gene, expressionExperiments, filterNonSpecific );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene gene, java.util.Collection expressionExperiments, boolean filterNonSpecific)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene, java.util.Collection, boolean)}
     */
    protected abstract java.util.Collection handleGetExpressionExperimentsLinkTestedIn(
            ubic.gemma.model.genome.Gene gene, java.util.Collection expressionExperiments, boolean filterNonSpecific )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene,
     *      java.util.Collection, java.util.Collection, boolean)
     */
    public java.util.Map getExpressionExperimentsLinkTestedIn( final ubic.gemma.model.genome.Gene geneA,
            final java.util.Collection genesB, final java.util.Collection expressionExperiments,
            final boolean filterNonSpecific ) {
        try {
            return this.handleGetExpressionExperimentsLinkTestedIn( geneA, genesB, expressionExperiments,
                    filterNonSpecific );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene geneA, java.util.Collection genesB, java.util.Collection expressionExperiments, boolean filterNonSpecific)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #getExpressionExperimentsLinkTestedIn(ubic.gemma.model.genome.Gene, java.util.Collection, java.util.Collection, boolean)}
     */
    protected abstract java.util.Map handleGetExpressionExperimentsLinkTestedIn( ubic.gemma.model.genome.Gene geneA,
            java.util.Collection genesB, java.util.Collection expressionExperiments, boolean filterNonSpecific )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getGenesTestedBy(ubic.gemma.model.expression.experiment.BioAssaySet,
     *      boolean)
     */
    public java.util.Collection getGenesTestedBy(
            final ubic.gemma.model.expression.experiment.BioAssaySet expressionExperiment,
            final boolean filterNonSpecific ) {
        try {
            return this.handleGetGenesTestedBy( expressionExperiment, filterNonSpecific );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getGenesTestedBy(ubic.gemma.model.expression.experiment.BioAssaySet expressionExperiment, boolean filterNonSpecific)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #getGenesTestedBy(ubic.gemma.model.expression.experiment.BioAssaySet, boolean)}
     */
    protected abstract java.util.Collection handleGetGenesTestedBy(
            ubic.gemma.model.expression.experiment.BioAssaySet expressionExperiment, boolean filterNonSpecific )
            throws java.lang.Exception;

    /**
     * @see ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService#getExpressionExperimentsTestedIn(java.util.Collection,
     *      java.util.Collection, boolean)
     */
    public java.util.Map getExpressionExperimentsTestedIn( final java.util.Collection geneIds,
            final java.util.Collection experiments, final boolean filterNonSpecific ) {
        try {
            return this.handleGetExpressionExperimentsTestedIn( geneIds, experiments, filterNonSpecific );
        } catch ( Throwable th ) {
            throw new ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionServiceException(
                    "Error performing 'ubic.gemma.model.association.coexpression.Probe2ProbeCoexpressionService.getExpressionExperimentsTestedIn(java.util.Collection geneIds, java.util.Collection experiments, boolean filterNonSpecific)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #getExpressionExperimentsTestedIn(java.util.Collection, java.util.Collection, boolean)}
     */
    protected abstract java.util.Map handleGetExpressionExperimentsTestedIn( java.util.Collection geneIds,
            java.util.Collection experiments, boolean filterNonSpecific ) throws java.lang.Exception;

}