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
package ubic.gemma.model.expression.arrayDesign;

import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * <p>
 * Base Spring DAO Class: is able to create, update, remove, load, and find objects of type
 * <code>ubic.gemma.model.expression.arrayDesign.ArrayDesign</code>.
 * </p>
 * 
 * @see ubic.gemma.model.expression.arrayDesign.ArrayDesign
 */
public abstract class ArrayDesignDaoBase extends HibernateDaoSupport implements ArrayDesignDao {

    /**
     * This anonymous transformer is designed to transform entities or report query results (which result in an array of
     * objects) to {@link ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject} using the Jakarta
     * Commons-Collections Transformation API.
     */
    private org.apache.commons.collections.Transformer ARRAYDESIGNVALUEOBJECT_TRANSFORMER = new org.apache.commons.collections.Transformer() {
        public Object transform( Object input ) {
            Object result = null;
            if ( input instanceof ubic.gemma.model.expression.arrayDesign.ArrayDesign ) {
                result = toArrayDesignValueObject( ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) input );
            } else if ( input instanceof Object[] ) {
                result = toArrayDesignValueObject( ( Object[] ) input );
            }
            return result;
        }
    };

    private final org.apache.commons.collections.Transformer ArrayDesignValueObjectToEntityTransformer = new org.apache.commons.collections.Transformer() {
        public Object transform( Object input ) {
            return arrayDesignValueObjectToEntity( ( ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject ) input );
        }
    };

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#arrayDesignValueObjectToEntity(ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void arrayDesignValueObjectToEntity( ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject source,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign target, boolean copyIfNull ) {
        if ( copyIfNull || source.getShortName() != null ) {
            target.setShortName( source.getShortName() );
        }
        if ( copyIfNull || source.getTechnologyType() != null ) {
            target.setTechnologyType( source.getTechnologyType() );
        }
        if ( copyIfNull || source.getName() != null ) {
            target.setName( source.getName() );
        }
        if ( copyIfNull || source.getDescription() != null ) {
            target.setDescription( source.getDescription() );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#arrayDesignValueObjectToEntityCollection(java.util.Collection)
     */
    public final void arrayDesignValueObjectToEntityCollection( java.util.Collection<ArrayDesignValueObject> instances ) {
        if ( instances != null ) {
            for ( final java.util.Iterator<ArrayDesignValueObject> iterator = instances.iterator(); iterator.hasNext(); ) {
                // - remove an objects that are null or not of the correct instance
                iterator.remove();

            }
            org.apache.commons.collections.CollectionUtils.transform( instances,
                    ArrayDesignValueObjectToEntityTransformer );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#compositeSequenceWithoutBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutBioSequences(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleCompositeSequenceWithoutBioSequences( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.compositeSequenceWithoutBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#compositeSequenceWithoutBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutBlatResults(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleCompositeSequenceWithoutBlatResults( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.compositeSequenceWithoutBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#compositeSequenceWithoutGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public java.util.Collection<CompositeSequence> compositeSequenceWithoutGenes(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleCompositeSequenceWithoutGenes( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.compositeSequenceWithoutGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#countAll()
     */
    public java.lang.Integer countAll() {
        try {
            return this.handleCountAll();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.countAll()' --> " + th,
                    th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#create(int, java.util.Collection)
     */
    public java.util.Collection<ArrayDesign> create( final int transform,
            final java.util.Collection<ArrayDesign> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "ArrayDesign.create - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator<ArrayDesign> entityIterator = entities.iterator(); entityIterator
                                .hasNext(); ) {
                            create( transform, entityIterator.next() );
                        }
                        return null;
                    }
                } );
        return entities;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#create(int transform,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public Object create( final int transform, final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        if ( arrayDesign == null ) {
            throw new IllegalArgumentException( "ArrayDesign.create - 'arrayDesign' can not be null" );
        }
        this.getHibernateTemplate().save( arrayDesign );
        return this.transformEntity( transform, arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#create(java.util.Collection)
     */
    public java.util.Collection<ArrayDesign> create( final java.util.Collection<ArrayDesign> entities ) {
        return create( TRANSFORM_NONE, entities );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#create(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public ArrayDesign create( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.create( TRANSFORM_NONE, arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#deleteAlignmentData(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void deleteAlignmentData( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            this.handleDeleteAlignmentData( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.deleteAlignmentData(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#deleteGeneProductAssociations(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void deleteGeneProductAssociations( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            this.handleDeleteGeneProductAssociations( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.deleteGeneProductAssociations(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayDesign find( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( arrayDesign );
        argNames.add( "arrayDesign" );
        java.util.Set<ArrayDesign> results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam(
                queryString, argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;
        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.arrayDesign.ArrayDesign"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) result );
        return ( ArrayDesign ) result;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#find(int,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public Object find( final int transform, final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return this
                .find(
                        transform,
                        "from ubic.gemma.model.expression.arrayDesign.ArrayDesign as arrayDesign where arrayDesign.arrayDesign = :arrayDesign",
                        arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#find(java.lang.String,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign find( final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return this.find( TRANSFORM_NONE, queryString, arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByAlternateName(java.lang.String)
     */
    public java.util.Collection<ArrayDesign> findByAlternateName( final java.lang.String queryString ) {
        try {
            return this.handleFindByAlternateName( queryString );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.findByAlternateName(java.lang.String queryString)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByName(int, java.lang.String)
     */
    public Object findByName( final int transform, final java.lang.String name ) {
        return this.findByName( transform, "from ArrayDesignImpl a where a.name=:name", name );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByName(int, java.lang.String, java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findByName( final int transform, final java.lang.String queryString, final java.lang.String name ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( name );
        argNames.add( "name" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.arrayDesign.ArrayDesign"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByName(java.lang.String)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findByName( java.lang.String name ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findByName( TRANSFORM_NONE, name );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByName(java.lang.String, java.lang.String)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findByName( final java.lang.String queryString,
            final java.lang.String name ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findByName( TRANSFORM_NONE, queryString,
                name );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByShortName(int, java.lang.String)
     */
    public Object findByShortName( final int transform, final java.lang.String shortName ) {
        return this.findByShortName( transform, "from ArrayDesignImpl a where a.shortName=:shortName", shortName );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByShortName(int, java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findByShortName( final int transform, final java.lang.String queryString,
            final java.lang.String shortName ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( shortName );
        argNames.add( "shortName" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.arrayDesign.ArrayDesign"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByShortName(java.lang.String)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findByShortName( java.lang.String shortName ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findByShortName( TRANSFORM_NONE, shortName );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findByShortName(java.lang.String, java.lang.String)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findByShortName( final java.lang.String queryString,
            final java.lang.String shortName ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findByShortName( TRANSFORM_NONE,
                queryString, shortName );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findOrCreate(int, java.lang.String,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    @SuppressWarnings( { "unchecked" })
    public Object findOrCreate( final int transform, final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        java.util.List<String> argNames = new java.util.ArrayList<String>();
        java.util.List<Object> args = new java.util.ArrayList<Object>();
        args.add( arrayDesign );
        argNames.add( "arrayDesign" );
        java.util.Set results = new java.util.LinkedHashSet( this.getHibernateTemplate().findByNamedParam( queryString,
                argNames.toArray( new String[argNames.size()] ), args.toArray() ) );
        Object result = null;

        if ( results.size() > 1 ) {
            throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                    "More than one instance of 'ubic.gemma.model.expression.arrayDesign.ArrayDesign"
                            + "' was found when executing query --> '" + queryString + "'" );
        } else if ( results.size() == 1 ) {
            result = results.iterator().next();
        }

        result = transformEntity( transform, ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) result );
        return result;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findOrCreate(int,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public Object findOrCreate( final int transform,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return this
                .findOrCreate(
                        transform,
                        "from ubic.gemma.model.expression.arrayDesign.ArrayDesign as arrayDesign where arrayDesign.arrayDesign = :arrayDesign",
                        arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findOrCreate(java.lang.String,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findOrCreate( final java.lang.String queryString,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findOrCreate( TRANSFORM_NONE, queryString,
                arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#findOrCreate(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign findOrCreate(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        return ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) this.findOrCreate( TRANSFORM_NONE, arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#getAllAssociatedBioAssays(java.lang.Long)
     */
    public java.util.Collection<BioAssay> getAllAssociatedBioAssays( final java.lang.Long id ) {
        try {
            return this.handleGetAllAssociatedBioAssays( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.getAllAssociatedBioAssays(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#getAuditEvents(java.util.Collection)
     */
    public java.util.Map<Long, Collection<AuditEvent>> getAuditEvents( final java.util.Collection<Long> ids ) {
        try {
            return this.handleGetAuditEvents( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.getAuditEvents(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#getExpressionExperiments(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public java.util.Collection<ExpressionExperiment> getExpressionExperiments(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleGetExpressionExperiments( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.getExpressionExperiments(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#getTaxon(java.lang.Long)
     */
    public ubic.gemma.model.genome.Taxon getTaxon( final java.lang.Long id ) {
        try {
            return this.handleGetTaxon( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.getTaxon(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#isMerged(java.util.Collection)
     */
    public java.util.Map<Long, Boolean> isMerged( final java.util.Collection<Long> ids ) {
        try {
            return this.handleIsMerged( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.isMerged(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#isMergee(java.util.Collection)
     */
    public java.util.Map<Long, Boolean> isMergee( final java.util.Collection<Long> ids ) {
        try {
            return this.handleIsMergee( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.isMergee(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#isSubsumed(java.util.Collection)
     */
    public java.util.Map<Long, Boolean> isSubsumed( final java.util.Collection<Long> ids ) {
        try {
            return this.handleIsSubsumed( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.isSubsumed(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#isSubsumer(java.util.Collection)
     */
    public java.util.Map<Long, Boolean> isSubsumer( final java.util.Collection<Long> ids ) {
        try {
            return this.handleIsSubsumer( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.isSubsumer(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#load(int, java.lang.Long)
     */
    public ArrayDesign load( final int transform, final java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "ArrayDesign.load - 'id' can not be null" );
        }
        final Object entity = this.getHibernateTemplate().get(
                ubic.gemma.model.expression.arrayDesign.ArrayDesignImpl.class, id );
        return ( ArrayDesign ) transformEntity( transform,
                ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) entity );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#load(java.lang.Long)
     */
    public ArrayDesign load( java.lang.Long id ) {
        return this.load( TRANSFORM_NONE, id );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadAll()
     */
    public java.util.Collection<ArrayDesign> loadAll() {
        return this.loadAll( TRANSFORM_NONE );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadAll(int)
     */
    @SuppressWarnings("unchecked")
    public java.util.Collection<ArrayDesign> loadAll( final int transform ) {
        return this.getHibernateTemplate().loadAll( ubic.gemma.model.expression.arrayDesign.ArrayDesignImpl.class );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadAllValueObjects()
     */
    public java.util.Collection<ArrayDesignValueObject> loadAllValueObjects() {
        try {
            return this.handleLoadAllValueObjects();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.loadAllValueObjects()' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadCompositeSequences(java.lang.Long)
     */
    public java.util.Collection<CompositeSequence> loadCompositeSequences( final java.lang.Long id ) {
        try {
            return this.handleLoadCompositeSequences( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.loadCompositeSequences(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadFully(java.lang.Long)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesign loadFully( final java.lang.Long id ) {
        try {
            return this.handleLoadFully( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.loadFully(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#load(java.util.Collection)
     */
    public java.util.Collection<ArrayDesign> load( final java.util.Collection<Long> ids ) {
        try {
            return this.handleLoadMultiple( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.loadMultiple(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#loadValueObjects(java.util.Collection)
     */
    public java.util.Collection<ArrayDesignValueObject> loadValueObjects( final java.util.Collection<Long> ids ) {
        try {
            return this.handleLoadValueObjects( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.loadValueObjects(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithBioSequences()
     */
    public long numAllCompositeSequenceWithBioSequences() {
        try {
            return this.handleNumAllCompositeSequenceWithBioSequences();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithBioSequences()' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithBioSequences(java.util.Collection)
     */
    public long numAllCompositeSequenceWithBioSequences( final java.util.Collection<Long> ids ) {
        try {
            return this.handleNumAllCompositeSequenceWithBioSequences( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithBioSequences(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithBlatResults()
     */
    public long numAllCompositeSequenceWithBlatResults() {
        try {
            return this.handleNumAllCompositeSequenceWithBlatResults();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithBlatResults()' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithBlatResults(java.util.Collection)
     */
    public long numAllCompositeSequenceWithBlatResults( final java.util.Collection<Long> ids ) {
        try {
            return this.handleNumAllCompositeSequenceWithBlatResults( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithBlatResults(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithGenes()
     */
    public long numAllCompositeSequenceWithGenes() {
        try {
            return this.handleNumAllCompositeSequenceWithGenes();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithGenes()' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllCompositeSequenceWithGenes(java.util.Collection)
     */
    public long numAllCompositeSequenceWithGenes( final java.util.Collection<Long> ids ) {
        try {
            return this.handleNumAllCompositeSequenceWithGenes( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllCompositeSequenceWithGenes(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllGenes()
     */
    public long numAllGenes() {
        try {
            return this.handleNumAllGenes();
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllGenes()' --> " + th,
                    th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numAllGenes(java.util.Collection)
     */
    public long numAllGenes( final java.util.Collection<Long> ids ) {
        try {
            return this.handleNumAllGenes( ids );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numAllGenes(java.util.Collection ids)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numBioSequences( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumBioSequences( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numBlatResults( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumBlatResults( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequences(java.lang.Long)
     */
    public java.lang.Integer numCompositeSequences( final java.lang.Long id ) {
        try {
            return this.handleNumCompositeSequences( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequences(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequenceWithBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numCompositeSequenceWithBioSequences(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumCompositeSequenceWithBioSequences( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequenceWithBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequenceWithBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numCompositeSequenceWithBlatResults(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumCompositeSequenceWithBlatResults( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequenceWithBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequenceWithGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numCompositeSequenceWithGenes( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumCompositeSequenceWithGenes( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequenceWithGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequenceWithPredictedGene(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numCompositeSequenceWithPredictedGene(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumCompositeSequenceWithPredictedGene( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequenceWithPredictedGene(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numCompositeSequenceWithProbeAlignedRegion(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numCompositeSequenceWithProbeAlignedRegion(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumCompositeSequenceWithProbeAlignedRegion( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numCompositeSequenceWithProbeAlignedRegion(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public long numGenes( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            return this.handleNumGenes( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#numReporters(java.lang.Long)
     */
    public java.lang.Integer numReporters( final java.lang.Long id ) {
        try {
            return this.handleNumReporters( id );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.numReporters(java.lang.Long id)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#remove(java.lang.Long)
     */
    public void remove( java.lang.Long id ) {
        if ( id == null ) {
            throw new IllegalArgumentException( "ArrayDesign.remove - 'id' can not be null" );
        }
        ubic.gemma.model.expression.arrayDesign.ArrayDesign entity = this.load( id );
        if ( entity != null ) {
            this.remove( entity );
        }
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#remove(java.util.Collection)
     */
    public void remove( java.util.Collection<ArrayDesign> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "ArrayDesign.remove - 'entities' can not be null" );
        }
        this.getHibernateTemplate().deleteAll( entities );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#remove(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void remove( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        if ( arrayDesign == null ) {
            throw new IllegalArgumentException( "ArrayDesign.remove - 'arrayDesign' can not be null" );
        }
        this.getHibernateTemplate().delete( arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#removeBiologicalCharacteristics(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void removeBiologicalCharacteristics( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            this.handleRemoveBiologicalCharacteristics( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.removeBiologicalCharacteristics(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#thaw(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void thaw( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            this.handleThaw( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.thaw(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#thawLite(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void thawLite( final ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        try {
            this.handleThawLite( arrayDesign );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.thawLite(ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign)' --> "
                            + th, th );
        }
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#toArrayDesignValueObject(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject toArrayDesignValueObject(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign entity ) {
        final ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject target = new ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject();
        this.toArrayDesignValueObject( entity, target );
        return target;
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#toArrayDesignValueObject(ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject)
     */
    public void toArrayDesignValueObject( ubic.gemma.model.expression.arrayDesign.ArrayDesign source,
            ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject target ) {
        target.setName( source.getName() );
        target.setShortName( source.getShortName() );
        target.setId( source.getId() );
        target.setDescription( source.getDescription() );
        target.setTechnologyType( source.getTechnologyType() );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#toArrayDesignValueObjectCollection(java.util.Collection)
     */
    public final void toArrayDesignValueObjectCollection( java.util.Collection<ArrayDesign> entities ) {
        if ( entities != null ) {
            org.apache.commons.collections.CollectionUtils.transform( entities, ARRAYDESIGNVALUEOBJECT_TRANSFORMER );
        }
    }

    /**
     * @see ubic.gemma.model.common.SecurableDao#update(java.util.Collection)
     */
    public void update( final java.util.Collection<ArrayDesign> entities ) {
        if ( entities == null ) {
            throw new IllegalArgumentException( "ArrayDesign.update - 'entities' can not be null" );
        }
        this.getHibernateTemplate().executeWithNativeSession(
                new org.springframework.orm.hibernate3.HibernateCallback() {
                    public Object doInHibernate( org.hibernate.Session session )
                            throws org.hibernate.HibernateException {
                        for ( java.util.Iterator<ArrayDesign> entityIterator = entities.iterator(); entityIterator
                                .hasNext(); ) {
                            update( entityIterator.next() );
                        }
                        return null;
                    }
                } );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#update(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public void update( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) {
        if ( arrayDesign == null ) {
            throw new IllegalArgumentException( "ArrayDesign.update - 'arrayDesign' can not be null" );
        }
        this.getHibernateTemplate().update( arrayDesign );
    }

    /**
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#updateSubsumingStatus(ubic.gemma.model.expression.arrayDesign.ArrayDesign,
     *      ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    public java.lang.Boolean updateSubsumingStatus(
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumer,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumee ) {
        try {
            return this.handleUpdateSubsumingStatus( candidateSubsumer, candidateSubsumee );
        } catch ( Throwable th ) {
            throw new java.lang.RuntimeException(
                    "Error performing 'ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.updateSubsumingStatus(ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumer, ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumee)' --> "
                            + th, th );
        }
    }

    /**
     * Performs the core logic for
     * {@link #compositeSequenceWithoutBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract java.util.Collection<CompositeSequence> handleCompositeSequenceWithoutBioSequences(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #compositeSequenceWithoutBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract java.util.Collection<CompositeSequence> handleCompositeSequenceWithoutBlatResults(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #compositeSequenceWithoutGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract java.util.Collection<CompositeSequence> handleCompositeSequenceWithoutGenes(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #countAll()}
     */
    protected abstract java.lang.Integer handleCountAll() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #deleteAlignmentData(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract void handleDeleteAlignmentData( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #deleteGeneProductAssociations(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract void handleDeleteGeneProductAssociations(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #findByAlternateName(java.lang.String)}
     */
    protected abstract java.util.Collection<ArrayDesign> handleFindByAlternateName( java.lang.String queryString )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #getAllAssociatedBioAssays(java.lang.Long)}
     */
    protected abstract java.util.Collection<BioAssay> handleGetAllAssociatedBioAssays( java.lang.Long id )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #getAuditEvents(java.util.Collection)}
     */
    protected abstract java.util.Map<Long, Collection<AuditEvent>> handleGetAuditEvents( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #getExpressionExperiments(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract java.util.Collection<ExpressionExperiment> handleGetExpressionExperiments(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #getTaxon(java.lang.Long)}
     */
    protected abstract ubic.gemma.model.genome.Taxon handleGetTaxon( java.lang.Long id ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #isMerged(java.util.Collection)}
     */
    protected abstract java.util.Map<Long, Boolean> handleIsMerged( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #isMergee(java.util.Collection)}
     */
    protected abstract java.util.Map<Long, Boolean> handleIsMergee( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #isSubsumed(java.util.Collection)}
     */
    protected abstract java.util.Map<Long, Boolean> handleIsSubsumed( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #isSubsumer(java.util.Collection)}
     */
    protected abstract java.util.Map<Long, Boolean> handleIsSubsumer( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #loadAllValueObjects()}
     */
    protected abstract java.util.Collection<ArrayDesignValueObject> handleLoadAllValueObjects()
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #loadCompositeSequences(java.lang.Long)}
     */
    protected abstract java.util.Collection<CompositeSequence> handleLoadCompositeSequences( java.lang.Long id )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #loadFully(java.lang.Long)}
     */
    protected abstract ubic.gemma.model.expression.arrayDesign.ArrayDesign handleLoadFully( java.lang.Long id )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #load(java.util.Collection)}
     */
    protected abstract java.util.Collection<ArrayDesign> handleLoadMultiple( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #loadValueObjects(java.util.Collection)}
     */
    protected abstract java.util.Collection<ArrayDesignValueObject> handleLoadValueObjects(
            java.util.Collection<Long> ids ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithBioSequences()}
     */
    protected abstract long handleNumAllCompositeSequenceWithBioSequences() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithBioSequences(java.util.Collection)}
     */
    protected abstract long handleNumAllCompositeSequenceWithBioSequences( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithBlatResults()}
     */
    protected abstract long handleNumAllCompositeSequenceWithBlatResults() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithBlatResults(java.util.Collection)}
     */
    protected abstract long handleNumAllCompositeSequenceWithBlatResults( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithGenes()}
     */
    protected abstract long handleNumAllCompositeSequenceWithGenes() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllCompositeSequenceWithGenes(java.util.Collection)}
     */
    protected abstract long handleNumAllCompositeSequenceWithGenes( java.util.Collection<Long> ids )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllGenes()}
     */
    protected abstract long handleNumAllGenes() throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numAllGenes(java.util.Collection)}
     */
    protected abstract long handleNumAllGenes( java.util.Collection<Long> ids ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumBioSequences( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumBlatResults( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numCompositeSequences(java.lang.Long)}
     */
    protected abstract java.lang.Integer handleNumCompositeSequences( java.lang.Long id ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #numCompositeSequenceWithBioSequences(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumCompositeSequenceWithBioSequences(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #numCompositeSequenceWithBlatResults(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumCompositeSequenceWithBlatResults(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #numCompositeSequenceWithGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumCompositeSequenceWithGenes(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #numCompositeSequenceWithPredictedGene(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumCompositeSequenceWithPredictedGene(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #numCompositeSequenceWithProbeAlignedRegion(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumCompositeSequenceWithProbeAlignedRegion(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numGenes(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract long handleNumGenes( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #numReporters(java.lang.Long)}
     */
    protected abstract java.lang.Integer handleNumReporters( java.lang.Long id ) throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #removeBiologicalCharacteristics(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract void handleRemoveBiologicalCharacteristics(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign ) throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #thaw(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract void handleThaw( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for {@link #thawLite(ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract void handleThawLite( ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign )
            throws java.lang.Exception;

    /**
     * Performs the core logic for
     * {@link #updateSubsumingStatus(ubic.gemma.model.expression.arrayDesign.ArrayDesign, ubic.gemma.model.expression.arrayDesign.ArrayDesign)}
     */
    protected abstract java.lang.Boolean handleUpdateSubsumingStatus(
            ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumer,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign candidateSubsumee ) throws java.lang.Exception;

    /**
     * Default implementation for transforming the results of a report query into a value object. This implementation
     * exists for convenience reasons only. It needs only be overridden in the {@link ArrayDesignDaoImpl} class if you
     * intend to use reporting queries.
     * 
     * @see ubic.gemma.model.expression.arrayDesign.ArrayDesignDao#toArrayDesignValueObject(ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    protected ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject toArrayDesignValueObject( Object[] row ) {
        ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject target = null;
        if ( row != null ) {
            final int numberOfObjects = row.length;
            for ( int ctr = 0; ctr < numberOfObjects; ctr++ ) {
                final Object object = row[ctr];
                if ( object instanceof ubic.gemma.model.expression.arrayDesign.ArrayDesign ) {
                    target = this
                            .toArrayDesignValueObject( ( ubic.gemma.model.expression.arrayDesign.ArrayDesign ) object );
                    break;
                }
            }
        }
        return target;
    }

    /**
     * Transforms a collection of entities using the
     * {@link #transformEntity(int,ubic.gemma.model.expression.arrayDesign.ArrayDesign)} method. This method does not
     * instantiate a new collection. <p/> This method is to be used internally only.
     * 
     * @param transform one of the constants declared in
     *        <code>ubic.gemma.model.expression.arrayDesign.ArrayDesignDao</code>
     * @param entities the collection of entities to transform
     * @return the same collection as the argument, but this time containing the transformed entities
     * @see #transformEntity(int,ubic.gemma.model.expression.arrayDesign.ArrayDesign)
     */
    protected void transformEntities( final int transform, final java.util.Collection<ArrayDesign> entities ) {
        switch ( transform ) {
            case ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.TRANSFORM_ARRAYDESIGNVALUEOBJECT:
                toArrayDesignValueObjectCollection( entities );
                break;
            case TRANSFORM_NONE: // fall-through
            default:
                // do nothing;
        }
    }

    /**
     * Allows transformation of entities into value objects (or something else for that matter), when the
     * <code>transform</code> flag is set to one of the constants defined in
     * <code>ubic.gemma.model.expression.arrayDesign.ArrayDesignDao</code>, please note that the
     * {@link #TRANSFORM_NONE} constant denotes no transformation, so the entity itself will be returned. <p/> This
     * method will return instances of these types:
     * <ul>
     * <li>{@link ubic.gemma.model.expression.arrayDesign.ArrayDesign} - {@link #TRANSFORM_NONE}</li>
     * <li>{@link ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject} -
     * {@link TRANSFORM_ARRAYDESIGNVALUEOBJECT}</li>
     * </ul>
     * If the integer argument value is unknown {@link #TRANSFORM_NONE} is assumed.
     * 
     * @param transform one of the constants declared in {@link ubic.gemma.model.expression.arrayDesign.ArrayDesignDao}
     * @param entity an entity that was found
     * @return the transformed entity (i.e. new value object, etc)
     * @see #transformEntities(int,java.util.Collection)
     */
    protected Object transformEntity( final int transform,
            final ubic.gemma.model.expression.arrayDesign.ArrayDesign entity ) {
        Object target = null;
        if ( entity != null ) {
            switch ( transform ) {
                case ubic.gemma.model.expression.arrayDesign.ArrayDesignDao.TRANSFORM_ARRAYDESIGNVALUEOBJECT:
                    target = toArrayDesignValueObject( entity );
                    break;
                case TRANSFORM_NONE: // fall-through
                default:
                    target = entity;
            }
        }
        return target;
    }

}