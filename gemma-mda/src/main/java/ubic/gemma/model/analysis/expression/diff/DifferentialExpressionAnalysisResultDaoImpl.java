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
package ubic.gemma.model.analysis.expression.diff;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.LockMode;
import org.springframework.orm.hibernate3.HibernateTemplate;

import ubic.gemma.model.analysis.expression.ProbeAnalysisResult;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;

/**
 * @author keshav
 * @version $Id$
 * @see ubic.gemma.model.expression.analysis.DifferentialExpressionAnalysisResult
 */
public class DifferentialExpressionAnalysisResultDaoImpl extends
        ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResultDaoBase {

    private Log log = LogFactory.getLog( this.getClass() );

    public void thawAnalysisResult( final DifferentialExpressionAnalysisResult result ) throws Exception {
        HibernateTemplate templ = this.getHibernateTemplate();

        templ.execute( new org.springframework.orm.hibernate3.HibernateCallback() {

            public Object doInHibernate( org.hibernate.Session session ) throws org.hibernate.HibernateException {
                session.lock( result, LockMode.NONE );
                Hibernate.initialize( result );

                if ( result instanceof ProbeAnalysisResult ) {
                    ProbeAnalysisResult par = ( ProbeAnalysisResult ) result;
                    CompositeSequence cs = par.getProbe();
                    Hibernate.initialize( cs );
                }
                return null;
            }
        } );
    }

    /*
     * (non-Javadoc)
     * @see
     * ubic.gemma.model.expression.analysis.DifferentialExpressionAnalysisResultDaoBase#handleGetExperimentalFactors
     * (java.util.Collection)
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Map handleGetExperimentalFactors( Collection differentialExpressionAnalysisResults ) throws Exception {

        Map<DifferentialExpressionAnalysisResult, Collection<ExperimentalFactor>> factorsByResult = new HashMap<DifferentialExpressionAnalysisResult, Collection<ExperimentalFactor>>();
        if ( differentialExpressionAnalysisResults.size() == 0 ) {
            return factorsByResult;
        }

        final String queryString = "select ef, r from ExpressionAnalysisResultSetImpl rs"
                + " inner join rs.results r inner join rs.experimentalFactor ef where r in (:differentialExpressionAnalysisResults)";

        String[] paramNames = { "differentialExpressionAnalysisResults" };
        Object[] objectValues = { differentialExpressionAnalysisResults };

        List qr = this.getHibernateTemplate().findByNamedParam( queryString, paramNames, objectValues );

        if ( qr == null || qr.isEmpty() ) return factorsByResult;

        for ( Object o : qr ) {
            Object[] ar = ( Object[] ) o;
            ExperimentalFactor f = ( ExperimentalFactor ) ar[0];
            DifferentialExpressionAnalysisResult res = ( DifferentialExpressionAnalysisResult ) ar[1];

            if ( !factorsByResult.containsKey( res ) ) {
                factorsByResult.put( res, new HashSet<ExperimentalFactor>() );
            }

            factorsByResult.get( res ).add( f );

            if ( log.isDebugEnabled() ) log.debug( res );
        }

        return factorsByResult;

    }

    /*
     * (non-Javadoc)
     * @see
     * ubic.gemma.model.expression.analysis.DifferentialExpressionAnalysisResultDaoBase#handleGetExperimentalFactors
     * (ubic.gemma.model.expression.analysis.DifferentialExpressionAnalysisResult)
     */
    @Override
    protected Collection handleGetExperimentalFactors(
            DifferentialExpressionAnalysisResult differentialExpressionAnalysisResult ) throws Exception {

        final String queryString = "select ef from ExpressionAnalysisResultSetImpl rs"
                + " inner join rs.results r inner join rs.experimentalFactor ef where r=:differentialExpressionAnalysisResult";

        String[] paramNames = { "differentialExpressionAnalysisResult" };
        Object[] objectValues = { differentialExpressionAnalysisResult };

        return this.getHibernateTemplate().findByNamedParam( queryString, paramNames, objectValues );

    }
}