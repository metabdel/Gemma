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
 * @see ubic.gemma.model.genome.Qtl
 */
public interface BaseQtlDao<T extends Qtl> extends BaseDao<T> {
    /**
     * <p>
     * Does the same thing as
     * {@link #findByPhysicalMarkers(boolean, ubic.gemma.model.genome.PhysicalMarker, ubic.gemma.model.genome.PhysicalMarker)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByPhysicalMarkers(int, ubic.gemma.model.genome.PhysicalMarker
     * startMarker, ubic.gemma.model.genome.PhysicalMarker endMarker)}.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalMarkers( int transform, String queryString,
            ubic.gemma.model.genome.PhysicalMarker startMarker, ubic.gemma.model.genome.PhysicalMarker endMarker );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByPhysicalMarkers(ubic.gemma.model.genome.PhysicalMarker, ubic.gemma.model.genome.PhysicalMarker)}
     * with an additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then
     * finder results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other
     * constants defined here then finder results <strong>WILL BE</strong> passed through an operation which can
     * optionally transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalMarkers( int transform,
            ubic.gemma.model.genome.PhysicalMarker startMarker, ubic.gemma.model.genome.PhysicalMarker endMarker );

    /**
     * <p>
     * Does the same thing as
     * {@link #findByPhysicalMarkers(ubic.gemma.model.genome.PhysicalMarker, ubic.gemma.model.genome.PhysicalMarker)}
     * with an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in
     * {@link #findByPhysicalMarkers(ubic.gemma.model.genome.PhysicalMarker, ubic.gemma.model.genome.PhysicalMarker)}.
     * </p>
     */
    public java.util.Collection<T> findByPhysicalMarkers( String queryString,
            ubic.gemma.model.genome.PhysicalMarker startMarker, ubic.gemma.model.genome.PhysicalMarker endMarker );

    /**
     * 
     */
    public java.util.Collection<T> findByPhysicalMarkers( ubic.gemma.model.genome.PhysicalMarker startMarker,
            ubic.gemma.model.genome.PhysicalMarker endMarker );

}
