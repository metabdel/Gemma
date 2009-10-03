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
package ubic.gemma.model.genome.gene;

import ubic.gemma.persistence.BaseDao;

/**
 * @see ubic.gemma.model.genome.gene.CandidateGeneList
 */
public interface CandidateGeneListDao extends BaseDao<CandidateGeneList> {
    /**
     * <p>
     * Does the same thing as {@link #findByContributer(boolean, ubic.gemma.model.common.auditAndSecurity.Person)} with
     * an additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByContributer(int,
     * ubic.gemma.model.common.auditAndSecurity.Person owner)}.
     * </p>
     */
    public java.util.Collection findByContributer( int transform, String queryString,
            ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * <p>
     * Does the same thing as {@link #findByContributer(ubic.gemma.model.common.auditAndSecurity.Person)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection findByContributer( int transform, ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * <p>
     * Does the same thing as {@link #findByContributer(ubic.gemma.model.common.auditAndSecurity.Person)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByContributer(ubic.gemma.model.common.auditAndSecurity.Person)}.
     * </p>
     */
    public java.util.Collection findByContributer( String queryString,
            ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * 
     */
    public java.util.Collection findByContributer( ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * <p>
     * Does the same thing as {@link #findByGeneOfficialName(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection findByGeneOfficialName( int transform, java.lang.String officialName );

    /**
     * <p>
     * Does the same thing as {@link #findByGeneOfficialName(boolean, java.lang.String)} with an additional argument
     * called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #findByGeneOfficialName(int, java.lang.String officialName)}.
     * </p>
     */
    public java.util.Collection findByGeneOfficialName( int transform, String queryString, java.lang.String officialName );

    /**
     * 
     */
    public java.util.Collection findByGeneOfficialName( java.lang.String officialName );

    /**
     * <p>
     * Does the same thing as {@link #findByGeneOfficialName(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByGeneOfficialName(java.lang.String)}.
     * </p>
     */
    public java.util.Collection findByGeneOfficialName( String queryString, java.lang.String officialName );

    /**
     * <p>
     * Does the same thing as {@link #findByID(java.lang.Long)} with an additional flag called <code>transform</code>.
     * If this flag is set to <code>TRANSFORM_NONE</code> then finder results will <strong>NOT</strong> be transformed
     * during retrieval. If this flag is any of the other constants defined here then finder results <strong>WILL
     * BE</strong> passed through an operation which can optionally transform the entities (into value objects for
     * example). By default, transformation does not occur.
     * </p>
     */
    public Object findByID( int transform, java.lang.Long id );

    /**
     * <p>
     * Does the same thing as {@link #findByID(boolean, java.lang.Long)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByID(int, java.lang.Long id)}.
     * </p>
     */
    public Object findByID( int transform, String queryString, java.lang.Long id );

    /**
     * 
     */
    public ubic.gemma.model.genome.gene.CandidateGeneList findByID( java.lang.Long id );

    /**
     * <p>
     * Does the same thing as {@link #findByID(java.lang.Long)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByID(java.lang.Long)}.
     * </p>
     */
    public ubic.gemma.model.genome.gene.CandidateGeneList findByID( String queryString, java.lang.Long id );

    /**
     * <p>
     * Does the same thing as {@link #findByListOwner(boolean, ubic.gemma.model.common.auditAndSecurity.Person)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByListOwner(int, ubic.gemma.model.common.auditAndSecurity.Person
     * owner)}.
     * </p>
     */
    public java.util.Collection findByListOwner( int transform, String queryString,
            ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * <p>
     * Does the same thing as {@link #findByListOwner(ubic.gemma.model.common.auditAndSecurity.Person)} with an
     * additional flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder
     * results will <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants
     * defined here then finder results <strong>WILL BE</strong> passed through an operation which can optionally
     * transform the entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection findByListOwner( int transform, ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * <p>
     * Does the same thing as {@link #findByListOwner(ubic.gemma.model.common.auditAndSecurity.Person)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByListOwner(ubic.gemma.model.common.auditAndSecurity.Person)}.
     * </p>
     */
    public java.util.Collection findByListOwner( String queryString,
            ubic.gemma.model.common.auditAndSecurity.Person owner );

    /**
     * 
     */
    public java.util.Collection findByListOwner( ubic.gemma.model.common.auditAndSecurity.Person owner );

}
