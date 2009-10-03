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
package ubic.gemma.model.common.auditAndSecurity;

import ubic.gemma.persistence.BaseDao;

/**
 * @see ubic.gemma.model.common.auditAndSecurity.Person
 */
public interface PersonDao extends BaseDao<Person> {
    /**
     * 
     */
    public ubic.gemma.model.common.auditAndSecurity.Person find( ubic.gemma.model.common.auditAndSecurity.Person person );

    /**
     * <p>
     * Does the same thing as {@link #findByFirstAndLastName(java.lang.String, java.lang.String)} with an additional
     * flag called <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<Person> findByFirstAndLastName( int transform, java.lang.String name,
            java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByFirstAndLastName(boolean, java.lang.String, java.lang.String)} with an
     * additional argument called <code>queryString</code>. This <code>queryString</code> argument allows you to
     * override the query string defined in {@link #findByFirstAndLastName(int, java.lang.String name, java.lang.String
     * secondName)}.
     * </p>
     */
    public java.util.Collection<Person> findByFirstAndLastName( int transform, String queryString,
            java.lang.String name, java.lang.String secondName );

    /**
     * 
     */
    public java.util.Collection<Person> findByFirstAndLastName( java.lang.String name, java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByFirstAndLastName(java.lang.String, java.lang.String)} with an additional
     * argument called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query
     * string defined in {@link #findByFirstAndLastName(java.lang.String, java.lang.String)}.
     * </p>
     */
    public java.util.Collection<Person> findByFirstAndLastName( String queryString, java.lang.String name,
            java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByFullName(java.lang.String, java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<Person> findByFullName( int transform, java.lang.String name,
            java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByFullName(boolean, java.lang.String, java.lang.String)} with an additional
     * argument called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query
     * string defined in {@link #findByFullName(int, java.lang.String name, java.lang.String secondName)}.
     * </p>
     */
    public java.util.Collection<Person> findByFullName( int transform, String queryString, java.lang.String name,
            java.lang.String secondName );

    /**
     * 
     */
    public java.util.Collection<Person> findByFullName( java.lang.String name, java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByFullName(java.lang.String, java.lang.String)} with an additional argument
     * called <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string
     * defined in {@link #findByFullName(java.lang.String, java.lang.String)}.
     * </p>
     */
    public java.util.Collection<Person> findByFullName( String queryString, java.lang.String name,
            java.lang.String secondName );

    /**
     * <p>
     * Does the same thing as {@link #findByLastName(java.lang.String)} with an additional flag called
     * <code>transform</code>. If this flag is set to <code>TRANSFORM_NONE</code> then finder results will
     * <strong>NOT</strong> be transformed during retrieval. If this flag is any of the other constants defined here
     * then finder results <strong>WILL BE</strong> passed through an operation which can optionally transform the
     * entities (into value objects for example). By default, transformation does not occur.
     * </p>
     */
    public java.util.Collection<Person> findByLastName( int transform, java.lang.String lastName );

    /**
     * <p>
     * Does the same thing as {@link #findByLastName(boolean, java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByLastName(int, java.lang.String lastName)}.
     * </p>
     */
    public java.util.Collection<Person> findByLastName( int transform, String queryString, java.lang.String lastName );

    /**
     * 
     */
    public java.util.Collection<Person> findByLastName( java.lang.String lastName );

    /**
     * <p>
     * Does the same thing as {@link #findByLastName(java.lang.String)} with an additional argument called
     * <code>queryString</code>. This <code>queryString</code> argument allows you to override the query string defined
     * in {@link #findByLastName(java.lang.String)}.
     * </p>
     */
    public java.util.Collection<Person> findByLastName( String queryString, java.lang.String lastName );

    /**
     * 
     */
    public ubic.gemma.model.common.auditAndSecurity.Person findOrCreate(
            ubic.gemma.model.common.auditAndSecurity.Person person );

}
