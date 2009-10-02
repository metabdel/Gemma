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

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.common.auditAndSecurity.UserRole</code>.
 * </p>
 * 
 * @see ubic.gemma.model.common.auditAndSecurity.UserRole
 */
public abstract class UserRoleDaoBase extends HibernateDaoSupport implements
        ubic.gemma.model.common.auditAndSecurity.UserRoleDao {

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#create(int, java.util.Collection)
     */
    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "UserRole.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform, ( ubic.gemma.model.common.auditAndSecurity.UserRole ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#create(int transform,
     *      ubic.gemma.model.common.auditAndSecurity.UserRole)
     */
    public Object create( final int transform, final ubic.gemma.model.common.auditAndSecurity.UserRole userRole ) {
        if ( userRole == null ) {
            throw new IllegalArgumentException( "UserRole.create - 'userRole' can not be null" );
        }
        this.getHibernateTemplate().save( userRole );
        return this.transformEntity( transform, userRole );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#create(java.util.Collection)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#create(ubic.gemma.model.common.auditAndSecurity.UserRole)
     */
    public UserRole create( ubic.gemma.model.common.auditAndSecurity.UserRole userRole ) {
        return ( ubic.gemma.model.common.auditAndSecurity.UserRole ) this.create( TRANSFORM_NONE, userRole );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByRoleName(int, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByRoleName( final int transform, final java.lang.String name ) {
        return this.findRolesByRoleName( transform, "from UserRoleImpl u where u.name=:name", name );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByRoleName(int, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByRoleName( final int transform, final java.lang.String queryString,
            final java.lang.String name ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( name );
        argNames.add( "name" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByRoleName(java.lang.String)
     */
    public java.util.Collection findRolesByRoleName( java.lang.String name ) {
        return this.findRolesByRoleName( TRANSFORM_NONE, name );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByRoleName(java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByRoleName( final java.lang.String queryString, final java.lang.String name ) {
        return this.findRolesByRoleName( TRANSFORM_NONE, queryString, name );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByUserName(int, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByUserName( final int transform, final java.lang.String userName ) {
        return this.findRolesByUserName( transform, "from RoleImpl r where r.userName=:userName", userName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByUserName(int, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByUserName( final int transform, final java.lang.String queryString,
            final java.lang.String userName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( userName );
        argNames.add( "userName" );
        java.util.List results = this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() );
        transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByUserName(java.lang.String)
     */
    public java.util.Collection findRolesByUserName( java.lang.String userName ) {
        return this.findRolesByUserName( TRANSFORM_NONE, userName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#findRolesByUserName(java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public java.util.Collection findRolesByUserName( final java.lang.String queryString, final java.lang.String userName ) {
        return this.findRolesByUserName( TRANSFORM_NONE, queryString, userName );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#load(int, java.lang.Long)
     */

    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "UserRole.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.common.auditAndSecurity.UserRoleImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.common.auditAndSecurity.UserRole ) entity );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#load(java.lang.Long)
     */

    public UserRole load( java.lang.Long id ) {
        return ( ubic.gemma.model.common.auditAndSecurity.UserRole ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#loadAll()
     */

    @SuppressWarnings( { "unchecked" })
    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#loadAll(int)
     */

    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.common.auditAndSecurity.UserRoleImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#remove(java.lang.Long)
     */

    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "UserRole.remove - 'id' can not be null" );
        }
        ubic.gemma.model.common.auditAndSecurity.UserRole entity = this.load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#remove(java.util.Collection)
     */

    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "UserRole.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#remove(ubic.gemma.model.common.auditAndSecurity.UserRole)
     */
    public void remove( ubic.gemma.model.common.auditAndSecurity.UserRole userRole ) {
        if ( userRole == null ) {
            throw new IllegalArgumentException( "UserRole.remove - 'userRole' can not be null" );
        }
        this.getHibernateTemplate().delete( userRole );
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */

    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "UserRole.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.common.auditAndSecurity.UserRole ) entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.common.auditAndSecurity.UserRoleDao#update(ubic.gemma.model.common.auditAndSecurity.UserRole)
     */
    public void update( ubic.gemma.model.common.auditAndSecurity.UserRole userRole ) {
        if ( userRole == null ) {
            throw new IllegalArgumentException( "UserRole.update - 'userRole' can not be null" );
        }
        this.getHibernateTemplate().update( userRole );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.common.auditAndSecurity.UserRole)} method. This method does not
     * instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.common.auditAndSecurity.UserRoleDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.common.auditAndSecurity.UserRole)
     */

    protected void transformEntities( final int transform, final java.util.Collection entities ) {
        switch ( transform ) {
            case TRANSFORM_NONE: // fall-through
            default:
                // do nothing;
        }
    }

    /**
     * Allows transformation of entities into value objects (or something else for that matter), when the
     * <code>transform</code> flag is set to one of the constants defined in
     * <code>ubic.gemma.model.common.auditAndSecurity.UserRoleDao</code>, please note that the {@link #TRANSFORM_NONE}
     * constant denotes no transformation, so the entity itself will be returned. If the integer argument value is
     * unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.common.auditAndSecurity.UserRoleDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.common.auditAndSecurity.UserRole entity ) {
        Object target = null;
        if ( entity != null ) {
            switch ( transform ) {
                case TRANSFORM_NONE: // fall-through
                default:
                    target = entity;
            }
        }
        return target;
    }

}