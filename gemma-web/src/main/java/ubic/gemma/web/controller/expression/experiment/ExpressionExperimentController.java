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
package ubic.gemma.web.controller.expression.experiment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ubic.gemma.analysis.report.ExpressionExperimentReportService;
import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.bioAssay.BioAssay;
import ubic.gemma.model.expression.biomaterial.BioMaterial;
import ubic.gemma.model.expression.experiment.ExpressionExperiment;
import ubic.gemma.model.expression.experiment.ExpressionExperimentService;
import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSet;
import ubic.gemma.model.expression.experiment.ExpressionExperimentSubSetService;
import ubic.gemma.model.expression.experiment.ExpressionExperimentValueObject;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.search.SearchService;
import ubic.gemma.security.SecurityService;
import ubic.gemma.util.progress.ProgressJob;
import ubic.gemma.util.progress.ProgressManager;
import ubic.gemma.web.controller.BackgroundControllerJob;
import ubic.gemma.web.controller.BackgroundProcessingMultiActionController;
import ubic.gemma.web.util.EntityNotFoundException;

/**
 * @author keshav
 * @version $Id$
 * @spring.bean id="expressionExperimentController"
 * @spring.property name = "expressionExperimentService" ref="expressionExperimentService"
 * @spring.property name = "expressionExperimentSubSetService" ref="expressionExperimentSubSetService"
 * @spring.property name = "expressionExperimentReportService" ref="expressionExperimentReportService"
 * @spring.property name="methodNameResolver" ref="expressionExperimentActions"
 * @spring.property name="searchService" ref="searchService"
 */
public class ExpressionExperimentController extends BackgroundProcessingMultiActionController {

    private static final Boolean AJAX = true;

    private ExpressionExperimentService expressionExperimentService = null;

    private ExpressionExperimentSubSetService expressionExperimentSubSetService = null;
    private ExpressionExperimentReportService expressionExperimentReportService = null;

    private SearchService searchService;

    private final String identifierNotFound = "Must provide a valid ExpressionExperiment identifier";

    /**
     * @param request
     * @param response
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView delete( HttpServletRequest request, HttpServletResponse response ) {

        Long id = null;
        try {
            id = Long.parseLong( request.getParameter( "id" ) );
        } catch ( NumberFormatException e ) {
            throw new EntityNotFoundException( "There was no valid identifier." );
        }

        if ( id == null ) {
            // should be a validation error.
            throw new EntityNotFoundException( identifierNotFound );
        }

        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        if ( expressionExperiment == null ) {
            throw new EntityNotFoundException( expressionExperiment + " not found" );
        }

        RemoveExpressionExperimentJob removeExpressionExperimentJob = new RemoveExpressionExperimentJob(
                expressionExperiment, expressionExperimentService );

        return startJob( request, removeExpressionExperimentJob );

    }

    /**
     * Exposed for AJAX calls.
     * 
     * @param id
     * @return taskId
     */
    public String deleteById( Long id ) {
        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        RemoveExpressionExperimentJob removeExpressionExperimentJob = new RemoveExpressionExperimentJob(
                expressionExperiment, expressionExperimentService );
        return run( removeExpressionExperimentJob );
    }

    /**
     * @param request
     * @param response
     * @return
     */
    public ModelAndView filter( HttpServletRequest request, HttpServletResponse response ) {
        String searchString = request.getParameter( "filter" );

        // Validate the filtering search criteria.
        if ( StringUtils.isBlank( searchString ) ) {
            this.saveMessage( request, "No search criteria provided" );
            return showAll( request, response );
        }

        Collection<ExpressionExperiment> searchResults = searchService.expressionExperimentSearch( searchString );

        if ( ( searchResults == null ) || ( searchResults.size() == 0 ) ) {
            this.saveMessage( request, "Your search yielded no results." );
            return showAll( request, response );
        }

        if ( searchResults.size() == 1 ) {
            this.saveMessage( request, "Search Criteria: " + searchString + "; " + searchResults.size()
                    + " Datasets matched." );
            return new ModelAndView( new RedirectView( "/Gemma/expressionExperiment/showExpressionExperiment.html?id="
                    + searchResults.iterator().next().getId() ) );
        }

        String list = "";
        for ( ExpressionExperiment ee : searchResults )
            list += ee.getId() + ",";

        this.saveMessage( request, "Search Criteria: " + searchString + "; " + searchResults.size()
                + " Datasets matched." );
        return new ModelAndView( new RedirectView( "/Gemma/expressionExperiment/showAllExpressionExperiments.html?id="
                + list ) );
    }

    /**
     * @param request
     * @param response
     * @return
     */
    @SuppressWarnings( { "unused", "unchecked" })
    public ModelAndView generateSummary( HttpServletRequest request, HttpServletResponse response ) {

        String sId = request.getParameter( "id" );

        // if no IDs are specified, then load all expressionExperiments and show the summary (if available)
        if ( sId == null ) {
            return startJob( request, new GenerateSummary( expressionExperimentReportService ) );
        } else {
            Collection ids = new ArrayList<Long>();

            String[] idList = StringUtils.split( sId, ',' );
            for ( int i = 0; i < idList.length; i++ ) {
                if ( StringUtils.isNotBlank( idList[i] ) ) {
                    ids.add( new Long( idList[i] ) );
                }
            }
            expressionExperimentReportService.generateSummaryObjects( ids );
            String idStr = StringUtils.join( ids.toArray(), "," );
            return new ModelAndView( new RedirectView(
                    "/Gemma/expressionExperiment/showAllExpressionExperimentLinkSummaries.html" ) );
        }

    }

    /**
     * @return the searchService
     */
    public SearchService getSearchService() {
        return searchService;
    }

    /**
     * @param expressionExperimentReportService the expressionExperimentReportService to set
     */
    public void setExpressionExperimentReportService(
            ExpressionExperimentReportService expressionExperimentReportService ) {
        this.expressionExperimentReportService = expressionExperimentReportService;
    }

    /**
     * @param expressionExperimentService
     */
    public void setExpressionExperimentService( ExpressionExperimentService expressionExperimentService ) {
        this.expressionExperimentService = expressionExperimentService;
    }

    /**
     * @param expressionExperimentSubSetService
     */
    public void setExpressionExperimentSubSetService(
            ExpressionExperimentSubSetService expressionExperimentSubSetService ) {
        this.expressionExperimentSubSetService = expressionExperimentSubSetService;
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
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView show( HttpServletRequest request, HttpServletResponse response ) {

        if ( request.getParameter( "id" ) == null ) {
            // should be a validator error on submit
            return redirectToList( request );
        }

        Long id = Long.parseLong( request.getParameter( "id" ) );

        if ( id == null ) {
            // should be a validator error on submit
            return redirectToList( request );
        }

        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        if ( expressionExperiment == null ) {
            return redirectToList( request );
        }
        request.setAttribute( "id", id );

        ModelAndView mav = new ModelAndView( "expressionExperiment.detail" ).addObject( "expressionExperiment",
                expressionExperiment );

        // Set s = expressionExperimentService.getQuantitationTypeCountById( id ).entrySet();
        // mav.addObject( "qtCountSet", s );
        Collection quantitationTypes = expressionExperimentService.getQuantitationTypes( expressionExperiment );
        mav.addObject( "quantitationTypes", quantitationTypes );

        // add arrayDesigns used, by name
        Collection<ArrayDesign> arrayDesigns = new ArrayList<ArrayDesign>();
        Collection<BioAssay> bioAssays = expressionExperiment.getBioAssays();
        for ( BioAssay assay : bioAssays ) {
            ArrayDesign design = assay.getArrayDesignUsed();
            if ( !arrayDesigns.contains( design ) ) {
                arrayDesigns.add( design );
            }
        }

        mav.addObject( "arrayDesigns", arrayDesigns );
        // add count of designElementDataVectors
        Long designElementDataVectorCount = new Long( expressionExperimentService
                .getDesignElementDataVectorCountById( id ) );
        mav.addObject( "designElementDataVectorCount", designElementDataVectorCount );

        // load coexpression link count from cache
        Collection<Long> eeId = new ArrayList<Long>();
        eeId.add( id );
        Collection eeVo = expressionExperimentReportService.retrieveSummaryObjects( eeId );
        if ( eeVo != null && eeVo.size() > 0 ) {
            ExpressionExperimentValueObject vo = ( ExpressionExperimentValueObject ) eeVo.iterator().next();
            String eeLinks = vo.getCoexpressionLinkCount().toString() + " (as of " + vo.getDateCached() + ")";
            mav.addObject( "eeCoexpressionLinks", eeLinks );
        }

        return mav;
    }

    /**
     * @param request
     * @param response
     * @return ModelAndView
     */
    @SuppressWarnings( { "unused", "unchecked" })
    public ModelAndView showAll( HttpServletRequest request, HttpServletResponse response ) {

        String sId = request.getParameter( "id" );
        String taxonId = request.getParameter( "taxonId" );
        Collection<ExpressionExperimentValueObject> expressionExperiments = new ArrayList<ExpressionExperimentValueObject>();

        // if a taxon ID is specified, load all expression experiments for this taxon
        if ( taxonId != null ) {
            Taxon taxon = Taxon.Factory.newInstance();
            Long tId = Long.parseLong( taxonId );
            taxon.setId( tId );
            // taxon = taxonService.find( taxon );
            Collection<ExpressionExperimentValueObject> eeValObjectCol = this
                    .getExpressionExperimentValueObjects( expressionExperimentService.findByTaxon( taxon ) );

            expressionExperiments.addAll( eeValObjectCol );
        }
        // if no IDs are specified, then load all expressionExperiments
        else if ( sId == null ) {
            this.saveMessage( request, "Displaying all Datasets" );
            // TODO refactor this and make more generic (that is, turning securable objects into value objects).
            // I did this because I need to go through security.
            Collection<ExpressionExperimentValueObject> eeValObjectCol = this
                    .getFilteredExpressionExperimentValueObjects( null );
            expressionExperiments.addAll( eeValObjectCol );
        }
        // if ids are specified, then display only those expressionExperiments
        else {
            Collection eeList = new ArrayList<ExpressionExperiment>();

            String[] idList = StringUtils.split( sId, ',' );
            for ( int i = 0; i < idList.length; i++ ) {
                if ( StringUtils.isNotBlank( idList[i] ) ) {
                    // ids.add( new Long( idList[i] ) );
                    ExpressionExperiment ee = ExpressionExperiment.Factory.newInstance();
                    ee.setId( new Long( idList[i] ) );
                    eeList.add( ee );
                }
            }
            Collection<ExpressionExperimentValueObject> eeValObjectCol = this
                    .getFilteredExpressionExperimentValueObjects( eeList );
            expressionExperiments.addAll( eeValObjectCol );
        }
        // sort expression experiments by name first
        Collections.sort( ( List<ExpressionExperimentValueObject> ) expressionExperiments, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                String s1 = ( ( ExpressionExperimentValueObject ) o1 ).getName();
                String s2 = ( ( ExpressionExperimentValueObject ) o2 ).getName();
                int comparison = s1.compareToIgnoreCase( s2 );
                return comparison;
            }
        } );
        Long numExpressionExperiments = new Long( expressionExperiments.size() );
        ModelAndView mav = new ModelAndView( "expressionExperiments" );
        mav.addObject( "expressionExperiments", expressionExperiments );
        mav.addObject( "numExpressionExperiments", numExpressionExperiments );
        return mav;

    }

    /**
     * @param request
     * @param response
     * @return ModelAndView
     */
    @SuppressWarnings( { "unused", "unchecked" })
    public ModelAndView showAllLinkSummaries( HttpServletRequest request, HttpServletResponse response ) {

        String sId = request.getParameter( "id" );
        Collection<ExpressionExperimentValueObject> expressionExperiments = new ArrayList<ExpressionExperimentValueObject>();

        // if no IDs are specified, then load all expressionExperiments
        if ( sId == null ) {
            this.saveMessage( request, "Displaying all Datasets" );
            Collection<ExpressionExperimentValueObject> eeValObjectCol = this
                    .getFilteredExpressionExperimentValueObjects( null );
            expressionExperiments.addAll( eeValObjectCol );
            // expressionExperiments.addAll( expressionExperimentService.loadAllValueObjects() );
        }

        // if ids are specified, then display only those expressionExperiments
        else {
            Collection ids = new ArrayList<Long>();

            String[] idList = StringUtils.split( sId, ',' );
            for ( int i = 0; i < idList.length; i++ ) {
                if ( StringUtils.isNotBlank( idList[i] ) ) {
                    ids.add( new Long( idList[i] ) );
                }
            }
            Collection<ExpressionExperimentValueObject> eeValObjectCol = this
                    .getFilteredExpressionExperimentValueObjects( null );
            expressionExperiments.addAll( eeValObjectCol );
            // expressionExperiments.addAll( expressionExperimentService.loadValueObjects( ids ) );
        }

        // load cached data
        expressionExperimentReportService.fillLinkStatsFromCache( expressionExperiments );
        expressionExperimentReportService.fillEventInformation( expressionExperiments );
        // sort expression experiments by name first
        Collections.sort( ( List<ExpressionExperimentValueObject> ) expressionExperiments, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                String s1 = ( ( ExpressionExperimentValueObject ) o1 ).getName();
                String s2 = ( ( ExpressionExperimentValueObject ) o2 ).getName();
                int comparison = s1.compareToIgnoreCase( s2 );
                return comparison;
            }
        } );
        Long numExpressionExperiments = new Long( expressionExperiments.size() );
        ModelAndView mav = new ModelAndView( "expressionExperimentLinkSummary" );
        mav.addObject( "expressionExperiments", expressionExperiments );
        mav.addObject( "numExpressionExperiments", numExpressionExperiments );
        return mav;

    }

    /**
     * @param request
     * @param response
     * @param errors
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView showBioAssays( HttpServletRequest request, HttpServletResponse response ) {
        String idStr = request.getParameter( "id" );

        if ( idStr == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( identifierNotFound );
        }
        Long id = Long.parseLong( idStr );

        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        Map m = expressionExperimentService.getQuantitationTypeCountById( id );
        if ( expressionExperiment == null ) {
            throw new EntityNotFoundException( id + " not found" );
        }

        request.setAttribute( "id", id );
        return new ModelAndView( "bioAssays" ).addObject( "bioAssays", expressionExperiment.getBioAssays() );
    }

    /**
     * @param request
     * @param response
     * @param errors
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView showBioMaterials( HttpServletRequest request, HttpServletResponse response ) {
        String idStr = request.getParameter( "id" );

        if ( idStr == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( identifierNotFound );
        }
        Long id = Long.parseLong( idStr );

        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        if ( expressionExperiment == null ) {
            throw new EntityNotFoundException( id + " not found" );
        }

        Collection<BioAssay> bioAssays = expressionExperiment.getBioAssays();
        Collection<BioMaterial> bioMaterials = new ArrayList<BioMaterial>();
        for ( BioAssay assay : bioAssays ) {
            Collection<BioMaterial> materials = assay.getSamplesUsed();
            if ( materials != null ) {
                bioMaterials.addAll( materials );
            }
        }

        ModelAndView mav = new ModelAndView( "bioMaterials" );
        if ( AJAX ) {
            StringBuilder buf = new StringBuilder();
            for ( BioMaterial bm : bioMaterials ) {
                buf.append( bm.getId() );
                buf.append( "," );
            }
            mav.addObject( "bioMaterialIdList", buf.toString().replaceAll( ",$", "" ) );
        }

        Long numBioMaterials = new Long( bioMaterials.size() );
        mav.addObject( "numBioMaterials", numBioMaterials );
        mav.addObject( "bioMaterials", bioMaterials );

        return mav;
    }

    /**
     * Shows a bioassay view of a single expression experiment subset.
     * 
     * @param request
     * @param response
     * @param errors
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView showExpressionExperimentSubSet( HttpServletRequest request, HttpServletResponse response ) {
        Long id = Long.parseLong( request.getParameter( "id" ) );

        if ( id == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( identifierNotFound );
        }

        ExpressionExperiment expressionExperiment = expressionExperimentService.load( id );
        if ( expressionExperiment == null ) {
            throw new EntityNotFoundException( id + " not found" );
        }

        request.setAttribute( "id", id );
        return new ModelAndView( "bioAssays" ).addObject( "bioAssays", expressionExperiment.getBioAssays() );
    }

    /**
     * shows a list of BioAssays for an expression experiment subset
     * 
     * @param request
     * @param response
     * @param errors
     * @return ModelAndView
     */
    @SuppressWarnings("unused")
    public ModelAndView showSubSet( HttpServletRequest request, HttpServletResponse response ) {
        Long id = Long.parseLong( request.getParameter( "id" ) );
        if ( id == null ) {
            // should be a validation error, on 'submit'.
            throw new EntityNotFoundException( identifierNotFound );
        }

        ExpressionExperimentSubSet subset = expressionExperimentSubSetService.load( id );
        if ( subset == null ) {
            throw new EntityNotFoundException( id + " not found" );
        }

        // request.setAttribute( "id", id );
        return new ModelAndView( "bioAssays" ).addObject( "bioAssays", subset.getBioAssays() );
    }

    /**
     * @param securedEEs
     * @return
     */
    @SuppressWarnings("unchecked")
    private Collection<ExpressionExperimentValueObject> getExpressionExperimentValueObjects(
            Collection<ExpressionExperiment> securedEEs ) {
        // FIXME use the ee, not the id
        Collection ids = new LinkedHashSet();
        for ( ExpressionExperiment ee : securedEEs ) {
            ids.add( ee.getId() );
        }
        return expressionExperimentService.loadValueObjects( ids );
    }

    /**
     * Get the expression experiment value objects for the expression experiments.
     * 
     * @param eeCol
     * @return Collection<ExpressionExperimentValueObject>
     */
    @SuppressWarnings("unchecked")
    private Collection<ExpressionExperimentValueObject> getFilteredExpressionExperimentValueObjects(
            Collection<ExpressionExperiment> eeCol ) {

        log.debug( SecurityService.getPrincipal() );

        /* Filtering happens here. */
        Collection<ExpressionExperiment> allEEs = expressionExperimentService.loadAll();
        Collection<ExpressionExperiment> securedEEs = new ArrayList<ExpressionExperiment>();

        if ( eeCol == null ) {
            securedEEs = allEEs;
        } else {
            Collection ids = new LinkedHashSet();
            for ( ExpressionExperiment ee : eeCol ) {
                ids.add( ee.getId() );
            }
            securedEEs = expressionExperimentService.load( ids );
        }
        return getExpressionExperimentValueObjects( securedEEs );
    }

    private ModelAndView redirectToList( HttpServletRequest request ) {
        this.addMessage( request, "errors.objectnotfound", new Object[] { "Expression Experiment" } );
        return new ModelAndView( new RedirectView( "/Gemma/expressionExperiment/showAllExpressionExperiments.html" ) );
    }

    /**
     * Generates summary reports of expression experiments
     * 
     * @author pavlidis
     * @version $Id$
     */
    private class GenerateSummary extends BackgroundControllerJob<ModelAndView> {

        private ExpressionExperimentReportService expressionExperimentReportService;
        private Collection ids;

        public GenerateSummary( ExpressionExperimentReportService expressionExperimentReportService ) {
            super( getMessageUtil() );
            this.expressionExperimentReportService = expressionExperimentReportService;
            ids = null;
        }

        public GenerateSummary( ExpressionExperimentReportService expressionExperimentReportService, Collection id ) {
            super( getMessageUtil() );
            this.expressionExperimentReportService = expressionExperimentReportService;
            this.ids = id;
        }

        @SuppressWarnings("unchecked")
        public ModelAndView call() throws Exception {

            init();

            ProgressJob job = ProgressManager.createProgressJob( this.getTaskId(), securityContext.getAuthentication()
                    .getName(), "Expression experiment report  generating" );

            if ( ids == null ) {
                saveMessage( "Generating report for all experiments" );
                job.updateProgress( "Generating report for all experiments" );
                expressionExperimentReportService.generateSummaryObjects();
            } else {
                saveMessage( "Generating report for experiment" );
                job.updateProgress( "Generating report for specified experiment" );
                expressionExperimentReportService.generateSummaryObjects( ids );
            }
            ProgressManager.destroyProgressJob( job );
            if ( ids != null ) {
                String idStr = StringUtils.join( ids.toArray(), "," );
                return new ModelAndView( new RedirectView(
                        "/Gemma/expressionExperiment/showAllExpressionExperimentLinkSummaries.html?id=" + idStr ) );
            } else {
                return new ModelAndView( new RedirectView(
                        "/Gemma/expressionExperiment/showAllExpressionExperimentLinkSummaries.html" ) );
            }

        }
    }

    /**
     * Delete expression experiments.
     * 
     * @author pavlidis
     * @version $Id$
     */
    private class RemoveExpressionExperimentJob extends BackgroundControllerJob<ModelAndView> {

        ExpressionExperimentService expressionExperimentService;
        ExpressionExperiment ee;

        public RemoveExpressionExperimentJob( ExpressionExperiment ee,
                ExpressionExperimentService expressionExperimentService ) {
            super( getMessageUtil() );
            this.expressionExperimentService = expressionExperimentService;
            this.ee = ee;
        }

        @SuppressWarnings("unchecked")
        public ModelAndView call() throws Exception {

            init();

            expressionExperimentService.thawLite( ee );
            ProgressJob job = ProgressManager.createProgressJob( this.getTaskId(), securityContext.getAuthentication()
                    .getName(), "Deleting dataset: " + ee.getId() );

            expressionExperimentService.delete( ee );
            saveMessage( "Dataset " + ee.getShortName() + " removed from Database" );
            ee = null;

            ProgressManager.destroyProgressJob( job );
            return new ModelAndView( new RedirectView( "/Gemma/expressionExperiment/showAllExpressionExperiments.html" ) );

        }
    }

}