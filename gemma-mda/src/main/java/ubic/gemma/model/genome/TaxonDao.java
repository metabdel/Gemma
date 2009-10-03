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
package ubic.gemma.model.genome;

/**
 * @see ubic.gemma.model.genome.Taxon
 */
public interface TaxonDao {
    /**
     * This constant is used as a transformation flag; entities can be converted automatically into value objects or
     * other types, different methods in a class implementing this interface support this feature: look for an
     * <code>int</code> parameter called <code>transform</code>.
     * <p/>
     * This specific flag denotes no transformation will occur.
     */
    public final static int TRANSFORM_NONE = 0;

    /**
     * <p>
     * Does the same thing as {@link #create(ubic.gemma.model.genome.Taxon)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then the returned entity will
     * <strong>NOT</strong> be transformed. If this flag is any of the other constants defined here then the result
     * <strong>WILL BE</strong> passed through an operation which can optionally transform the entities (into value
     * objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<Taxon> create( int transform, java.util.Collection<Taxon> entities );

    /**
     * <p>
     * Does the same thing as {@link #create(ubic.gemma.model.genome.Taxon)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then the returned entity will
     * <strong>NOT</strong> be transformed. If this flag is any of the other constants defined here then the result
     * <strong>WILL BE</strong> passed through an operation which can optionally transform the entity (into a value
     * object for example). By default, transformation does not occur.
     * </p>
     */
    public Taxon create( int transform, ubic.gemma.model.genome.Taxon taxon );

    /**
     * Creates a new instance of ubic.gemma.model.genome.Taxon and adds from the passed in <code>entities</code>
     * collection
     * 
     * @param entities the collection of ubic.gemma.model.genome.Taxon instances to create.
     * @return the created instances.
     */
    public java.util.Collection<Taxon> create( java.util.Collection<Taxon> entities );

    /**
     * Creates an instance of ubic.gemma.model.genome.Taxon and adds it to the persistent store.
     */
    public ubic.gemma.model.genome.Taxon create( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #find(boolean, ubic.gemma.model.genome.Taxon)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #find(int, ubic.gemma.model.genome.Taxon taxon)}.
     * </p>
     */
    public Object find( int transform, String queryString, ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.genome.Taxon)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Taxon find( int transform, ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #find(ubic.gemma.model.genome.Taxon)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #find(ubic.gemma.model.genome.Taxon)}.
     * </p>
     */
    public ubic.gemma.model.genome.Taxon find( String queryString, ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon find( ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #findByCommonName(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Taxon findByCommonName( int transform, java.lang.String commonName );

    /**
     * <p>
     * Does the same thing as {@link #findByCommonName(boolean, java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByCommonName(int, java.lang.String commonName)}.
     * </p>
     */
    public Taxon findByCommonName( int transform, String queryString, java.lang.String commonName );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findByCommonName( java.lang.String commonName );

    /**
     * <p>
     * Does the same thing as {@link #findByCommonName(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByCommonName(java.lang.String)}.
     * </p>
     */
    public ubic.gemma.model.genome.Taxon findByCommonName( String queryString, java.lang.String commonName );

    /**
     * <p>
     * Does the same thing as {@link #findByScientificName(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Taxon findByScientificName( int transform, java.lang.String scientificName );

    /**
     * <p>
     * Does the same thing as {@link #findByScientificName(boolean, java.lang.String)} with an additional argument
     * called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #findByScientificName(int, java.lang.String scientificName)}.
     * </p>
     */
    public Taxon findByScientificName( int transform, String queryString, java.lang.String scientificName );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findByScientificName( java.lang.String scientificName );

    /**
     * <p>
     * Does the same thing as {@link #findByScientificName(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByScientificName(java.lang.String)}.
     * </p>
     */
    public ubic.gemma.model.genome.Taxon findByScientificName( String queryString, java.lang.String scientificName );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(boolean, ubic.gemma.model.genome.Taxon)} with an additional argument
     * called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #findOrCreate(int, ubic.gemma.model.genome.Taxon taxon)}.
     * </p>
     */
    public Taxon findOrCreate( int transform, String queryString, ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.genome.Taxon)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public Taxon findOrCreate( int transform, ubic.gemma.model.genome.Taxon taxon );

    /**
     * <p>
     * Does the same thing as {@link #findOrCreate(ubic.gemma.model.genome.Taxon)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findOrCreate(ubic.gemma.model.genome.Taxon)}.
     * </p>
     */
    public ubic.gemma.model.genome.Taxon findOrCreate( String queryString, ubic.gemma.model.genome.Taxon taxon );

    /**
     * 
     */
    public ubic.gemma.model.genome.Taxon findOrCreate( ubic.gemma.model.genome.Taxon taxon );

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
    public Taxon load( int transform, java.lang.Long id );

    /**
     * Loads an instance of ubic.gemma.model.genome.Taxon from the persistent store.
     */
    public ubic.gemma.model.genome.Taxon load( java.lang.Long id );

    /**
     * Loads all entities of type {@link ubic.gemma.model.genome.Taxon}.
     * 
     * @return the loaded entities.
     */
    public java.util.Collection<Taxon> loadAll();

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
    public java.util.Collection<Taxon> loadAll( final int transform );

    /**
     * Removes the instance of ubic.gemma.model.genome.Taxon having the given <code>identifier</code> from the
     * persistent store.
     */
    public void remove( java.lang.Long id );

    /**
     * Removes all entities in the given <code>entities<code> collection.
     */
    public void remove( java.util.Collection<Taxon> entities );

    /**
     * Removes the instance of ubic.gemma.model.genome.Taxon from the persistent store.
     */
    public void remove( ubic.gemma.model.genome.Taxon taxon );

    /**
     * Updates all instances in the <code>entities</code> collection in the persistent store.
     */
    public void update( java.util.Collection<Taxon> entities );

    /**
     * Updates the <code>taxon</code> instance in the persistent store.
     */
    public void update( ubic.gemma.model.genome.Taxon taxon );

}
