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
package ubic.gemma.web.controller.expression.experiment;

import java.util.List;

import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentImpl;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ExpressionExperimentEditCommand extends ExpressionExperimentImpl {

    private List<QuantitationType> quantitationTypes;
    private ExpressionExperiment expressionExperiment;

    public ExpressionExperimentEditCommand() {
    }

    public ExpressionExperimentEditCommand( ExpressionExperiment ee, List<QuantitationType> quantitationTypes ) {
        this.quantitationTypes = quantitationTypes;
        this.expressionExperiment = ee;
        this.setName( ee.getName() );
        this.setDescription( ee.getDescription() );
        this.setBioAssays( ee.getBioAssays() );
        this.setAuditTrail( ee.getAuditTrail() );
        this.setExperimentalDesign( ee.getExperimentalDesign() );
        this.setOtherRelevantPublications( ee.getOtherRelevantPublications() );
        this.setPrimaryPublication( ee.getPrimaryPublication() );
        this.setShortName( ee.getShortName() );
        this.setSource( ee.getSource() );
        this.setInvestigators( ee.getInvestigators() );
        this.setId( ee.getId() );
        this.setAccession( ee.getAccession() );
        this.setSubsets( ee.getSubsets() );
        this.setOwner( ee.getOwner() );
        this.setRawDataFile( ee.getRawDataFile() );
    }

    public List<QuantitationType> getQuantitationTypes() {
        return quantitationTypes;
    }

    public void setQuantitationTypes( List<QuantitationType> quantitationTypes ) {
        this.quantitationTypes = quantitationTypes;
    }

    /**
     * Copy values over.
     * 
     * @return
     */
    public ExpressionExperiment toExpressionExperiment() {
        expressionExperiment.setName( this.getName() );
        expressionExperiment.setDescription( this.getDescription() );
        expressionExperiment.setBioAssays( this.getBioAssays() );
        expressionExperiment.setAuditTrail( this.getAuditTrail() );
        expressionExperiment.setExperimentalDesign( this.getExperimentalDesign() );
        expressionExperiment.setOtherRelevantPublications( this.getOtherRelevantPublications() );
        expressionExperiment.setPrimaryPublication( this.getPrimaryPublication() );
        expressionExperiment.setShortName( this.getShortName() );
        expressionExperiment.setSource( this.getSource() );
        expressionExperiment.setInvestigators( this.getInvestigators() );
        expressionExperiment.setId( this.getId() ); // maybe not...
        expressionExperiment.setAccession( this.getAccession() );
        expressionExperiment.setSubsets( this.getSubsets() );
        expressionExperiment.setOwner( this.getOwner() );
        expressionExperiment.setBioAssayDataVectors( this.getBioAssayDataVectors() );
        expressionExperiment.setDesignElementDataVectors( this.getDesignElementDataVectors() );
        expressionExperiment.setRawDataFile( this.getRawDataFile() );
        return expressionExperiment;
    }

}
