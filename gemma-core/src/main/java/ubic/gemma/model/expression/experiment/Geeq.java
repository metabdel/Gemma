/*
 * The gemma project
 *
 * Copyright (c) 2015 University of British Columbia
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

import ubic.gemma.model.common.Identifiable;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents quality information about a data set. The class name comes from the research project name, GEEQ.
 * The score has two components: Quality and Suitability. See the variables javadoc for further description.
 * The scoring rules are implemented in the GeeqServiceImpl, which also exposes public methods for experiment
 * scoring.
 *
 * @author paul, tesarst
 */
public class Geeq implements Identifiable, Serializable {

    private static final long serialVersionUID = 4783171234360698630L;
    private Long id;
    private AuditEvent lastRun;
    private AuditEvent lastManualOverride;
    private AuditEvent lastBatchEffectChange;
    private AuditEvent lastBatchConfoundChange;

    /**
     * Quality refers to data quality, wherein the same study could have been done twice with the same technical
     * parameters and in one case yield bad quality data, and in another high quality data.
     * The quality score can be overridden. The manual value is stored in manualQualityScore, while
     * manualQualityOverride boolean value denotes whether the manual value should be used.
     */
    private double detectedQualityScore;
    private double manualQualityScore;
    private boolean manualQualityOverride;

    /**
     * Suitability mostly refers to technical aspects which, if we were doing the study ourselves, we would have
     * altered to make it optimal for analyses of the sort used in Gemma.
     * The suitability score can be overridden. The manual value is stored in manualSuitabilityScore, while
     * manualSuitabilityOverride boolean value denotes whether the manual value should be used.
     */
    private double detectedSuitabilityScore;
    private double manualSuitabilityScore;
    private boolean manualSuitabilityOverride;

    /*
     * Suitability score factors
     */

    /**
     * -1.0 - if experiment has no publication
     * -0.7 - if date not filled in
     * -0.5 if date < 2006
     * -0.3 if date < 2009
     * +1.0 otherwise
     */
    private double sScorePublication;

    /**
     * The amount of platforms the experiment uses:
     * -1.0 if amount > 2
     * -0.5 if amount > 1
     * +1.0 otherwise
     */
    private double sScorePlatformAmount;

    /**
     * Extra punishment for platform technology inconsistency
     * -1.0 if platforms amount > 1 and platforms do not have the same technology type
     * +1.0 otherwise
     */
    private double sScorePlatformsTechMulti;

    /**
     * Score for each platforms popularity: (final score is average of scores for all used platforms)
     * -1.0 if used in < 10 EEs
     * -0.5 if used in < 20 EEs
     * +0.0 if used in < 50 EEs
     * +0.5 if used in < 100 EEs
     * +1.0 otherwise
     */
    private double sScoreAvgPlatformPopularity;

    /**
     * Score for each platforms size: (final score is average of scores for all used platforms)
     * -1.0 if gene count < 5k
     * -0.5 if gene count < 10k
     * +0.0 if gene count < 15k
     * +0.5 if gene count < 18k
     * +1.0 otherwise
     */
    private double sScoreAvgPlatformSize;

    /**
     * The amount of samples in the experiment
     * -1.0 if sample size < 20
     * -0.5 if sample size < 50
     * +0.0 if sample size < 100
     * +0.5 if sample size < 200
     * +1.0 otherwise
     */
    private double sScoreSampleSize;

    /**
     * Raw data availability (shows also as the 'external' badge)
     * -1.0 if no raw data available
     * +1.0 otherwise
     */
    private double sScoreRawData;

    /**
     * Missing values
     * -1.0 if experiment has any missing values
     * +1.0 otherwise (also assumed if experiment has raw data available)
     * extra:
     * -0.0001 if experiment has no computed vectors
     */
    private double sScoreMissingValues;

    /*
     * Quality score factors
     */

    /**
     * Ratio of detected (non-removed) outliers vs sample size:
     * -1.0 if ratio > 5%
     * -0.5 if ratio > 2%
     * +0.0 if ratio > 0.1%
     * +0.5 if ratio > 0% small punishment for very large experiments with one bad apple
     * +1.0 if ratio = 0%
     * extra:
     * -0.0001 if the correlation matrix is empty
     */
    private double qScoreOutliers;

    /**
     * Using the mean sample correlation r:
     * +r use the computed value
     * extra
     * -0.0001 if the correlation matrix is empty
     */
    private double qScoreSampleMeanCorrelation;

    /**
     * Using the median sample correlation m:
     * +m use the computed value
     * extra
     * -0.0001 if the correlation matrix is empty
     */
    private double qScoreSampleMedianCorrelation;

    /**
     * Using the sample correlation variance v:
     * +v use the computed value
     * extra
     * -0.0001 if the correlation matrix is empty
     */
    private double qScoreSampleCorrelationVariance;

    /**
     * Platform technologies
     * -1.0 if any platform is two-color
     * +1.0 otherwise
     */
    private double qScorePlatformsTech;

    /**
     * Number of replicates - ee has to have design and more than one condition
     * -1.0 if lowest replicate amount < 4 & !=1
     * +0.0 if lowest replicate amount < 10 & !=1
     * +1.0 otherwise
     * extra:
     * -0.0001 if the experiment has no design
     * -0.0002 if there were no factor values found
     * -0.0003 if all replicate amounts were 1
     * -0.0004 if lowest replicate was 0 (that really should not happen though)
     */
    private double qScoreReplicates;

    /**
     * State of batch info
     * -1.0 if no batch info available
     * +1.0 otherwise
     */
    private double qScoreBatchInfo;

    /**
     * Batch effect without batch correction. Can ve overridden.
     * -1.0 if batch pVal < 0.0001 or (manualHasStrongBatchEffect & manualBatchEffectActive);
     * +1.0 if batch pVal > 0.1 or (!manualHasNoBatchEffect & manualBatchEffectActive);
     * +0.0 otherwise
     * extra:
     * +0.0001 if data was batch-corrected
     */
    private double qScoreBatchEffect;

    private boolean manualHasStrongBatchEffect;
    private boolean manualHasNoBatchEffect;
    private boolean manualBatchEffectActive;

    /**
     * Batch confound
     * -1.0 if data confound detected or (manualHasBatchConfound & manualBatchConfoundActive)
     * +1.0 otherwise
     */
    private double qScoreBatchConfound;

    private boolean manualHasBatchConfound;
    private boolean manualBatchConfoundActive;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( !( o instanceof Geeq ) )
            return false;
        Geeq geeq = ( Geeq ) o;
        return Objects.equals( getId(), geeq.getId() );
    }

    @Override
    public int hashCode() {
        return Objects.hash( getId() );
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Required by hibernate
     *
     * @param id the unique ID of the instance this object represents
     */
    private void setId( Long id ) {
        this.id = id;
    }

    @Transient
    public double[] getSuitabilityScoreArray() {
        return new double[] { this.sScorePublication, this.sScorePlatformAmount, this.sScorePlatformsTechMulti,
                this.sScoreAvgPlatformPopularity, this.sScoreAvgPlatformSize, this.sScoreSampleSize, this.sScoreRawData,
                this.sScoreMissingValues };
    }

    @Transient
    public double[] getQualityScoreArray() {
        return new double[] { this.qScoreOutliers, this.qScoreSampleMeanCorrelation, this.qScoreSampleMedianCorrelation,
                this.qScoreSampleCorrelationVariance, this.qScorePlatformsTech, this.qScoreReplicates,
                this.qScoreBatchInfo, this.qScoreBatchEffect, this.qScoreBatchConfound };
    }

    @Transient
    public double[] getSuitabilityScoreWeightsArray() {
        return new double[] { 1, 1, 1, 1, 1, 1, 1, 1 };
    }

    @Transient
    public double[] getQualityScoreWeightsArray() {
        return new double[] { 1, 0, 1, 0, 1, 1, 1, 1, 1 };
    }

    public double getDetectedQualityScore() {
        return detectedQualityScore;
    }

    public void setDetectedQualityScore( double detectedQualityScore ) {
        this.detectedQualityScore = detectedQualityScore;
    }

    public double getManualQualityScore() {
        return manualQualityScore;
    }

    public void setManualQualityScore( double manualQualityScore ) {
        this.manualQualityScore = manualQualityScore;
    }

    public boolean getManualQualityOverride() {
        return manualQualityOverride;
    }

    public void setManualQualityOverride( boolean manualQualityOverride ) {
        this.manualQualityOverride = manualQualityOverride;
    }

    public double getDetectedSuitabilityScore() {
        return detectedSuitabilityScore;
    }

    public void setDetectedSuitabilityScore( double detectedSuitabilityScore ) {
        this.detectedSuitabilityScore = detectedSuitabilityScore;
    }

    public double getManualSuitabilityScore() {
        return manualSuitabilityScore;
    }

    public void setManualSuitabilityScore( double manualSuitabilityScore ) {
        this.manualSuitabilityScore = manualSuitabilityScore;
    }

    public boolean getManualSuitabilityOverride() {
        return manualSuitabilityOverride;
    }

    public void setManualSuitabilityOverride( boolean manualSuitabilityOverride ) {
        this.manualSuitabilityOverride = manualSuitabilityOverride;
    }

    public double getSScorePublication() {
        return sScorePublication;
    }

    public void setSScorePublication( double sScorePublicationDate ) {
        this.sScorePublication = sScorePublicationDate;
    }

    public double getSScorePlatformAmount() {
        return sScorePlatformAmount;
    }

    public void setSScorePlatformAmount( double sScorePlatformAmount ) {
        this.sScorePlatformAmount = sScorePlatformAmount;
    }

    public double getSScorePlatformsTechMulti() {
        return sScorePlatformsTechMulti;
    }

    public void setSScorePlatformsTechMulti( double sScorePlatformsTechMulti ) {
        this.sScorePlatformsTechMulti = sScorePlatformsTechMulti;
    }

    public double getSScoreAvgPlatformPopularity() {
        return sScoreAvgPlatformPopularity;
    }

    public void setSScoreAvgPlatformPopularity( double sScoreAvgPlatformPopularity ) {
        this.sScoreAvgPlatformPopularity = sScoreAvgPlatformPopularity;
    }

    public double getSScoreAvgPlatformSize() {
        return sScoreAvgPlatformSize;
    }

    public void setSScoreAvgPlatformSize( double sScoreAvgPlatformSize ) {
        this.sScoreAvgPlatformSize = sScoreAvgPlatformSize;
    }

    public double getSScoreSampleSize() {
        return sScoreSampleSize;
    }

    public void setSScoreSampleSize( double sScoreSampleSize ) {
        this.sScoreSampleSize = sScoreSampleSize;
    }

    public double getSScoreRawData() {
        return sScoreRawData;
    }

    public void setSScoreRawData( double sScoreRawData ) {
        this.sScoreRawData = sScoreRawData;
    }

    public double getSScoreMissingValues() {
        return sScoreMissingValues;
    }

    public void setSScoreMissingValues( double sScoreMissingValues ) {
        this.sScoreMissingValues = sScoreMissingValues;
    }

    public double getQScoreOutliers() {
        return qScoreOutliers;
    }

    public void setQScoreOutliers( double qScoreOutliers ) {
        this.qScoreOutliers = qScoreOutliers;
    }

    public double getQScorePlatformsTech() {
        return qScorePlatformsTech;
    }

    public void setQScorePlatformsTech( double qScorePlatformsTech ) {
        this.qScorePlatformsTech = qScorePlatformsTech;
    }

    public double getQScoreReplicates() {
        return qScoreReplicates;
    }

    public void setQScoreReplicates( double qScoreReplicates ) {
        this.qScoreReplicates = qScoreReplicates;
    }

    public double getQScoreBatchInfo() {
        return qScoreBatchInfo;
    }

    public void setQScoreBatchInfo( double qScoreBatchInfo ) {
        this.qScoreBatchInfo = qScoreBatchInfo;
    }

    public double getQScoreBatchEffect() {
        return qScoreBatchEffect;
    }

    public void setQScoreBatchEffect( double qScoreBatchEffect ) {
        this.qScoreBatchEffect = qScoreBatchEffect;
    }

    public boolean getManualHasStrongBatchEffect() {
        return manualHasStrongBatchEffect;
    }

    public void setManualHasStrongBatchEffect( boolean manualHasStrongBatchEffect ) {
        this.manualHasStrongBatchEffect = manualHasStrongBatchEffect;
    }

    public boolean getManualHasNoBatchEffect() {
        return manualHasNoBatchEffect;
    }

    public void setManualHasNoBatchEffect( boolean manualHasNoBatchEffect ) {
        this.manualHasNoBatchEffect = manualHasNoBatchEffect;
    }

    public boolean getManualBatchEffectActive() {
        return manualBatchEffectActive;
    }

    public void setManualBatchEffectActive( boolean manualBatchEffectOverride ) {
        this.manualBatchEffectActive = manualBatchEffectOverride;
    }

    public double getQScoreBatchConfound() {
        return qScoreBatchConfound;
    }

    public void setQScoreBatchConfound( double qScoreBatchConfound ) {
        this.qScoreBatchConfound = qScoreBatchConfound;
    }

    public boolean getManualHasBatchConfound() {
        return manualHasBatchConfound;
    }

    public void setManualHasBatchConfound( boolean manualHasBatchConfound ) {
        this.manualHasBatchConfound = manualHasBatchConfound;
    }

    public boolean getManualBatchConfoundActive() {
        return manualBatchConfoundActive;
    }

    public void setManualBatchConfoundActive( boolean manualBatchConfoundActive ) {
        this.manualBatchConfoundActive = manualBatchConfoundActive;
    }

    public double getQScoreSampleMeanCorrelation() {
        return qScoreSampleMeanCorrelation;
    }

    public void setQScoreSampleMeanCorrelation( double qScoreSampleMeanCorrelation ) {
        this.qScoreSampleMeanCorrelation = qScoreSampleMeanCorrelation;
    }

    public double getQScoreSampleMedianCorrelation() {
        return qScoreSampleMedianCorrelation;
    }

    public void setQScoreSampleMedianCorrelation( double qScoreSampleMedianCorrelation ) {
        this.qScoreSampleMedianCorrelation = qScoreSampleMedianCorrelation;
    }

    public double getQScoreSampleCorrelationVariance() {
        return qScoreSampleCorrelationVariance;
    }

    public void setQScoreSampleCorrelationVariance( double qScoreSampleCorrelationVariance ) {
        this.qScoreSampleCorrelationVariance = qScoreSampleCorrelationVariance;
    }

    public AuditEvent getLastRun() {
        return lastRun;
    }

    public void setLastRun( AuditEvent lastRun ) {
        this.lastRun = lastRun;
    }

    public AuditEvent getLastManualOverride() {
        return lastManualOverride;
    }

    public void setLastManualOverride( AuditEvent lastManualOverride ) {
        this.lastManualOverride = lastManualOverride;
    }

    public AuditEvent getLastBatchEffectChange() {
        return lastBatchEffectChange;
    }

    public void setLastBatchEffectChange( AuditEvent lastBatchEffectChange ) {
        this.lastBatchEffectChange = lastBatchEffectChange;
    }

    public AuditEvent getLastBatchConfoundChange() {
        return lastBatchConfoundChange;
    }

    public void setLastBatchConfoundChange( AuditEvent lastBatchConfoundChange ) {
        this.lastBatchConfoundChange = lastBatchConfoundChange;
    }
}
