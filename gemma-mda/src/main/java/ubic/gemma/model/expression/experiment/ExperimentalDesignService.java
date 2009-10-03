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
package ubic.gemma.model.expression.experiment;

/**
 * 
 */
public interface ExperimentalDesignService extends ubic.gemma.model.common.AuditableService {

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExperimentalDesign create(
            ubic.gemma.model.expression.experiment.ExperimentalDesign experimentalDesign );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExperimentalDesign find(
            ubic.gemma.model.expression.experiment.ExperimentalDesign experimentalDesign );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExperimentalDesign findByName( java.lang.String name );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExperimentalDesign findOrCreate(
            ubic.gemma.model.expression.experiment.ExperimentalDesign experimentalDesign );

    /**
     * <p>
     * gets the expression experiment for the specified experimental design object
     * </p>
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment getExpressionExperiment(
            ubic.gemma.model.expression.experiment.ExperimentalDesign experimentalDesign );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExperimentalDesign load( java.lang.Long id );

    /**
     * 
     */
    public java.util.Collection<ExperimentalDesign> loadAll();

    /**
     * 
     */
    public void update( ubic.gemma.model.expression.experiment.ExperimentalDesign experimentalDesign );

}
