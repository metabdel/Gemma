/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.gemma.web.controller.diff;

import java.util.Collection;
import java.util.HashSet;

import ubic.gemma.analysis.expression.diff.DifferentialExpressionAnalyzerService;
import ubic.gemma.web.controller.expression.experiment.ExperimentalFactorValueObject;

/**
 * Used to carry information about the experimental design analysis settings to clients.
 * 
 * @author paul
 * @version $Id$
 */
public class DifferentialExpressionAnalyzerInfo {

    Collection<ExperimentalFactorValueObject> factors = new HashSet<ExperimentalFactorValueObject>();

    DifferentialExpressionAnalyzerService.AnalysisType type;

    public Collection<ExperimentalFactorValueObject> getFactors() {
        return factors;
    }

    public DifferentialExpressionAnalyzerService.AnalysisType getType() {
        return type;
    }

    public void setFactors( Collection<ExperimentalFactorValueObject> factors ) {
        this.factors = factors;
    }

    public void setType( DifferentialExpressionAnalyzerService.AnalysisType type ) {
        this.type = type;
    }

}
