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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysis;
import ubic.gemma.model.genome.Gene;

/**
 * Spring Service base class for <code>ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService</code>,
 * provides access to all services and entities referenced by this service.
 * 
 * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService
 */
public abstract class Gene2GeneCoexpressionServiceBase implements
        ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService {

    @Autowired
    Gene2GeneCoexpressionDao gene2GeneCoexpressionDao;

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#create(java.util.Collection)
     */
    @Override
    public java.util.Collection<Gene2GeneCoexpression> create(
            final java.util.Collection<Gene2GeneCoexpression> gene2geneCoexpressions ) {
        return this.handleCreate( gene2geneCoexpressions );
    }

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#create(ubic.gemma.model.association.coexpression.Gene2GeneCoexpression)
     */
    @Override
    public ubic.gemma.model.association.coexpression.Gene2GeneCoexpression create(
            final ubic.gemma.model.association.coexpression.Gene2GeneCoexpression gene2gene ) {
        return this.handleCreate( gene2gene );
    }

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#delete(ubic.gemma.model.association.coexpression.Gene2GeneCoexpression)
     */
    @Override
    public void delete( final ubic.gemma.model.association.coexpression.Gene2GeneCoexpression toDelete ) {
        this.handleDelete( toDelete );
    }

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#findCoexpressionRelationships(java.util.Collection,
     *      int, int)
     */
    @Override
    public Map<Gene, Collection<Gene2GeneCoexpression>> findCoexpressionRelationships(
            final java.util.Collection<Gene> genes, final int stringency, final int maxResults,
            GeneCoexpressionAnalysis sourceAnalysis ) {
        return this.handleFindCoexpressionRelationships( genes, stringency, maxResults, sourceAnalysis );

    }

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#findCoexpressionRelationships(ubic.gemma.model.genome.Gene,
     *      int, int)
     */
    @Override
    public java.util.Collection<Gene2GeneCoexpression> findCoexpressionRelationships(
            final ubic.gemma.model.genome.Gene gene, final int stringency, final int maxResults,
            GeneCoexpressionAnalysis sourceAnalysis ) {
        return this.handleFindCoexpressionRelationships( gene, stringency, maxResults, sourceAnalysis );
    }

    /**
     * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpressionService#findInterCoexpressionRelationship(java.util.Collection,
     *      int)
     */
    @Override
    public Map<Gene, Collection<Gene2GeneCoexpression>> findInterCoexpressionRelationship(
            final Collection<Gene> genes, final int stringency, GeneCoexpressionAnalysis sourceAnalysis ) {
        return this.handleFindInterCoexpressionRelationship( genes, stringency, sourceAnalysis );
    }

    /**
     * @return the gene2GeneCoexpressionDao
     */
    public Gene2GeneCoexpressionDao getGene2GeneCoexpressionDao() {
        return gene2GeneCoexpressionDao;
    }

    /**
     * @param gene2GeneCoexpressionDao the gene2GeneCoexpressionDao to set
     */
    public void setGene2GeneCoexpressionDao( Gene2GeneCoexpressionDao gene2GeneCoexpressionDao ) {
        this.gene2GeneCoexpressionDao = gene2GeneCoexpressionDao;
    }

    /**
     * Performs the core logic for {@link #create(java.util.Collection)}
     */
    protected abstract Collection<Gene2GeneCoexpression> handleCreate(
            Collection<Gene2GeneCoexpression> gene2geneCoexpressions );

    /**
     * Performs the core logic for {@link #create(ubic.gemma.model.association.coexpression.Gene2GeneCoexpression)}
     */
    protected abstract Gene2GeneCoexpression handleCreate( Gene2GeneCoexpression gene2gene );

    /**
     * Performs the core logic for {@link #delete(ubic.gemma.model.association.coexpression.Gene2GeneCoexpression)}
     */
    protected abstract void handleDelete( Gene2GeneCoexpression toDelete );

    /**
     * Performs the core logic for {@link #findCoexpressionRelationships(java.util.Collection, int, int)}
     */
    protected abstract Map<Gene, Collection<Gene2GeneCoexpression>> handleFindCoexpressionRelationships(
            Collection<Gene> genes, int stringency, int maxResults, GeneCoexpressionAnalysis sourceAnalysis );

    /**
     * Performs the core logic for {@link #findCoexpressionRelationships(ubic.gemma.model.genome.Gene, int, int)}
     */
    protected abstract Collection<Gene2GeneCoexpression> handleFindCoexpressionRelationships( Gene gene,
            int stringency, int maxResults, GeneCoexpressionAnalysis sourceAnalysis );

    /**
     * Performs the core logic for {@link #findInterCoexpressionRelationship(java.util.Collection, int)}
     */
    protected abstract Map<Gene, Collection<Gene2GeneCoexpression>> handleFindInterCoexpressionRelationship(
            java.util.Collection<Gene> genes, int stringency, GeneCoexpressionAnalysis sourceAnalysis );

}