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

import java.util.Collection;
import java.util.Map;

import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.quantitationtype.QuantitationType;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssayData.BioAssayDimension;
import ubic.gemma.model.expression.bioAssayData.ProcessedExpressionDataVector;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;

/**
 * 
 */
public interface ExpressionExperimentService extends ubic.gemma.model.common.AuditableService {

    /**
     * <p>
     * Count how many ExpressionExperiments are in the database
     * </p>
     */
    public java.lang.Integer countAll();

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment create(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Deletes an experiment and all of its associated objects, including coexpression links. Some types of associated
     * objects may need to be deleted before this can be run (example: analyses involving multiple experiments; these
     * will not be deleted automatically, though this behavior could be changed)
     * </p>
     */
    public void delete( ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment find(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findByAccession(
            ubic.gemma.model.common.description.DatabaseEntry accession );

    /**
     * <p>
     * given a bibliographicReference returns a collection of EE that have that reference that BibliographicReference
     * </p>
     */
    public java.util.Collection<ExpressionExperiment> findByBibliographicReference(
            ubic.gemma.model.common.description.BibliographicReference bibRef );

    /**
     * <p>
     * Given a bioMaterial returns an expressionExperiment
     * </p>
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findByBioMaterial(
            ubic.gemma.model.expression.biomaterial.BioMaterial bm );

    /**
     * 
     */
    public java.util.Collection<ExpressionExperiment> findByBioMaterials( java.util.Collection<BioMaterial> bioMaterials );

    /**
     * <p>
     * Returns a collection of expression experiment ids that express the given gene above the given expression level
     * </p>
     */
    public java.util.Collection<ExpressionExperiment> findByExpressedGene( ubic.gemma.model.genome.Gene gene,
            double rank );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findByFactorValue(
            ubic.gemma.model.expression.experiment.FactorValue factorValue );

    /**
     * 
     */
    public java.util.Collection<ExpressionExperiment> findByFactorValues( java.util.Collection<FactorValue> factorValues );

    /**
     * <p>
     * Returns a collection of expression experiments that have an AD that detects the given Gene (ie a probe on the AD
     * hybidizes to the given Gene)
     * </p>
     */
    public java.util.Collection<ExpressionExperiment> findByGene( ubic.gemma.model.genome.Gene gene );

    /**
     * 
     */
    public java.util.Collection<ExpressionExperiment> findByInvestigator(
            ubic.gemma.model.common.auditAndSecurity.Contact investigator );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findByName( java.lang.String name );

    public ExpressionExperiment findByQuantitationType( QuantitationType type );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findByShortName( java.lang.String shortName );

    /**
     * <p>
     * gets all EE that match the given Taxon
     * </p>
     */
    public java.util.Collection<ExpressionExperiment> findByTaxon( ubic.gemma.model.genome.Taxon taxon );

    /**
     *  
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment findOrCreate(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Get the map of ids to number of terms associated with each expression experiment.
     * </p>
     */
    public java.util.Map getAnnotationCounts( java.util.Collection<Long> ids );

    /**
     * <p>
     * Returns a collection of ArrayDesigns referenced by any of the BioAssays that make up the given
     * ExpressionExperiment.
     * </p>
     */
    public java.util.Collection<ArrayDesign> getArrayDesignsUsed(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Counts the number of biomaterials associated with this expression experiment.
     * </p>
     */
    public long getBioMaterialCount( ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * Retrieve the BioAssayDimensions for the study.
     * 
     * @param expressionExperiment
     * @return
     */
    public Collection<BioAssayDimension> getBioAssayDimensions( ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public long getDesignElementDataVectorCountById( long id );

    /**
     * <p>
     * Get all the vectors for the given expression experiment, but limited to the given quantitation types.
     * </p>
     */
    public java.util.Collection getDesignElementDataVectors( java.util.Collection<QuantitationType> quantitationTypes );

    /**
     * <p>
     * Find vectors for the given expression experiment, constrained to the given quantitation type and design elements
     * </p>
     */
    public java.util.Collection getDesignElementDataVectors( java.util.Collection designElements,
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType );

    /**
     * 
     */
    public java.util.Map getLastArrayDesignUpdate( java.util.Collection expressionExperiments, java.lang.Class type );

    /**
     * <p>
     * Get the date of the last time any of the array designs associated with this experiment were updated.
     * </p>
     */
    public ubic.gemma.model.common.auditAndSecurity.AuditEvent getLastArrayDesignUpdate(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment, java.lang.Class eventType );

    /**
     * <p>
     * Gets the AuditEvents of the latest link analyses for the specified expression experiment ids. This returns a map
     * of id -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map getLastLinkAnalysis( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets the AuditEvents of the latest missing value analysis for the specified expression experiment ids. This
     * returns a map of id -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map getLastMissingValueAnalysis( java.util.Collection<Long> ids );

    /**
     * <p>
     * Gets the AuditEvents of the latest rank computation for the specified expression experiment ids. This returns a
     * map of id -> AuditEvent. If the events do not exist, the map entry will point to null.
     * </p>
     */
    public java.util.Map getLastProcessedDataUpdate( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map getLastTroubleEvent( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Map getLastValidationEvent( java.util.Collection<Long> ids );

    /**
     * <p>
     * Function to get a count of expression experiments, grouped by Taxon
     * </p>
     */
    public java.util.Map<Taxon, Long> getPerTaxonCount();

    /**
     * <p>
     * Get map of ids to how many factor values the experiment has, counting only factor values which are associated
     * with biomaterials.
     * </p>
     */
    public java.util.Map getPopulatedFactorCounts( java.util.Collection<Long> ids );

    /**
     * <p>
     * Iterates over the quantiation types for a given expression experiment and returns the preferred quantitation
     * types.
     * </p>
     */
    public java.util.Collection getPreferredQuantitationType(
            ubic.gemma.model.expression.experiment.ExpressionExperiment EE );

    public Collection<ProcessedExpressionDataVector> getProcessedDataVectors( ExpressionExperiment ee );

    /**
     * <p>
     * Counts the number of ProcessedExpressionDataVectors.
     * </p>
     */
    public long getProcessedExpressionVectorCount(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Function to get a count of an expressionExperiment's designelementdatavectors, grouped by quantitation type
     * </p>
     */
    public java.util.Map<QuantitationType, Long> getQuantitationTypeCountById( java.lang.Long Id );

    /**
     * <p>
     * Return all the quantitation types used by the given expression experiment
     * </p>
     */
    public java.util.Collection<QuantitationType> getQuantitationTypes(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Get the quantitation types for the expression experiment, for the array design specified. This is really only
     * useful for expression experiments that use more than one array design.
     * </p>
     */
    public java.util.Collection<QuantitationType> getQuantitationTypes(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment,
            ubic.gemma.model.expression.arrayDesign.ArrayDesign arrayDesign );

    /**
     * 
     */
    public Map<ExpressionExperiment, Collection<AuditEvent>> getSampleRemovalEvents(
            java.util.Collection<ExpressionExperiment> expressionExperiments );

    /**
     * <p>
     * Retrieve some of the vectors for the given expressionExperiment and quantitation type. Used for peeking at the
     * data without retrieving the whole data set.
     * </p>
     */
    public java.util.Collection getSamplingOfVectors(
            ubic.gemma.model.common.quantitationtype.QuantitationType quantitationType, java.lang.Integer limit );

    /**
     * <p>
     * Return any ExpressionExperimentSubSets this Experiment might have.
     * </p>
     */
    public java.util.Collection<ExpressionExperimentSubSet> getSubSets(
            ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Returns the taxon of the given expressionExperiment.
     * </p>
     */
    public ubic.gemma.model.genome.Taxon getTaxon( java.lang.Long ExpressionExperimentID );

    /**
     * 
     */
    public ubic.gemma.model.expression.experiment.ExpressionExperiment load( java.lang.Long id );

    /**
     * 
     */
    public java.util.Collection<ExpressionExperiment> loadAll();

    /**
     * 
     */
    public java.util.Collection<ExpressionExperimentValueObject> loadAllValueObjects();

    /**
     * 
     */
    public java.util.Collection<ExpressionExperiment> loadMultiple( java.util.Collection<Long> ids );

    /**
     * 
     */
    public java.util.Collection<ExpressionExperimentValueObject> loadValueObjects( java.util.Collection<Long> ids );

    /**
     * 
     */
    public void thaw( ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * <p>
     * Partially thaw the expression experiment given - do not thaw the raw data.
     * </p>
     */
    public void thawLite( ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

    /**
     * 
     */
    public void update( ubic.gemma.model.expression.experiment.ExpressionExperiment expressionExperiment );

}
