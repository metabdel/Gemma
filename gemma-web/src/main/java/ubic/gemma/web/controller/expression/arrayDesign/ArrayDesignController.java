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
package ubic.gemma.web.controller.expression.arrayDesign;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ubic.gemma.analysis.report.ArrayDesignReportService;
import ubic.gemma.analysis.report.ArrayDesignReportServiceImpl;
import ubic.gemma.analysis.sequence.ArrayDesignMapResultService;
import ubic.gemma.analysis.sequence.CompositeSequenceMapValueObject;
import ubic.gemma.analysis.service.ArrayDesignAnnotationService;
import ubic.gemma.model.common.auditAndSecurity.AuditEvent;
import ubic.gemma.model.common.auditAndSecurity.AuditTrailService;
import ubic.gemma.model.expression.arrayDesign.AlternateName;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignService;
import ubic.gemma.model.expression.arrayDesign.ArrayDesignValueObject;
import ubic.gemma.model.expression.arrayDesign.TechnologyType;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.designElement.CompositeSequenceService;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.search.SearchResult;
import ubic.gemma.search.SearchService;
import ubic.gemma.search.SearchSettings;
import ubic.gemma.security.SecurityService;
import ubic.gemma.util.progress.ProgressJob;
import ubic.gemma.util.progress.ProgressManager;
import ubic.gemma.web.controller.BackgroundControllerJob;
import ubic.gemma.web.controller.BackgroundProcessingMultiActionController;
import ubic.gemma.web.remote.EntityDelegator;
import ubic.gemma.web.taglib.arrayDesign.ArrayDesignHtmlUtil;
import ubic.gemma.web.taglib.displaytag.ArrayDesignValueObjectComparator;
import ubic.gemma.web.util.EntityNotFoundException;

/**
 * @author keshav
 * @version $Id$
 */
@Controller
@RequestMapping("/arrays")
public class ArrayDesignController extends BackgroundProcessingMultiActionController {

    /**
     * Inner class used for building array design summary
     */
    class GenerateSummary extends BackgroundControllerJob<ModelAndView> {

        private Long arrayDesignId;

        public GenerateSummary( HttpSession session ) {
            super( getMessageUtil(), session );
            arrayDesignId = null;
        }

        public GenerateSummary( Long id ) {
            this( null, id );
        }

        public GenerateSummary( HttpSession session, Long id ) {
            super( getMessageUtil(), session );
            this.arrayDesignId = id;
        }

        public ModelAndView call() throws Exception {

            ProgressJob job = init( "Generating ArrayDesign Report summary" );

            if ( arrayDesignId == null ) {
                if ( this.getDoForward() ) saveMessage( "Generated summary for all platforms" );
                job.updateProgress( "Generated summary for all platforms" );
                arrayDesignReportService.generateArrayDesignReport();
            } else {
                if ( this.getDoForward() ) saveMessage( "Generating summary for platform " + arrayDesignId );
                job.updateProgress( "Generating summary for specified platform" );
                ArrayDesignValueObject report = arrayDesignReportService.generateArrayDesignReport( arrayDesignId );
                job.setPayload( report );
            }

            // ProgressManager.destroyProgressJob( job, this.getDoForward() );
            return new ModelAndView( new RedirectView( "/Gemma/arrays/showAllArrayDesignStatistics.html" ) );

        }
    }

    /**
     * Inner class used for deleting array designs
     */
    class RemoveArrayJob extends BackgroundControllerJob<ModelAndView> {

        private ArrayDesign ad;

        public RemoveArrayJob( ArrayDesign ad ) {
            super();
            this.ad = ad;
        }

        public ModelAndView call() throws Exception {

            ProgressJob job = init( "Deleting Array: " + ad.getShortName() );

            arrayDesignService.remove( ad );
            saveMessage( "Array " + ad.getShortName() + " removed from Database." );
            ad = null;

            ProgressManager.destroyProgressJob( job, true );
            return new ModelAndView( new RedirectView( "/Gemma/arrays/showAllArrayDesigns.html" ) );

        }
    }

    private static boolean AJAX = true;

    private static Log log = LogFactory.getLog( ArrayDesignController.class.getName() );
    /**
     * Instead of showing all the probes for the array, we might only fetch some of them.
     */
    private static final int NUM_PROBES_TO_SHOW = 500;

    @Autowired
    private ArrayDesignMapResultService arrayDesignMapResultService = null;

    @Autowired
    private ArrayDesignReportService arrayDesignReportService = null;

    @Autowired
    private ArrayDesignService arrayDesignService = null;

    @Autowired
    private AuditTrailService auditTrailService;

    @Autowired
    private CompositeSequenceService compositeSequenceService = null;

    private final String identifierNotFound = "Must provide a valid Array Design identifier";

    private final String messageName = "Array design with name";

    @Autowired
    private SearchService searchService;

    public String addAlternateName( Long arrayDesignId, String alternateName ) {
        ArrayDesign ad = arrayDesignService.load( arrayDesignId );
        if ( ad == null ) {
            throw new IllegalArgumentException( "No such array design with id=" + arrayDesignId );
        }

        if ( StringUtils.isBlank( alternateName ) ) {
            return formatAlternateNames( ad );
        }

        AlternateName newName = AlternateName.Factory.newInstance( alternateName );

        ad.getAlternateNames().add( newName );

        arrayDesignService.update( ad );
        return formatAlternateNames( ad );
    }

    /**
     * Delete an arrayDesign.
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/deleteArrayDesign.html")
    public ModelAndView delete( HttpServletRequest request, HttpServletResponse response ) {
        String stringId = request.getParameter( "id" );

        if ( stringId == null ) {
            // should be a validation error.
            throw new EntityNotFoundException( "Must provide an id" );
        }

        Long id = null;
        try {
            id = Long.parseLong( stringId );
        } catch ( NumberFormatException e ) {
            throw new EntityNotFoundException( "Identifier was invalid" );
        }

        ArrayDesign arrayDesign = arrayDesignService.load( id );
        if ( arrayDesign == null ) {
            throw new EntityNotFoundException( "Array design with id=" + id + " not found" );
        }

        // check that no EE depend on the arraydesign we want to delete
        // Do this by checking if there are any bioassays that depend this AD
        Collection<BioAssay> assays = arrayDesignService.getAllAssociatedBioAssays( id );
        if ( assays.size() != 0 ) {
            // String eeName = ( ( BioAssay ) assays.iterator().next() )
            // todo tell user what EE depends on this array design
            addMessage( request, "Array  " + arrayDesign.getName()
                    + " can't be deleted. Dataset has a dependency on this Array.", new Object[] { messageName,
                    arrayDesign.getName() } );
            return new ModelAndView( new RedirectView( "/Gemma/arrays/showAllArrayDesigns.html" ) );
        }

        return startJob( new RemoveArrayJob( arrayDesign ) );

    }

    /**
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/downloadAnnotationFile.html")
    public ModelAndView downloadAnnotationFile( HttpServletRequest request, HttpServletResponse response ) {

        String arrayDesignIdStr = request.getParameter( "id" );
        if ( arrayDesignIdStr == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( "Must provide an Array Design name or Id" );
        }

        String fileType = request.getParameter( "fileType" );
        if ( fileType == null )
            fileType = ArrayDesignAnnotationService.STANDARD_FILE_SUFFIX;
        else if ( fileType.equalsIgnoreCase( "noParents" ) )
            fileType = ArrayDesignAnnotationService.NO_PARENTS_FILE_SUFFIX;
        else if ( fileType.equalsIgnoreCase( "bioProcess" ) )
            fileType = ArrayDesignAnnotationService.BIO_PROCESS_FILE_SUFFIX;
        else
            fileType = ArrayDesignAnnotationService.STANDARD_FILE_SUFFIX;

        ArrayDesign arrayDesign = arrayDesignService.load( Long.parseLong( arrayDesignIdStr ) );
        String fileBaseName = ArrayDesignAnnotationService.mungeFileName( arrayDesign.getShortName() );
        String fileName = fileBaseName + fileType + ArrayDesignAnnotationService.ANNOTATION_FILE_SUFFIX;

        File f = new File( ArrayDesignAnnotationService.ANNOT_DATA_DIR + fileName );
        InputStream reader;
        try {
            reader = new BufferedInputStream( new FileInputStream( f ) );
        } catch ( FileNotFoundException fnfe ) {
            log.warn( "Annotation file " + fileName + " can't be found at " + fnfe );
            return null;
        }

        response.setHeader( "Content-disposition", "attachment; filename=" + fileName );
        response.setContentType( "application/octet-stream" );

        try {
            OutputStream outputStream = response.getOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ( ( len = reader.read( buf ) ) > 0 ) {
                outputStream.write( buf, 0, len );
            }
            reader.close();

        } catch ( IOException ioe ) {
            log.warn( "Failure during streaming of annotation file " + fileName + " Error: " + ioe );
        }

        return null;
    }

    /**
     * Show array designs that match search criteria.
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/filterArrayDesigns.html")
    public ModelAndView filter( HttpServletRequest request, HttpServletResponse response ) {

        StopWatch overallWatch = new StopWatch();
        overallWatch.start();

        String filter = request.getParameter( "filter" );

        // Validate the filtering search criteria.
        if ( StringUtils.isBlank( filter ) ) {
            this.saveMessage( request, "No search critera provided" );
            return showAllArrayDesigns( request, response );
        }

        Collection<SearchResult> searchResults = searchService.search( SearchSettings.ArrayDesignSearch( filter ) )
                .get( ArrayDesign.class );

        if ( ( searchResults == null ) || ( searchResults.size() == 0 ) ) {
            this.saveMessage( request, "Your search yielded no results" );
            Long overallElapsed = overallWatch.getTime();
            log.info( "No results found. Search took: " + overallElapsed / 1000 + "s " );
            return showAllArrayDesigns( request, response );
        }

        String list = "";

        if ( searchResults.size() == 1 ) {
            ArrayDesign arrayDesign = arrayDesignService.load( searchResults.iterator().next().getId() );
            this.saveMessage( request, "Matched one : " + arrayDesign.getName() + "(" + arrayDesign.getShortName()
                    + ")" );
            overallWatch.stop();
            Long overallElapsed = overallWatch.getTime();
            log.info( "Filter found 1 AD:  " + arrayDesign.getName() + " took: " + overallElapsed / 1000 + "s " );
            return new ModelAndView( new RedirectView( "/Gemma/arrays/showArrayDesign.html?id=" + arrayDesign.getId() ) );
        }

        for ( SearchResult ad : searchResults ) {
            list += ad.getId() + ",";
        }

        this.saveMessage( request, "Search Criteria: " + filter );
        this.saveMessage( request, searchResults.size() + " Array Designs matched your search." );

        overallWatch.stop();
        Long overallElapsed = overallWatch.getTime();
        log.info( "Generating the AD list:  (" + list + ") took: " + overallElapsed / 1000 + "s " );

        return new ModelAndView( new RedirectView( "/Gemma/arrays/showAllArrayDesigns.html?id=" + list ) );

    }

    /**
     * Build summary report for an array design
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/generateArrayDesignSummary.html")
    public ModelAndView generateSummary( HttpServletRequest request, HttpServletResponse response ) {

        String sId = request.getParameter( "id" );

        // if no IDs are specified, then load all expressionExperiments and show the summary (if available)
        if ( sId == null ) {
            return startJob( new GenerateSummary( request.getSession() ) );
        }
        Long id = Long.parseLong( sId );
        return startJob( new GenerateSummary( request.getSession(), id ) );
    }

    /**
     * AJAX
     * 
     * @param arrayDesignIds
     * @param showMergees
     * @param showOrphans
     * @return
     */
    public Collection<ArrayDesignValueObject> getArrayDesigns( Collection<Long> arrayDesignIds, boolean showMergees,
            boolean showOrphans ) {
        List<ArrayDesignValueObject> result = new ArrayList<ArrayDesignValueObject>();

        /*
         * TODO remove 'troubled' unless admin.
         */

        // If no IDs are specified, then load all expressionExperiments and show the summary (if available)
        if ( arrayDesignIds.isEmpty() ) {
            result.addAll( arrayDesignService.loadAllValueObjects() );
        } else {// if ids are specified, then display only those arrayDesigns
            result.addAll( arrayDesignService.loadValueObjects( arrayDesignIds ) );
        }

        // Filter...
        Collection<ArrayDesignValueObject> toHide = new HashSet<ArrayDesignValueObject>();
        for ( ArrayDesignValueObject a : result ) {
            if ( !showMergees && a.getIsMergee() != null && a.getIsMergee() ) {
                toHide.add( a );
            }
            if ( !showOrphans && ( a.getExpressionExperimentCount() == null || a.getExpressionExperimentCount() == 0 ) ) {
                toHide.add( a );
            }
        }
        result.removeAll( toHide );
        Collections.sort( result, new ArrayDesignValueObjectComparator() );

        return result;
    }

    /**
     * Exposed for AJAX calls.
     * 
     * @param ed
     * @return
     */
    public Collection<CompositeSequenceMapValueObject> getCsSummaries( EntityDelegator ed ) {
        ArrayDesign arrayDesign = arrayDesignService.load( ed.getId() );
        return this.getDesignSummaries( arrayDesign );
    }

    /**
     * @param arrayDesign
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<CompositeSequenceMapValueObject> getDesignSummaries( ArrayDesign arrayDesign ) {
        Collection rawSummaries = compositeSequenceService.getRawSummary( arrayDesign, NUM_PROBES_TO_SHOW );
        Collection<CompositeSequenceMapValueObject> summaries = arrayDesignMapResultService
                .getSummaryMapValueObjects( rawSummaries );
        return summaries;
    }

    /**
     * AJAX
     * 
     * @param ed
     * @return the HTML to display.
     */
    public Map<String, String> getReportHtml( EntityDelegator ed ) {
        assert ed.getId() != null;
        ArrayDesignValueObject summary = arrayDesignReportService.getSummaryObject( ed.getId() );
        Map<String, String> result = new HashMap<String, String>();

        result.put( "id", ed.getId().toString() );
        if ( summary == null )
            result.put( "html", "Not available" );
        else
            result.put( "html", ArrayDesignHtmlUtil.getSummaryHtml( summary ) );
        return result;
    }

    /**
     * @return the searchService
     */
    public SearchService getSearchService() {
        return searchService;
    }

    /**
     * AJAX
     * 
     * @return the taskid
     */
    public String remove( EntityDelegator ed ) {
        ArrayDesign arrayDesign = arrayDesignService.load( ed.getId() );
        if ( arrayDesign == null ) {
            throw new EntityNotFoundException( ed.getId() + " not found" );
        }
        Collection<BioAssay> assays = arrayDesignService.getAllAssociatedBioAssays( ed.getId() );
        if ( assays.size() != 0 ) {
            throw new IllegalArgumentException( "Cannot delete " + arrayDesign
                    + ", it is used by an expression experiment" );
        }
        return ( String ) startJob( new RemoveArrayJob( arrayDesign ) ).getModel().get( "taskId" );
    }

    /**
     * @param arrayDesignMapResultService the arrayDesignMapResultService to set
     */
    public void setArrayDesignMapResultService( ArrayDesignMapResultService arrayDesignMapResultService ) {
        this.arrayDesignMapResultService = arrayDesignMapResultService;
    }

    /**
     * @param arrayDesignReportService the arrayDesignReportService to set
     */
    public void setArrayDesignReportService( ArrayDesignReportServiceImpl arrayDesignReportService ) {
        this.arrayDesignReportService = arrayDesignReportService;
    }

    /**
     * @param arrayDesignService The arrayDesignService to set.
     */
    public void setArrayDesignService( ArrayDesignService arrayDesignService ) {
        this.arrayDesignService = arrayDesignService;
    }

    /**
     * @param ausitTrailService the auditTrailService to set
     */
    public void setAuditTrailService( AuditTrailService auditTrailService ) {
        this.auditTrailService = auditTrailService;
    }

    /**
     * @param compositeSequenceService the compositeSequenceService to set
     */
    public void setCompositeSequenceService( CompositeSequenceService compositeSequenceService ) {
        this.compositeSequenceService = compositeSequenceService;
    }

    /**
     * @param searchService the searchService to set
     */
    public void setSearchService( SearchService searchService ) {
        this.searchService = searchService;
    }

    /**
     * @param request
     * @param response
     * @param errors
     * @return
     */
    @RequestMapping("/showArrayDesign.html")
    public ModelAndView showArrayDesign( HttpServletRequest request, HttpServletResponse response ) {
        String name = request.getParameter( "name" );
        String idStr = request.getParameter( "id" );

        if ( ( name == null ) && ( idStr == null ) ) {
            // should be a validation error, on 'submit'.
            this.saveMessage( request, "Must provide an array design name or id. Displaying all Arrays" );
            return this.showAllArrayDesigns( request, response );

        }
        ArrayDesign arrayDesign = null;
        if ( idStr != null ) {
            arrayDesign = arrayDesignService.load( Long.parseLong( idStr ) );
            request.setAttribute( "id", idStr );
        } else if ( name != null ) {
            arrayDesign = arrayDesignService.findByName( name );
            request.setAttribute( "name", name );
        }

        if ( arrayDesign == null ) {
            this.saveMessage( request, "Unable to load Array Design with id: " + idStr + ". Displaying all Arrays" );
            return this.showAllArrayDesigns( request, response );

        }
        long id = arrayDesign.getId();

        Long numCompositeSequences = new Long( arrayDesignService.getCompositeSequenceCount( arrayDesign ) );
        Collection<ExpressionExperiment> ee = arrayDesignService.getExpressionExperiments( arrayDesign );
        Long numExpressionExperiments = new Long( ee.size() );

        Collection<Taxon> t = arrayDesignService.getTaxa( id );
        String taxa = formatTaxa( t );

        String colorString = formatTechnologyType( arrayDesign );

        ArrayDesignValueObject summary = arrayDesignReportService.getSummaryObject( id );

        String eeIds = formatExpressionExperimentIds( ee );

        ModelAndView mav = new ModelAndView( "arrayDesign.detail" );

        AuditEvent troubleEvent = auditTrailService.getLastTroubleEvent( arrayDesign );
        if ( troubleEvent != null ) {
            mav.addObject( "troubleEvent", troubleEvent );
            mav.addObject( "troubleEventDescription", StringEscapeUtils.escapeHtml( troubleEvent.toString() ) );
        }
        AuditEvent validatedEvent = auditTrailService.getLastValidationEvent( arrayDesign );
        if ( validatedEvent != null ) {
            mav.addObject( "validatedEvent", validatedEvent );
            mav.addObject( "validatedEventDescription", StringEscapeUtils.escapeHtml( validatedEvent.toString() ) );
        }

        Collection<ArrayDesign> subsumees = arrayDesign.getSubsumedArrayDesigns();
        ArrayDesign subsumer = arrayDesign.getSubsumingArrayDesign();

        Collection<ArrayDesign> mergees = arrayDesign.getMergees();
        ArrayDesign merger = arrayDesign.getMergedInto();

        getAnnotationFileLinks( arrayDesign, mav );

        mav.addObject( "subsumer", subsumer );
        mav.addObject( "subsumees", subsumees );
        mav.addObject( "merger", merger );
        mav.addObject( "mergees", mergees );
        mav.addObject( "taxon", taxa );
        mav.addObject( "arrayDesign", arrayDesign );
        mav.addObject( "alternateNames", this.formatAlternateNames( arrayDesign ) );
        mav.addObject( "numCompositeSequences", numCompositeSequences );
        mav.addObject( "numExpressionExperiments", numExpressionExperiments );

        mav.addObject( "expressionExperimentIds", eeIds );
        mav.addObject( "technologyType", colorString );
        mav.addObject( "summary", summary );
        return mav;
    }

    /**
     * Show all array designs, or according to a list of IDs passed in.
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/showAllArrayDesigns.html")
    public ModelAndView showAllArrayDesigns( HttpServletRequest request, HttpServletResponse response ) {

        StopWatch overallWatch = new StopWatch();
        overallWatch.start();

        String sId = request.getParameter( "id" );
        String sShowMerge = request.getParameter( "showMerg" );
        String sShowOrph = request.getParameter( "showOrph" );

        boolean showMergees = Boolean.parseBoolean( sShowMerge );
        boolean showOrphans = Boolean.parseBoolean( sShowOrph );

        ArrayDesignValueObject summary = arrayDesignReportService.getSummaryObject();

        if ( sId == null ) {
            this.saveMessage( request, "Displaying all Arrays" );
        }

        Collection<Long> ids = new ArrayList<Long>();
        if ( sId != null ) {
            String[] idList = StringUtils.split( sId, ',' );
            for ( int i = 0; i < idList.length; i++ ) {
                ids.add( new Long( idList[i] ) );
            }
        }

        Collection<ArrayDesignValueObject> valueObjects = getArrayDesigns( ids, showMergees, showOrphans );

        if ( !SecurityService.isUserAdmin() ) {
            removeTroubledArrayDesigns( valueObjects );
        }

        arrayDesignReportService.fillInValueObjects( valueObjects );
        arrayDesignReportService.fillEventInformation( valueObjects );
        arrayDesignReportService.fillInSubsumptionInfo( valueObjects );

        Long numArrayDesigns = new Long( valueObjects.size() );
        ModelAndView mav = new ModelAndView( "arrayDesigns" );
        mav.addObject( "showMergees", showMergees );
        mav.addObject( "showOrphans", showOrphans );
        mav.addObject( "arrayDesigns", valueObjects );
        mav.addObject( "numArrayDesigns", numArrayDesigns );
        mav.addObject( "summary", summary );

        log.info( "ArrayDesign.showall took: " + overallWatch.getTime() + "ms for " + numArrayDesigns );

        return mav;
    }

    /**
     * Show (some of) the probes from an array.
     * 
     * @param request
     * @return
     */
    @RequestMapping("/showCompositeSequenceSummary.html")
    public ModelAndView showCompositeSequences( HttpServletRequest request ) {

        String arrayDesignIdStr = request.getParameter( "id" );

        if ( arrayDesignIdStr == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( "Must provide an Array Design name or Id" );
        }

        ArrayDesign arrayDesign = arrayDesignService.load( Long.parseLong( arrayDesignIdStr ) );
        ModelAndView mav = new ModelAndView( "compositeSequences.geneMap" );

        if ( !AJAX ) {
            Collection<CompositeSequenceMapValueObject> compositeSequenceSummary = getDesignSummaries( arrayDesign );
            if ( compositeSequenceSummary == null || compositeSequenceSummary.size() == 0 ) {
                throw new RuntimeException( "No probes found for " + arrayDesign );
            }
            mav.addObject( "sequenceData", compositeSequenceSummary );
            mav.addObject( "numCompositeSequences", compositeSequenceSummary.size() );
        }

        mav.addObject( "arrayDesign", arrayDesign );

        return mav;
    }

    /**
     * shows a list of BioAssays for an expression experiment subset
     * 
     * @param request
     * @param errors
     * @return ModelAndView
     */
    @RequestMapping("/showExpressionExperiments.html")
    public ModelAndView showExpressionExperiments( HttpServletRequest request ) {
        Long id = Long.parseLong( request.getParameter( "id" ) );
        if ( id == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( identifierNotFound );
        }

        ArrayDesign arrayDesign = arrayDesignService.load( id );
        if ( arrayDesign == null ) {
            this.addMessage( request, "errors.objectnotfound", new Object[] { "Array Design " } );
            return new ModelAndView( new RedirectView( "/Gemma/arrays/showAllArrayDesigns.html" ) );
        }

        Collection<ExpressionExperiment> ees = arrayDesignService.getExpressionExperiments( arrayDesign );
        Collection<Long> eeIds = new ArrayList<Long>();
        for ( ExpressionExperiment object : ees ) {
            eeIds.add( object.getId() );
        }
        String ids = StringUtils.join( eeIds.toArray(), "," );
        return new ModelAndView( new RedirectView( "/Gemma/expressionExperiment/showAllExpressionExperiments.html?id="
                + ids ) );
    }

    /**
     * AJAX
     * 
     * @param ed
     * @return the taskid
     */
    public String updateReport( EntityDelegator ed ) {
        GenerateSummary runner = new GenerateSummary( ed.getId() );
        runner.setDoForward( false );
        return ( String ) super.startJob( runner ).getModel().get( "taskId" );
    }

    /**
     * @param ad
     * @return
     */
    private String formatAlternateNames( ArrayDesign ad ) {
        Collection<String> names = new HashSet<String>();
        for ( AlternateName an : ad.getAlternateNames() ) {
            names.add( an.getName() );
        }
        return StringUtils.join( names, "; " );
    }

    private String formatExpressionExperimentIds( Collection<ExpressionExperiment> ee ) {
        String[] eeIdList = new String[ee.size()];
        int i = 0;
        for ( ExpressionExperiment e : ee ) {
            eeIdList[i] = e.getId().toString();
            i++;
        }
        String eeIds = StringUtils.join( eeIdList, "," );
        return eeIds;
    }

    /**
     * Method to format taxon list for display.
     * 
     * @param taxonSet Collection of taxon used to create array/platform
     * @return Alpabetically sorted semicolon separated list of scientific names of taxa used on array/platform
     */
    private String formatTaxa( Collection<Taxon> taxonSet ) {
        String taxonListString = "";
        int i = 0;
        if ( !taxonSet.isEmpty() ) {
            String[] taxonList = new String[taxonSet.size()];
            for ( Taxon taxon : taxonSet ) {
                taxonList[i] = taxon.getScientificName();
                i++;
            }
            Arrays.sort( taxonList, String.CASE_INSENSITIVE_ORDER );
            taxonListString = StringUtils.join( taxonList, "; " );
            ;
        } else {
            taxonListString = "(Taxon not known)";
        }
        return taxonListString;
    }

    /**
     * @param arrayDesign
     * @return
     */
    private String formatTechnologyType( ArrayDesign arrayDesign ) {
        TechnologyType technologyType = arrayDesign.getTechnologyType();

        if ( technologyType == null ) {
            return "Not specified";
        }

        String techType = technologyType.getValue();
        String colorString = "";
        if ( techType.equalsIgnoreCase( "ONECOLOR" ) ) {
            colorString = "one-color";
        } else if ( techType.equalsIgnoreCase( "TWOCOLOR" ) ) {
            colorString = "two-color";
        } else if ( techType.equalsIgnoreCase( "DUALMODE" ) ) {
            colorString = "dual mode";
        } else {
            colorString = "Not specified";
        }
        return colorString;
    }

    /**
     * @param arrayDesign
     * @param mav
     */
    private void getAnnotationFileLinks( ArrayDesign arrayDesign, ModelAndView mav ) {

        ArrayDesign merger = arrayDesign.getMergedInto();
        ArrayDesign annotationFileDesign;
        if ( merger != null )
            annotationFileDesign = merger;
        else
            annotationFileDesign = arrayDesign;

        String mungedShortName = ArrayDesignAnnotationService.mungeFileName( annotationFileDesign.getShortName() );
        File fnp = new File( ArrayDesignAnnotationService.ANNOT_DATA_DIR + mungedShortName
                + ArrayDesignAnnotationService.NO_PARENTS_FILE_SUFFIX
                + ArrayDesignAnnotationService.ANNOTATION_FILE_SUFFIX );

        File fap = new File( ArrayDesignAnnotationService.ANNOT_DATA_DIR + mungedShortName
                + ArrayDesignAnnotationService.STANDARD_FILE_SUFFIX
                + ArrayDesignAnnotationService.ANNOTATION_FILE_SUFFIX );

        File fbp = new File( ArrayDesignAnnotationService.ANNOT_DATA_DIR + mungedShortName
                + ArrayDesignAnnotationService.BIO_PROCESS_FILE_SUFFIX
                + ArrayDesignAnnotationService.ANNOTATION_FILE_SUFFIX );

        // context here is Gemma/arrays
        if ( fnp.exists() ) {
            mav.addObject( "noParentsAnnotationLink", "downloadAnnotationFile.html?id=" + annotationFileDesign.getId()
                    + "&fileType=noParents" );
        }
        if ( fap.exists() ) {
            mav.addObject( "allParentsAnnotationLink", "downloadAnnotationFile.html?id=" + annotationFileDesign.getId()
                    + "&fileType=allParents" );
        }
        if ( fbp.exists() ) {
            mav.addObject( "bioProcessAnnotationLink", "downloadAnnotationFile.html?id=" + annotationFileDesign.getId()
                    + "&fileType=bioProcess" );
        }

    }

    /**
     * @param valueObjects
     */
    private void removeTroubledArrayDesigns( Collection<ArrayDesignValueObject> valueObjects ) {

        if ( valueObjects == null || valueObjects.size() == 0 ) {
            log.warn( "No ads to remove troubled from" );
            return;
        }

        Collection<Long> ids = new HashSet<Long>();
        for ( ArrayDesignValueObject advo : valueObjects ) {
            ids.add( advo.getId() );
        }

        int size = valueObjects.size();
        final Map<Long, AuditEvent> trouble = arrayDesignService.getLastTroubleEvent( ids );

        CollectionUtils.filter( valueObjects, new Predicate() {
            public boolean evaluate( Object vo ) {
                boolean hasTrouble = trouble.get( ( ( ArrayDesignValueObject ) vo ).getId() ) != null;
                return !hasTrouble;
            }
        } );
        int newSize = valueObjects.size();
        if ( newSize != size ) {
            assert newSize < size;
            log.info( "Removed " + ( size - newSize ) + " array designs with 'trouble' flags, leaving " + newSize );
        }
    }
}
