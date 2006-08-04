/*
 * The Gemma project.
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.gemma.model.expression.experiment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import ubic.gemma.util.BeanPropertyCompleter;

/**
 * @author pavlidis
 * @version $Id$
 * @see ubic.gemma.model.expression.experiment.ExpressionExperiment
 */
public class ExpressionExperimentDaoImpl extends ubic.gemma.model.expression.experiment.ExpressionExperimentDaoBase {

    private static Log log = LogFactory.getLog( ExpressionExperimentDaoImpl.class.getName() );

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentDaoBase#find(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    public ExpressionExperiment find( ExpressionExperiment expressionExperiment ) {
        try {
            Criteria queryObject = super.getSession( false ).createCriteria( ExpressionExperiment.class );

            if ( expressionExperiment.getAccession() != null ) {
                queryObject.add( Restrictions.eq( "accession", expressionExperiment.getAccession() ) );
            } else {
                queryObject.add( Restrictions.eq( "name", expressionExperiment.getName() ) );
            }

            java.util.List results = queryObject.list();
            Object result = null;
            if ( results != null ) {
                if ( results.size() > 1 ) {
                    throw new org.springframework.dao.InvalidDataAccessResourceUsageException(
                            "More than one instance of '" + ExpressionExperiment.class.getName()
                                    + "' was found when executing query" );

                } else if ( results.size() == 1 ) {
                    result = results.iterator().next();
                }
            }
            return ( ExpressionExperiment ) result;
        } catch ( org.hibernate.HibernateException ex ) {
            throw super.convertHibernateAccessException( ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.gemma.model.expression.experiment.ExpressionExperimentDaoBase#findOrCreate(ubic.gemma.model.expression.experiment.ExpressionExperiment)
     */
    @Override
    public ExpressionExperiment findOrCreate( ExpressionExperiment expressionExperiment ) {
        if ( expressionExperiment.getName() == null && expressionExperiment.getAccession() == null ) {
            throw new IllegalArgumentException( "ExpressionExperiment must have name or external accession." );
        }
        ExpressionExperiment newExpressionExperiment = this.find( expressionExperiment );
        if ( newExpressionExperiment != null ) {
            BeanPropertyCompleter.complete( newExpressionExperiment, expressionExperiment );
            return newExpressionExperiment;
        }
        log.debug( "Creating new expressionExperiment: " + expressionExperiment.getName() );
        return ( ExpressionExperiment ) create( expressionExperiment );
    }
}