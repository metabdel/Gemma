/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2012 University of British Columbia
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

import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * 
 */
public abstract class DesignElementDataVector extends DataVector {

    private BioAssayDimension bioAssayDimension;
    private CompositeSequence designElement;

    /**
     * 
     */
    public BioAssayDimension getBioAssayDimension() {
        return this.bioAssayDimension;
    }

    /**
     * 
     */
    public CompositeSequence getDesignElement() {
        return this.designElement;
    }

    /**
     * 
     */
    public abstract ExpressionExperiment getExpressionExperiment();

    public void setBioAssayDimension( BioAssayDimension bioAssayDimension ) {
        this.bioAssayDimension = bioAssayDimension;
    }

    public void setDesignElement( CompositeSequence designElement ) {
        this.designElement = designElement;
    }

    /**
     * 
     */
    public abstract void setExpressionExperiment( ExpressionExperiment expressionExperiment );

}