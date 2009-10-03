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
package ubic.gemma.model.genome.sequenceAnalysis;

/**
 * 
 */
public interface BlastResultService {

    /**
     * 
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult create(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult );

    /**
     * 
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult find(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult resultToFind );

    /**
     * 
     */
    public ubic.gemma.model.genome.sequenceAnalysis.BlastResult findOrCreate(
            ubic.gemma.model.genome.sequenceAnalysis.BlastResult resultToFindOrCreate );

    /**
     * 
     */
    public void remove( ubic.gemma.model.genome.sequenceAnalysis.BlastResult blastResult );

}
