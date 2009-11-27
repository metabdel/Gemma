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
package ubic.gemma.model.expression.bioAssayData;

import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.expression.bioAssayData.DesignElementDimension</code>.
 * </p>
 * 
 * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimension
 */
public abstract class DesignElementDimensionDaoBase extends HibernateDaoSupport implements
        ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao {

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#create(int, java.util.Collection)
     */
    public java.util.Collection create( final int transform, final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDimension.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback<Object>() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform,
                                    ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) entityIterator
                                            .next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#create(int transform,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public Object create( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        if ( designElementDimension == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDimension.create - 'designElementDimension' can not be null" );
        }
        this.getHibernateTemplate().save( designElementDimension );
        return this.transformEntity( transform, designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#create(java.util.Collection)
     */

    public java.util.Collection create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#create(ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public DesignElementDimension create(
            ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.create( TRANSFORM_NONE,
                designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#find(int, java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public Object find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( designElementDimension );
        argNames.add( "designElementDimension" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.bioAssayData.DesignElementDimension"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#find(int,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public Object find( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return this
                .find(
                        transform,
                        "from ubic.gemma.model.expression.bioAssayData.DesignElementDimension as designElementDimension where designElementDimension.designElementDimension = :designElementDimension",
                        designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#find(java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public ubic.gemma.model.expression.bioAssayData.DesignElementDimension find( final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.find( TRANSFORM_NONE,
                queryString, designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#find(ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDimension find(
            ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.find( TRANSFORM_NONE,
                designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( designElementDimension );
        argNames.add( "designElementDimension" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.bioAssayData.DesignElementDimension"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#findOrCreate(int,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public Object findOrCreate( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return this
                .findOrCreate(
                        transform,
                        "from ubic.gemma.model.expression.bioAssayData.DesignElementDimension as designElementDimension where designElementDimension.designElementDimension = :designElementDimension",
                        designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */

    public ubic.gemma.model.expression.bioAssayData.DesignElementDimension findOrCreate(
            final java.lang.String queryString,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.findOrCreate( TRANSFORM_NONE,
                queryString, designElementDimension );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#findOrCreate(ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public ubic.gemma.model.expression.bioAssayData.DesignElementDimension findOrCreate(
            ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.findOrCreate( TRANSFORM_NONE,
                designElementDimension );
    }

    public Collection<? extends DesignElementDimension> load( Collection<Long> ids ) {
        return this.getHibernateTemplate().findByNamedParam( "from DesignElementDimensionImpl where id in (:ids)",
                "ids", ids );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#load(int, java.lang.Long)
     */

    public Object load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DesignElementDimension.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.expression.bioAssayData.DesignElementDimensionImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) entity );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#load(java.lang.Long)
     */

    public DesignElementDimension load( java.lang.Long id ) {
        return ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#loadAll()
     */

    public java.util.Collection loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#loadAll(int)
     */

    public java.util.Collection loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.expression.bioAssayData.DesignElementDimensionImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#remove(java.lang.Long)
     */

    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "DesignElementDimension.remove - 'id' can not be null" );
        }
        ubic.gemma.model.expression.bioAssayData.DesignElementDimension entity = this.load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#remove(java.util.Collection)
     */

    public void remove( java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDimension.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#remove(ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public void remove( ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        if ( designElementDimension == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDimension.remove - 'designElementDimension' can not be null" );
        }
        this.getHibernateTemplate().delete( designElementDimension );
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */

    public void update( final java.util.Collection entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "DesignElementDimension.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback<Object>() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( ( ubic.gemma.model.expression.bioAssayData.DesignElementDimension ) entityIterator
                                    .next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao#update(ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
     */
    public void update( ubic.gemma.model.expression.bioAssayData.DesignElementDimension designElementDimension ) {
        if ( designElementDimension == null ) {
            throw new IllegalArgumentException(
                    "DesignElementDimension.update - 'designElementDimension' can not be null" );
        }
        this.getHibernateTemplate().update( designElementDimension );
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DesignElementDimension)} method. This method
     * does not instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.expression.bioAssayData.DesignElementDimension)
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
     * <code>ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. If the integer
     * argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in
     *        {@link ubic.gemma.model.expression.bioAssayData.DesignElementDimensionDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.expression.bioAssayData.DesignElementDimension entity ) {
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