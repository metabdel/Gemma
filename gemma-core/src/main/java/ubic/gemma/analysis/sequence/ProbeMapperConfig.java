/*
 * The Gemma project Copyright (c) 2009 University of British Columbia Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */
package ubic.gemma.analysis.sequence;

import ubic.gemma.apps.Blat;
import ubic.gemma.model.genome.sequenceAnalysis.BlatResult;

/**
 * Holds parameters for how mapping should be done.
 * 
 * @author paul
 * @version $Id$
 */
public class ProbeMapperConfig {

    /**
     * Sequence identity below which we throw hits away.
     */
    public static final double DEFAULT_IDENTITY_THRESHOLD = 0.80;

    /**
     * BLAT score threshold below which we do not consider hits. This reflects the fraction of aligned bases.
     * 
     * @see Blat for the use of a similar parameter, used to determine the retention of raw Blat results.
     * @see BlatResult for how the score is computed.
     */
    public static final double DEFAULT_SCORE_THRESHOLD = 0.75;

    /**
     * Sequences which hybridize to this many or more sites in the genome are candidates to be considered non-specific.
     * This is used in combination with the REPEAT_FRACTION_MAXIMUM. Note that many sequences which contain repeats
     * nonetheless only align to very few sites in the genome.
     */
    public static final int NON_SPECIFIC_SITE_THRESHOLD = 3;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "# Configuration:\n# blatScoreThreshold=" + this.blatScoreThreshold + "\n# identityThreshold="
                + this.identityThreshold + "\n# maximumRepeatFraction=" + this.maximumRepeatFraction
                + "\n# nonSpecificSiteCountThreshold=" + this.nonSpecificSiteCountThreshold + "\n# useRefGene="
                + this.useRefGene + "\n# useAcembly=" + this.useAcembly + "\n# useNscan=" + this.useNscan
                + "\n# useEnsembl=" + this.useEnsembl + "\n# useMrnas=" + this.useMrnas + "\n# useMiRNA="
                + this.useMiRNA + "\n# useEsts=" + this.useEsts + "\n# useKnownGene=" + this.useKnownGene + "\n";

    }

    /**
     * Sequences which have more than this fraction accounted for by repeats (via repeatmasker) will not be examined if
     * they produce multiple alignments to the genome, regardless of the alignment quality.
     */
    public static final double REPEAT_FRACTION_MAXIMUM = 0.3;

    /**
     * Limit below which BLAT results are ignored. If BLAT was run with a threshold higher than this, it won't have any
     * effect.
     */
    private double blatScoreThreshold = DEFAULT_SCORE_THRESHOLD;

    private double identityThreshold = DEFAULT_IDENTITY_THRESHOLD;

    private double maximumRepeatFraction = REPEAT_FRACTION_MAXIMUM;

    /**
     * Sequences that contain more than the maximumRepeatFraction of repeat sequences AND which align to more than this
     * number of sites will be left unmapped. FIXME we might modify this behavior.
     */
    private double nonSpecificSiteCountThreshold = NON_SPECIFIC_SITE_THRESHOLD;

    private boolean useAcembly = true;

    private boolean useEnsembl = true;

    /**
     * This is the only track off by default.
     */
    private boolean useEsts = false;

    private boolean useKnownGene = true;

    private boolean useMiRNA = true;

    private boolean useMrnas = true;

    private boolean useNscan = true;

    private boolean useRefGene = true;

    /**
     * @return the blatScoreThreshold
     */
    public double getBlatScoreThreshold() {
        return blatScoreThreshold;
    }

    /**
     * @return the identityThreshold
     */
    public double getIdentityThreshold() {
        return identityThreshold;
    }

    /**
     * @return the maximumRepeatFraction
     */
    public double getMaximumRepeatFraction() {
        return maximumRepeatFraction;
    }

    /**
     * @return the nonSpecificSiteCountThreshold
     */
    public double getNonSpecificSiteCountThreshold() {
        return nonSpecificSiteCountThreshold;
    }

    /**
     * @return the useAcembly
     */
    public boolean isUseAcembly() {
        return useAcembly;
    }

    /**
     * @return the useEnsembl
     */
    public boolean isUseEnsembl() {
        return useEnsembl;
    }

    /**
     * @return the useEsts
     */
    public boolean isUseEsts() {
        return useEsts;
    }

    /**
     * @return the useKnownGene
     */
    public boolean isUseKnownGene() {
        return useKnownGene;
    }

    /**
     * @return the useMiRNA
     */
    public boolean isUseMiRNA() {
        return useMiRNA;
    }

    /**
     * @return the useMrnas
     */
    public boolean isUseMrnas() {
        return useMrnas;
    }

    /**
     * @return the useNscan
     */
    public boolean isUseNscan() {
        return useNscan;
    }

    /**
     * @return the useRefGene
     */
    public boolean isUseRefGene() {
        return useRefGene;
    }

    /**
     * Set to use no tracks. Obviously then nothing will be found, so it is wise to then switch some tracks on.
     */
    public void setAllTracksOff() {
        setUseEsts( false );
        setUseMrnas( false );
        setUseMiRNA( false );
        setUseEnsembl( false );
        setUseNscan( false );
        setUseRefGene( false );
        setUseKnownGene( false );
        setUseAcembly( false );
    }

    /**
     * Set to use all tracks, including ESTs
     */
    public void setAllTracksOn() {
        setUseEsts( true );
        setUseMrnas( true );
        setUseMiRNA( true );
        setUseEnsembl( true );
        setUseNscan( true );
        setUseRefGene( true );
        setUseKnownGene( true );
        setUseAcembly( true );
    }

    /**
     * @param blatScoreThreshold the blatScoreThreshold to set
     */
    public void setBlatScoreThreshold( double blatScoreThreshold ) {
        this.blatScoreThreshold = blatScoreThreshold;
    }

    /**
     * @param identityThreshold the identityThreshold to set
     */
    public void setIdentityThreshold( double identityThreshold ) {
        this.identityThreshold = identityThreshold;
    }

    /**
     * @param maximumRepeatFraction the maximumRepeatFraction to set
     */
    public void setMaximumRepeatFraction( double maximumRepeatFraction ) {
        this.maximumRepeatFraction = maximumRepeatFraction;
    }

    /**
     * @param nonSpecificSiteCountThreshold the nonSpecificSiteCountThreshold to set
     */
    public void setNonSpecificSiteCountThreshold( double nonSpecificSiteCountThreshold ) {
        this.nonSpecificSiteCountThreshold = nonSpecificSiteCountThreshold;
    }

    /**
     * @param useAcembly the useAcembly to set
     */
    public void setUseAcembly( boolean useAcembly ) {
        this.useAcembly = useAcembly;
    }

    /**
     * @param useEnsembl the useEnsembl to set
     */
    public void setUseEnsembl( boolean useEnsembl ) {
        this.useEnsembl = useEnsembl;
    }

    /**
     * @param useEsts the useEsts to set
     */
    public void setUseEsts( boolean useEsts ) {
        this.useEsts = useEsts;
    }

    /**
     * @param useKnownGene the useKnownGene to set
     */
    public void setUseKnownGene( boolean useKnownGene ) {
        this.useKnownGene = useKnownGene;
    }

    /**
     * @param useMiRNA the useMiRNA to set
     */
    public void setUseMiRNA( boolean useMiRNA ) {
        this.useMiRNA = useMiRNA;
    }

    /**
     * @param useMrnas the useMrnas to set
     */
    public void setUseMrnas( boolean useMrnas ) {
        this.useMrnas = useMrnas;
    }

    /**
     * @param useNscan the useNscan to set
     */
    public void setUseNscan( boolean useNscan ) {
        this.useNscan = useNscan;
    }

    /**
     * @param useRefGene the useRefGene to set
     */
    public void setUseRefGene( boolean useRefGene ) {
        this.useRefGene = useRefGene;
    }

}
