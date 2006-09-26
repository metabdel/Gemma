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
package ubic.gemma.web.taglib.displaytag.expression.experiment;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.displaytag.decorator.TableDecorator;

import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.experiment.ExperimentalDesign;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Taxon;

/**
 * Used to generate hyperlinks in displaytag tables.
 * <p>
 * See http://displaytag.sourceforge.net/10/tut_decorators.html and http://displaytag.sourceforge.net/10/tut_links.html
 * for explanation of how this works.
 * 
 * @author pavlidis
 * @version $Id$
 */
public class ExpressionExperimentWrapper extends TableDecorator {

    Log log = LogFactory.getLog( this.getClass() );

    /**
     * @return String
     */
    public String getSourceLink() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getSource() != null ) {
            return "<a href=\"" + object.getAccession().getExternalDatabase().getWebUri() + "\">" + object.getSource()
                    + "</a>";
        }
        return "No Source";
    }

    /**
     * @return String
     */
    public String getDetailsLink() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getAccession() != null ) {
            return "<a href=\"showExpressionExperiment.html?id=" + object.getId() + "\">" + getDetails() + "</a>";
        }
        return "No accession";
    }

    /**
     * Return detail string for an expression experiment
     * 
     * @return String
     */
    public String getDetails() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getAccession() != null ) {
            return object.getAccession().getExternalDatabase().getName() + " - " + object.getAccession().getAccession();
        }
        return "No accession";
    }

    /**
     * @return String
     */
    public String getAssaysLink() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getBioAssays() != null ) {
            return "<a href=\"showBioAssaysFromExpressionExperiment.html?id=" + object.getId() + "\">"
                    + object.getBioAssays().size() + "</a>";
        }

        return "No bioassays";
    }

    /**
     * @return String
     */
    public String getFactorsLink() {

        log.debug( getCurrentRowObject() );

        ExperimentalDesign object = ( ExperimentalDesign ) getCurrentRowObject();

        if ( object != null && object.getExperimentalFactors() != null ) {
            return "<a href=\"/Gemma/experimentalDesign/showExperimentalDesign.html?id=" + object.getId() + "\">"
                    + object.getExperimentalFactors().size() + "</a>";
        }
        return "No experimental factors";
    }

    /**
     * @return String
     */
    public String getExperimentalDesignNameLink() {

        log.debug( getCurrentRowObject() );

        ExperimentalDesign object = ( ExperimentalDesign ) getCurrentRowObject();
        String name = object.getName();
        if ( ( name == null ) || ( name.length() == 0 ) ) {
            name = "No name";
        }
        return "<a href=\"/Gemma/experimentalDesign/showExperimentalDesign.html?id=" + object.getId() + "\">" + name
                + "</a>";
    }

    /**
     * @return String
     */
    public String getDesignsLink() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getExperimentalDesign() != null ) {
            return "<a href=\"showExpressionExperiment.html?name=" + object.getName() + "\"> </a>";
        }
        return "No design";
    }

    /**
     * link to the expression experiment view, with the name as the link view
     * 
     * @return String
     */
    public String getNameLink() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getExperimentalDesign() != null ) {
            return "<a href=\"showExpressionExperiment.html?id=" + object.getId() + "\">" + object.getName() + "</a>";
        }
        return "No design";
    }

    /**
     * @return The creation date.
     */
    public String getCreateDate() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object != null && object.getAuditTrail() != null && object.getAuditTrail().getCreationEvent() != null
                && object.getAuditTrail().getCreationEvent().getDate() != null ) {

            Date date = object.getAuditTrail().getCreationEvent().getDate();

            SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy" );
            dateFormat.setLenient( false );

            return dateFormat.format( date );
        }
        return "Creation date unavailable";
    }

    /**
     * @return View for key of the quantitation type counts.
     */
    public String getQtName() {
        Map.Entry entry = ( Map.Entry ) getCurrentRowObject();
        return ( String ) entry.getKey();
    }

    /**
     * @return View for value of the quantitation type counts.
     */
    public Integer getQtValue() {
        Map.Entry entry = ( Map.Entry ) getCurrentRowObject();
        return ( Integer ) entry.getValue();
    }

    /**
     * @return
     */
    public String getTaxon() {
        ExpressionExperiment object = ( ExpressionExperiment ) getCurrentRowObject();
        if ( object == null ) {
            return "Taxon unavailable";
        }

        Collection bioAssayCol = object.getBioAssays();
        BioAssay bioAssay = null;
        Taxon taxon = null;

        if ( bioAssayCol != null && bioAssayCol.size() > 0 ) {
            bioAssay = ( BioAssay ) bioAssayCol.iterator().next();
        } else {
            return "Taxon unavailable";
        }

        Collection bioMaterialCol = bioAssay.getSamplesUsed();
        if ( bioMaterialCol != null && bioMaterialCol.size() != 0 ) {
            BioMaterial bioMaterial = ( BioMaterial ) bioMaterialCol.iterator().next();
            taxon = bioMaterial.getSourceTaxon();
        } else {
            return "Taxon unavailable";
        }

        if ( taxon != null ) return taxon.getScientificName();

        return "Taxon unavailable";
    }
}
