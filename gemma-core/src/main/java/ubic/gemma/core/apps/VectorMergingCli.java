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
package ubic.gemma.core.apps;

import ubic.gemma.core.analysis.preprocess.VectorMergingService;
import ubic.gemma.core.util.AbstractCLI;
import ubic.gemma.model.expression.experiment.BioAssaySet;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;

/**
 * For experiments that used multiple array designs, merge the expression profiles
 *
 * @author pavlidis
 */
public class VectorMergingCli extends ExpressionExperimentManipulatingCLI {

    private VectorMergingService mergingService;

    public static void main( String[] args ) {
        VectorMergingCli v = new VectorMergingCli();
        Exception e = v.doWork( args );
        if ( e != null ) {
            AbstractCLI.log.fatal( e );
        }
    }

    @Override
    public GemmaCLI.CommandGroup getCommandGroup() {
        return GemmaCLI.CommandGroup.EXPERIMENT;
    }

    @Override
    protected void buildOptions() {
        super.buildOptions();
        super.addForceOption();
    }

    @Override
    public String getCommandName() {
        return "vectorMerge";
    }

    @Override
    protected Exception doWork( String[] args ) {
        Exception e = this.processCommandLine( args );
        if ( e != null ) {
            return e;
        }

        mergingService = this.getBean( VectorMergingService.class );

        for ( BioAssaySet ee : expressionExperiments ) {
            if ( ee instanceof ExpressionExperiment ) {
                this.processExperiment( ( ExpressionExperiment ) ee );
            } else {
                throw new UnsupportedOperationException(
                        "Can't do vector merging on non-expressionExperiment bioassaysets" );
            }
        }

        this.summarizeProcessing();
        return null;

    }

    @Override
    public String getShortDesc() {
        return "For experiments that used multiple array designs, merge the expression profiles";
    }

    private void processExperiment( ExpressionExperiment expressionExperiment ) {
        try {
            expressionExperiment = eeService.thawLite( expressionExperiment );

            expressionExperiment = mergingService.mergeVectors( expressionExperiment );

            super.successObjects.add( expressionExperiment.toString() );
        } catch ( Exception e ) {
            AbstractCLI.log.error( e, e );
            super.errorObjects.add( expressionExperiment + ": " + e.getMessage() );

        }
        AbstractCLI.log.info( "Finished processing " + expressionExperiment );
    }
}
