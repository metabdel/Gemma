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
package ubic.gemma.model.expression.designElement;

import ubic.gemma.model.genome.Gene;

/**
 * @see ubic.gemma.model.expression.designElement.CompositeSequence
 */
public interface CompositeSequenceDao extends
        ubic.gemma.model.expression.designElement.DesignElementDao<CompositeSequence> {
    /**
     * 
     */
    public java.lang.Integer countAll();

    /**
     * <p>
     * Does the same thing as {@link #find(boolean, ubic.gemma.model.expression.designElement.CompositeSequence)} with
     * an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #find(int,
     * ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence)}.
     * </p>
     */
    public CompositeSequence find( int transform, String queryString,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.expression.designElement.CompositeSequence)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public CompositeSequence find( int transform,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.expression.designElement.CompositeSequence)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #find(ubic.gemma.model.expression.designElement.CompositeSequence)}.
     * </p>
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence find( String queryString,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * 
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence find(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * 
     */
    public java.util.Collection<CompositeSequence> findByBioSequence(
            ubic.gemma.model.genome.biosequence.BioSequence bioSequence );

    /**
     * 
     */
    public java.util.Collection<CompositeSequence> findByBioSequenceName( java.lang.String name );

    /**
     * <p>
     * Does the same thing as {@link #findByGene(boolean, ubic.gemma.model.genome.Gene)} with an additional argument
     * called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #findByGene(int, ubic.gemma.model.genome.Gene gene)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( int transform, String queryString,
            ubic.gemma.model.genome.Gene gene );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByGene(boolean, ubic.gemma.model.genome.Gene, ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByGene(int, ubic.gemma.model.genome.Gene gene,
     * ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( int transform, String queryString,
            ubic.gemma.model.genome.Gene gene, ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * <p>
     * Does the same thing as {@link #findByGene(ubic.gemma.model.genome.Gene)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( int transform, ubic.gemma.model.genome.Gene gene );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByGene(ubic.gemma.model.genome.Gene, ubic.gemma.model.expression.arrayDesign.ArrayDesign)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( int transform, ubic.gemma.model.genome.Gene gene,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * <p>
     * Does the same thing as {@link #findByGene(ubic.gemma.model.genome.Gene)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByGene(ubic.gemma.model.genome.Gene)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( String queryString, ubic.gemma.model.genome.Gene gene );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByGene(ubic.gemma.model.genome.Gene, ubic.gemma.model.expression.arrayDesign.ArrayDesign)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #findByGene(ubic.gemma.model.genome.Gene, ubic.gemma.model.expression.arrayDesign.ArrayDesign)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByGene( String queryString, ubic.gemma.model.genome.Gene gene,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * 
     */
    public java.util.Collection<CompositeSequence> findByGene( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public java.util.Collection<CompositeSequence> findByGene( ubic.gemma.model.genome.Gene gene,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * <p>
     * Does the same thing as {@link #findByName(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByName( int transform, java.lang.String name );

    /**
     * <p>
     * Does the same thing as {@link #findByName(boolean, java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByName(int, java.lang.String name)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByName( int transform, String queryString, java.lang.String name );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByName(boolean, ubic.gemma.model.expression.arrayDesign.ArrayDesign, java.lang.String)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByName(int, ubic.gemma.model.expression.arrayDesign.ArrayDesign
     * arrayDesign, java.lang.String name)}.
     * </p>
     */
    public CompositeSequence findByName( int transform, String queryString,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign, java.lang.String name );

    /**
     * <p>
     * Does the same thing as {@link #findByName(ubic.gemma.model.expression.arrayDesign.ArrayDesign, java.lang.String)}
     * with an additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then
     * finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public CompositeSequence findByName( int transform,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign, java.lang.String name );

    /**
     * 
     */
    public java.util.Collection<CompositeSequence> findByName( java.lang.String name );

    /**
     * <p>
     * Does the same thing as {@link #findByName(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByName(java.lang.String)}.
     * </p>
     */
    public java.util.Collection<CompositeSequence> findByName( String queryString, java.lang.String name );

    /**
     * <p>
     * Does the same thing as {@link #findByName(ubic.gemma.model.expression.arrayDesign.ArrayDesign, java.lang.String)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #findByName(ubic.gemma.model.expression.arrayDesign.ArrayDesign, java.lang.String)}.
     * </p>
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence findByName( String queryString,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign, java.lang.String name );

    /**
     * 
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence findByName(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign, java.lang.String name );

    /**
     * <p>
     * Does the same thing as
     * {@link #findOrCreate(boolean, ubic.gemma.model.expression.designElement.CompositeSequence)} with an additional
     * argument called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query
     * string defined in {@link #findOrCreate(int, ubic.gemma.model.expression.designElement.CompositeSequence
     * compositeSequence)}.
     * </p>
     */
    public CompositeSequence findOrCreate( int transform, String queryString,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.expression.designElement.CompositeSequence)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public CompositeSequence findOrCreate( int transform,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.expression.designElement.CompositeSequence)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #findOrCreate(ubic.gemma.model.expression.designElement.CompositeSequence)}.
     * </p>
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence findOrCreate( String queryString,
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * 
     */
    public ubic.gemma.model.expression.designElement.CompositeSequence findOrCreate(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Given a collection of composite sequences returns a map of the given composite sequences to a collection of genes
     * </p>
     */
    public java.util.Map getGenes( java.util.Collection<CompositeSequence> compositeSequences );

    /**
     * <p>
     * given a composite sequence returns a collection of genes
     * </p>
     */
    public java.util.Collection<Gene> getGenes(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence );

    /**
     * <p>
     * Returns a map of CompositeSequences to PhysicalLocation to BlatAssociations at each location.
     * </p>
     */
    public java.util.Map getGenesWithSpecificity( java.util.Collection<CompositeSequence> compositeSequences );

    /**
     * 
     */
    public java.util.Collection getRawSummary( java.util.Collection<CompositeSequence> compositeSequences,
            java.lang.Integer numResults );

    /**
     * 
     */
    public java.util.Collection getRawSummary( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign,
            java.lang.Integer numResults );

    /**
     * <p>
     * See ArrayDesignDao.getRawCompositeSequenceSummary.
     * </p>
     */
    public java.util.Collection getRawSummary(
            ubic.gemma.model.expression.designElement.CompositeSequence compositeSequence, java.lang.Integer numResults );

    /**
     * 
     */
    public void thaw( java.util.Collection<CompositeSequence> compositeSequences );

}
