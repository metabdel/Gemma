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

import ubic.gemma.model.analysis.AnalysisResultSet;
import ubic.gemma.model.expression.experiment.ExperimentalFactor;

/**
 * 
 */
public interface DifferentialExpressionAnalysisResultService {

    /**
     * 
     */
    public java.util.Map<DifferentialExpressionAnalysisResult, Collection<ExperimentalFactor>> getExperimentalFactors(
            java.util.Collection differentialExpressionAnalysisResults );

    /**
     * 
     */
    public java.util.Collection getExperimentalFactors(
            ubic.gemma.model.analysis.expression.diff.DifferentialExpressionAnalysisResult differentialExpressionAnalysisResult );

    /**
     * 
     */
    public void thaw( ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet resultSet );
    
    /**
     * 
     * Does not thaw the collection of probes (just the factor information)
     * @param resultSet
     */
    public void thawLite( ubic.gemma.model.analysis.expression.ExpressionAnalysisResultSet resultSet );


    public void thawAnalysisResult( final DifferentialExpressionAnalysisResult result ) throws Exception;
    
    public AnalysisResultSet loadAnalysisResult(Long analysisResultId);

}
