/*
 * The Gemma project
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
package ubic.gemma.core.search.indexer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.compass.core.Compass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ubic.gemma.core.job.SubmittedTask;
import ubic.gemma.core.job.TaskResult;
import ubic.gemma.core.job.executor.webapp.TaskRunningService;
import ubic.gemma.core.tasks.AbstractTask;
import ubic.gemma.core.tasks.maintenance.IndexerResult;
import ubic.gemma.core.tasks.maintenance.IndexerTaskCommand;
import ubic.gemma.persistence.util.CompassUtils;

/**
 * Services for updating the search indexes.
 * 
 * @author keshav
 *
 */
@Component
public class IndexServiceImpl implements IndexService {

    @Autowired
    private TaskRunningService taskRunningService;

    @Autowired
    @Qualifier("compassArray")
    private Compass compassArray;

    @Autowired
    @Qualifier("compassBibliographic")
    private Compass compassBibliographic;

    @Autowired
    @Qualifier("compassBiosequence")
    private Compass compassBiosequence;

    @Autowired
    @Qualifier("compassExperimentSet")
    private Compass compassExperimentSet;

    @Autowired
    @Qualifier("compassExpression")
    private Compass compassExpression;

    @Autowired
    @Qualifier("compassGene")
    private Compass compassGene;

    @Autowired
    @Qualifier("compassGeneSet")
    private Compass compassGeneSet;

    @Autowired
    @Qualifier("compassProbe")
    private Compass compassProbe;

    /**
     * Job that loads in grid. NOTE do not set this up to run on a worker. The search index directory may not be
     * accessible.
     * 
     * @author Paul
     *
     */
    // This is started locally. Calls two job: ReIndex and SwapIndices if needed.
    private class IndexerJob extends AbstractTask<IndexerResult, IndexerTaskCommand> {

        public IndexerJob( IndexerTaskCommand command ) {
            super( command );
        }

        @Override
        public IndexerResult execute() {
            IndexerResult result = null;
            String taskId = taskRunningService.submitRemoteTask( taskCommand );

            SubmittedTask<IndexerResult> indexingTask = taskRunningService.getSubmittedTask( taskId );
            try {
                TaskResult f = indexingTask.getResult();

                if ( f instanceof IndexerResult ) {
                    if ( indexingTask.isRunningRemotely() ) {
                        loadExternalIndices( taskCommand, result );
                    }
                    return ( IndexerResult ) f;
                }
                // probably there was an error.
                IndexerResult ir = new IndexerResult( taskCommand );
                ir.setException( f.getException() );

            } catch ( ExecutionException e ) {
                e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
            } catch ( InterruptedException e ) {
                e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
            }

            return result;
        }
    }

    /**
     * @param indexerTaskCommand
     * @param remoteIndexTaskResult
     */
    private void loadExternalIndices( IndexerTaskCommand indexerTaskCommand, IndexerResult remoteIndexTaskResult ) {
        /*
         * When the rebuild is done in another JVM, in the client the index must be 'swapped' to refer to the new one.
         * 
         * Put in multiple try catch blocks so that if one swapping fails they all don't fail :)
         */
        try {
            if ( indexerTaskCommand.isIndexGene() && remoteIndexTaskResult.getPathToGeneIndex() != null )
                CompassUtils.swapCompassIndex( compassGene, remoteIndexTaskResult.getPathToGeneIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexEE() && remoteIndexTaskResult.getPathToExpressionIndex() != null )
                CompassUtils.swapCompassIndex( compassExpression, remoteIndexTaskResult.getPathToExpressionIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexAD() && remoteIndexTaskResult.getPathToArrayIndex() != null )
                CompassUtils.swapCompassIndex( compassArray, remoteIndexTaskResult.getPathToArrayIndex() );

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexBibRef() && remoteIndexTaskResult.getPathToBibliographicIndex() != null )
                CompassUtils.swapCompassIndex( compassBibliographic,
                        remoteIndexTaskResult.getPathToBibliographicIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexBioSequence() && remoteIndexTaskResult.getPathToBiosequenceIndex() != null )
                CompassUtils.swapCompassIndex( compassBiosequence, remoteIndexTaskResult.getPathToBiosequenceIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        try {
            if ( indexerTaskCommand.isIndexProbe() && remoteIndexTaskResult.getPathToProbeIndex() != null )
                CompassUtils.swapCompassIndex( compassProbe, remoteIndexTaskResult.getPathToProbeIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexGeneSet() && remoteIndexTaskResult.getPathToGeneSetIndex() != null )
                CompassUtils.swapCompassIndex( compassGeneSet, remoteIndexTaskResult.getPathToGeneSetIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        try {
            if ( indexerTaskCommand.isIndexExperimentSet()
                    && remoteIndexTaskResult.getPathToExperimentSetIndex() != null )
                CompassUtils.swapCompassIndex( compassExperimentSet,
                        remoteIndexTaskResult.getPathToExperimentSetIndex() );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public String index( IndexerTaskCommand command ) {
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexAll() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setAll( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexArrayDesigns() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexAD( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexBibligraphicReferences() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexBibRef( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexBioSequences() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexBioSequence( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexExpressionExperiments() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexEE( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexGenes() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexGene( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }

    @Override
    public String indexProbes() {
        IndexerTaskCommand command = new IndexerTaskCommand();
        command.setIndexProbe( true );
        return taskRunningService.submitLocalTask( new IndexerJob( command ) );
    }
}
