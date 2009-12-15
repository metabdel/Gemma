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

import java.util.Collection;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.genome.Taxon</code>.
 * </p>
 * 
 * @see ubic.gemma.model.genome.Taxon
 */
public abstract class TaxonDaoBase extends org.springframework.orm.hibernate3.support.HibernateDaoSupport implements
        ubic.gemma.model.genome.TaxonDao {

    /**
     * @see ubic.gemma.model.genome.TaxonDao#create(int, java.util.Collection)
     */
    public java.util.Collection<Taxon> create( final int transform, final java.util.Collection<Taxon> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Taxon.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback<Object>() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator<Taxon> entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            create( transform, entityIterator.next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#create(int transform, ubic.gemma.model.genome.Taxon)
     */
    public Taxon create( final int transform, final ubic.gemma.model.genome.Taxon taxon ) {
        if ( taxon == null ) {
            throw new IllegalArgumentException( "Taxon.create - 'taxon' can not be null" );
        }
        this.getHibernateTemplate().save( taxon );
        return this.transformEntity( transform, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#create(java.util.Collection)
     */

    public java.util.Collection<Taxon> create( final java.util.Collection entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#create(ubic.gemma.model.genome.Taxon)
     */
    public ubic.gemma.model.genome.Taxon create( ubic.gemma.model.genome.Taxon taxon ) {
        return this.create( TRANSFORM_NONE, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#find(int, java.lang.String, ubic.gemma.model.genome.Taxon)
     */

    public Taxon find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.genome.Taxon taxon ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( taxon );
        argNames.add( "taxon" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.genome.Taxon"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }
        result = transformEntity( transform, ( ubic.gemma.model.genome.Taxon ) result );
        return ( Taxon ) result;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#find(int, ubic.gemma.model.genome.Taxon)
     */
    public Taxon find( final int transform, final ubic.gemma.model.genome.Taxon taxon ) {
        return this.find( transform, "from ubic.gemma.model.genome.Taxon as taxon where taxon.taxon = :taxon", taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#find(java.lang.String, ubic.gemma.model.genome.Taxon)
     */
    public ubic.gemma.model.genome.Taxon find( final java.lang.String queryString,
            final ubic.gemma.model.genome.Taxon taxon ) {
        return this.find( TRANSFORM_NONE, queryString, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#find(ubic.gemma.model.genome.Taxon)
     */
    public ubic.gemma.model.genome.Taxon find( ubic.gemma.model.genome.Taxon taxon ) {
        return this.find( TRANSFORM_NONE, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByAbbreviation(java.lang.String abbreviation)
     */
    public Taxon findByAbbreviation( final java.lang.String abbreviation ) {
        try {
            return this.handleFindByAbbreviation( abbreviation );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.genome.TaxonDaoBase.findByAbbreviation(java.lang.String abbreviation)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByCommonName(int, java.lang.String)
     */
    public Taxon findByCommonName( final int transform, final java.lang.String commonName ) {
        return this.findByCommonName( transform, "from TaxonImpl t where t.commonName=:commonName", commonName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByCommonName(int, java.lang.String, java.lang.String)
     */

    public Taxon findByCommonName( final int transform, final java.lang.String queryString,
            final java.lang.String commonName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( commonName );
        argNames.add( "commonName" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.genome.Taxon"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.genome.Taxon ) result );
        return ( Taxon ) result;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByCommonName(java.lang.String)
     */
    public ubic.gemma.model.genome.Taxon findByCommonName( java.lang.String commonName ) {
        return this.findByCommonName( TRANSFORM_NONE, commonName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByCommonName(java.lang.String, java.lang.String)
     */
    public ubic.gemma.model.genome.Taxon findByCommonName( final java.lang.String queryString,
            final java.lang.String commonName ) {
        return this.findByCommonName( TRANSFORM_NONE, queryString, commonName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByScientificName(int, java.lang.String)
     */
    public Taxon findByScientificName( final int transform, final java.lang.String scientificName ) {
        return this.findByScientificName( transform, "from TaxonImpl t where t.scientificName=:scientificName ",
                scientificName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByScientificName(int, java.lang.String, java.lang.String)
     */

    public Taxon findByScientificName( final int transform, final java.lang.String queryString,
            final java.lang.String scientificName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( scientificName );
        argNames.add( "scientificName" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.genome.Taxon"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.genome.Taxon ) result );
        return ( Taxon ) result;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByScientificName(java.lang.String)
     */
    public ubic.gemma.model.genome.Taxon findByScientificName( java.lang.String scientificName ) {
        return this.findByScientificName( TRANSFORM_NONE, scientificName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findByScientificName(java.lang.String, java.lang.String)
     */
    public ubic.gemma.model.genome.Taxon findByScientificName( final java.lang.String queryString,
            final java.lang.String scientificName ) {
        return this.findByScientificName( TRANSFORM_NONE, queryString, scientificName );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findChildTaxaByParent(ubic.gemma.model.genome.Taxon)
     */

    public Collection<Taxon> findChildTaxaByParent( Taxon parentTaxon ) {
        String queryString = "from ubic.gemma.model.genome.Taxon as taxon where taxon.parentTaxon = :parentTaxon";
        Collection<Taxon> childTaxa = this.getHibernateTemplate().findByNamedParam( queryString, "parentTaxon",
                parentTaxon );
        return childTaxa;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findOrCreate(int, java.lang.String, ubic.gemma.model.genome.Taxon)
     */

    public Taxon findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.genome.Taxon taxon ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( taxon );
        argNames.add( "taxon" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.genome.Taxon"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.genome.Taxon ) result );
        return ( Taxon ) result;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findOrCreate(int, ubic.gemma.model.genome.Taxon)
     */
    public Taxon findOrCreate( final int transform, final ubic.gemma.model.genome.Taxon taxon ) {
        return this.findOrCreate( transform, "from ubic.gemma.model.genome.Taxon as taxon where taxon.taxon = :taxon",
                taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findOrCreate(java.lang.String, ubic.gemma.model.genome.Taxon)
     */
    public ubic.gemma.model.genome.Taxon findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.genome.Taxon taxon ) {
        return this.findOrCreate( TRANSFORM_NONE, queryString, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#findOrCreate(ubic.gemma.model.genome.Taxon)
     */
    public ubic.gemma.model.genome.Taxon findOrCreate( ubic.gemma.model.genome.Taxon taxon ) {
        return this.findOrCreate( TRANSFORM_NONE, taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#load(int, java.lang.Long)
     */
    public Taxon load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Taxon.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get( ubic.gemma.model.genome.TaxonImpl.class, id );
        return transformEntity( transform, ( ubic.gemma.model.genome.Taxon ) entity );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#load(java.lang.Long)
     */
    public ubic.gemma.model.genome.Taxon load( java.lang.Long id ) {
        return this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#loadAll()
     */

    public java.util.Collection<Taxon> loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#loadAll(int)
     */

    public java.util.Collection<Taxon> loadAll( final int transform ) {
        final java.util.Collection results = this.getHibernateTemplate().loadAll(
                ubic.gemma.model.genome.TaxonImpl.class );
        this.transformEntities( transform, results );
        return results;
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#remove(java.lang.Long)
     */
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "Taxon.remove - 'id' can not be null" );
        }
        ubic.gemma.model.genome.Taxon entity = this.load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#remove(java.util.Collection)
     */
    public void remove( java.util.Collection<Taxon> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Taxon.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#remove(ubic.gemma.model.genome.Taxon)
     */
    public void remove( ubic.gemma.model.genome.Taxon taxon ) {
        if ( taxon == null ) {
            throw new IllegalArgumentException( "Taxon.remove - 'taxon' can not be null" );
        }
        this.getHibernateTemplate().delete( taxon );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#update(java.util.Collection)
     */
    public void update( final java.util.Collection<Taxon> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "Taxon.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback<Object>() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator<Taxon> entityIterator = entities.iterator(); entityIterator.hasNext(); ) {
                            update( entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.genome.TaxonDao#update(ubic.gemma.model.genome.Taxon)
     */
    public void update( ubic.gemma.model.genome.Taxon taxon ) {
        if ( taxon == null ) {
            throw new IllegalArgumentException( "Taxon.update - 'taxon' can not be null" );
        }
        this.getHibernateTemplate().update( taxon );
    }

    /**
     * Performs the core logic for {@link #findByAbbreviation(java.lang.String abbreviation)}
     */
    protected abstract Taxon handleFindByAbbreviation( java.lang.String abbreviation ) throws java.lang.Exception;

    /**
     * Transforms a collection of entities using the {@link #transformEntity(int,ubic.gemma.model.genome.Taxon)} method.
     * This method does not instantiate a new collection.
     * <p/>
     * This method is to be used internally only.
     * 
     * @param transform one of the constants declared in <code>ubic.gemma.model.genome.TaxonDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.genome.Taxon)
     */
    protected void transformEntities( final int transform, final java.util.Collection<Taxon> entities ) {
        switch ( transform ) {
            case TRANSFORM_NONE: // fall-through
            default:
                // do nothing;
        }
    }

    /**
     * Allows transformation of entities into value objects (or something else for that matter), when the
     * <code>transform</code> flag is set to one of the constants defined in
     * <code>ubic.gemma.model.genome.TaxonDao</code>, please note that the {@link #TRANSFORM_NONE} constant denotes no
     * transformation, so the entity itself will be returned. If the integer argument value is unknown
     * {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.genome.TaxonDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Taxon transformEntity( final int transform, final ubic.gemma.model.genome.Taxon entity ) {
        Taxon target = null;
        if ( entity != null ) {
            switch ( transform ) {
                case TRANSFORM_NONE: // fall-through
                default:
                    target = entity;
            }
        }
        return target;
    }
    
    /**
     * Performs the core logic for {@link #thaw(ubic.gemma.model.expression.taxon.Taxon)}
     */
    protected abstract void handleThaw( ubic.gemma.model.genome.Taxon taxon )
            throws java.lang.Exception;
    
    
    /**
     * @see ubic.gemma.model.genome.TaxonDao.thaw#thaw(ubic.gemma.model.expression.bioAssay.BioAssay)
     */
    public void thaw( final ubic.gemma.model.genome.Taxon taxon ) {
        try {
            this.handleThaw( taxon );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.genome.TaxonDao.thaw(ubic.gemma.model.genome.Taxon)' --> "
                            + th, th );
        }
    }
    

}