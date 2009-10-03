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

import ubic.gemma.model.analysis.expression.coexpression.GeneCoexpressionAnalysis;
import ubic.gemma.model.genome.Gene;

/**
 * @see ubic.gemma.model.association.coexpression.Gene2GeneCoexpression
 */
public interface Gene2GeneCoexpressionDao extends ubic.gemma.model.association.Gene2GeneAssociationDao {
    /**
     * Loads an instance of ubic.gemma.model.association.coexpression.Gene2GeneCoexpression from the persistent store.
     */
    public Gene2GeneCoexpression load( java.lang.Long id );

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
     * Loads all entities of type {@link ubic.gemma.model.association.coexpression.Gene2GeneCoexpression}.
     * 
     * @return the loaded entities.
     */
    public java.util.Collection<Gene2GeneCoexpression> loadAll();

    /**
     * <p>
     * Does the same thing as {@link #loadAll()} with an additional flag called <code>transform</code>. If this flag is
     * set to <code>TRANSFORM_NONE</code> then the returned entity will <strong>NOT</strong> be transformed. If this
     * flag is any of the other constants defined here then the result <strong>WILL BE</strong> passed through an
     * operation which can optionally transform the entity (into a value object for example). By default, transformation
     * does not occur.
     * </p>
     * 
     * @param transform the flag indicating what transformation to use.
     * @return the loaded entities.
     */
    public java.util.Collection<Gene2GeneCoexpression> loadAll( final int transform );

    /**
     * Updates the <code>gene2GeneCoexpression</code> instance in the persistent store.
     */
    public void update( ubic.gemma.model.association.coexpression.Gene2GeneCoexpression gene2GeneCoexpression );

    /**
     * Updates all instances in the <code>entities</code> collection in the persistent store.
     */
    public void update( java.util.Collection<Gene2GeneCoexpression> entities );

    /**
     * Removes the instance of ubic.gemma.model.association.coexpression.Gene2GeneCoexpression from the persistent
     * store.
     */
    public void remove( ubic.gemma.model.association.coexpression.Gene2GeneCoexpression gene2GeneCoexpression );

    /**
     * Removes the instance of ubic.gemma.model.association.coexpression.Gene2GeneCoexpression having the given
     * <code>identifier</code> from the persistent store.
     */
    public void remove( java.lang.Long id );

    /**
     * Removes all entities in the given <code>entities<code> collection.
     */
    public void remove( java.util.Collection<Gene2GeneCoexpression> entities );

    /**
     * <p>
     * Returns a collection of gene2geneCoexpression objects. Set maxResults to 0 to remove limits.
     * </p>
     */
    public java.util.Collection<Gene2GeneCoexpression> findCoexpressionRelationships(
            ubic.gemma.model.genome.Gene gene, int stringency, int maxResults, GeneCoexpressionAnalysis sourceAnalysis );

    /**
     * <p>
     * Returns a map of genes to coexpression results. Set maxResults to 0 to remove limits.
     * </p>
     */
    public java.util.Map findCoexpressionRelationships( java.util.Collection<Gene> genes, int stringency,
            int maxResults, GeneCoexpressionAnalysis sourceAnalysis );

    /**
     * <p>
     * Return coexpression relationships among the given genes.
     * </p>
     */
    public java.util.Map findInterCoexpressionRelationships( java.util.Collection<Gene> genes, int stringency,
            GeneCoexpressionAnalysis sourceAnalysis );

}
