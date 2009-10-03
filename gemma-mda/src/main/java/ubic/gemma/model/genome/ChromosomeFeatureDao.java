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

import ubic.gemma.persistence.BaseDao;

/**
 * @see ubic.gemma.model.genome.ChromosomeFeature
 */
public interface ChromosomeFeatureDao<T extends ChromosomeFeature> extends BaseDao<T> {
    /**
     * <p>
     * Does the same thing as {@link #findByNcbiId(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<T> findByNcbiId( int transform, java.lang.String ncbiId );

    /**
     * <p>
     * Does the same thing as {@link #findByNcbiId(boolean, java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByNcbiId(int, java.lang.String ncbiId)}.
     * </p>
     */
    public java.util.Collection<T> findByNcbiId( int transform, String queryString, java.lang.String ncbiId );

    /**
     * 
     */
    public java.util.Collection<T> findByNcbiId( java.lang.String ncbiId );

    /**
     * <p>
     * Does the same thing as {@link #findByNcbiId(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByNcbiId(java.lang.String)}.
     * </p>
     */
    public java.util.Collection<T> findByNcbiId( String queryString, java.lang.String ncbiId );

    /**
     * <p>
     * Does the same thing as {@link #findByPhysicalLocation(boolean, ubic.gemma.model.genome.PhysicalLocation)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByPhysicalLocation(int, ubic.gemma.model.genome.PhysicalLocation
     * location)}.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalLocation( int transform, String queryString,
            ubic.gemma.model.genome.PhysicalLocation location );

    /**
     * <p>
     * Does the same thing as {@link #findByPhysicalLocation(ubic.gemma.model.genome.PhysicalLocation)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalLocation( int transform,
            ubic.gemma.model.genome.PhysicalLocation location );

    /**
     * <p>
     * Does the same thing as {@link #findByPhysicalLocation(ubic.gemma.model.genome.PhysicalLocation)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByPhysicalLocation(ubic.gemma.model.genome.PhysicalLocation)}.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalLocation( String queryString,
            ubic.gemma.model.genome.PhysicalLocation location );

    /**
     * <p>
     * Find chromosome features that fall within the physical location.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalLocation( ubic.gemma.model.genome.PhysicalLocation location );

}
