/*
 * The Gemma project
 * 
 * Copyright (c) 2008 University of British Columbia
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

package ubic.gemma.web.services;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ubic.gemma.analysis.expression.coexpression.CoexpressionMetaValueObject;
import ubic.gemma.analysis.expression.coexpression.CoexpressionValueObjectExt;
import ubic.gemma.analysis.expression.coexpression.GeneCoexpressionService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;
import ubic.gemma.model.genome.gene.GeneService;

/**
 *Allows access to the gene co-expression analysis. Given 1) a collection of gene ids, 2) a taxon id, 3) a stringency,
 * 4) an expression experiment set id, and 5) a boolean for whether to return results that are within the query set
 * only. The Expression Experiment Set ID (4) can be found by using the ExpressionExperimentSetIDEndpoint, which will
 * return all the expression experiment set ids for all taxons and their corresponding description. The stringency is
 * the miniumum number of times we found a particular relationship. Returns a list consisting of 7 columns: 1) the query
 * Gene, 2) the query gene ID, 3) the found Gene, 4) the found gene ID, 5) the support ( the number of times that
 * coexpression was found ), 6) Sign(+/-; denotes whehter the correlation between the coexpression pair is positive or
 * negative), and 7)the experiment ids that this co-expression was found in (since there should be more than 1
 * experiment this list will be returned as a space delimted string of EE Ids.)
 * 
 * @author gavin, klc
 * @version$Id$
 */

public class GeneCoexpressionEndpoint extends AbstractGemmaEndpoint {

    private static Log log = LogFactory.getLog( GeneCoexpressionEndpoint.class );

    private TaxonService taxonService;

    private GeneService geneService;

    private GeneCoexpressionService geneCoexpressionService;

    /**
     * The local name of the expected request/response.
     */
    public static final String LOCAL_NAME = "geneCoexpression";

    // The maximum number of coexpression results to return; a value of zero will return all possible results (ie. max
    // is infinity)
    public static final int MAX_RESULTS = 0;

    /**
     * Sets the "business service" to delegate to.
     */
    public void setTaxonService( TaxonService taxonService ) {
        this.taxonService = taxonService;
    }

    public void setGeneService( GeneService geneS ) {
        this.geneService = geneS;
    }

    public void setgeneCoexpressionService( GeneCoexpressionService geneCoexpressionService ) {
        this.geneCoexpressionService = geneCoexpressionService;
    }

    /**
     * Reads the given <code>requestElement</code>, and sends a the response back.
     * 
     * @param requestElement the contents of the SOAP message as DOM elements
     * @param document a DOM document to be used for constructing <code>Node</code>s
     * @return the response element
     */
    @Override
    protected Element invokeInternal( Element requestElement, Document document ) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        setLocalName( LOCAL_NAME );

        Collection<String> geneInput = getArrayValues( requestElement, "gene_ids" );
        Collection<Long> geneIDLong = new HashSet<Long>();
        for ( String id : geneInput )
            geneIDLong.add( Long.parseLong( id ) );

        Collection<String> taxonInput = getSingleNodeValue( requestElement, "taxon_id" );
        String taxonId = "";
        for ( String id : taxonInput ) {
            taxonId = id;
        }

        Collection<String> analysisInput = getSingleNodeValue( requestElement, "expression_experiment_set_id" );
        String analysisId = "";
        for ( String id : analysisInput ) {
            analysisId = id;
        }

        Collection<String> stringencyInput = getSingleNodeValue( requestElement, "stringency" );
        String string = "";
        for ( String id : stringencyInput ) {
            string = id;
        }

        Collection<String> queryGenesOnlyInput = getSingleNodeValue( requestElement, "queryGenesOnly" );
        String query = "";
        for ( String id : queryGenesOnlyInput )
            query = id;
        boolean queryGenesOnly = false;
        if ( query.endsWith( "1" ) ) queryGenesOnly = true;

        log.info( "XML input read: " + geneInput.size() + " gene ids,  & taxon id, " + taxonId + " & stringency, "
                + string + " & queryGenesOnly=" + query );

        Taxon taxon = taxonService.load( Long.parseLong( taxonId ) );
        if ( taxon == null ) {
            String msg = "No taxon with id, " + taxon + ", can be found.";
            return buildBadResponse( document, msg );
        }

        Collection<Gene> rawGeneCol = geneService.loadMultiple( geneIDLong );
        if ( rawGeneCol == null || rawGeneCol.isEmpty() ) {
            String msg = "None of the gene id's can be found.";
            return buildBadResponse( document, msg );
        }
        Collection<Gene> geneCol = retainGenesInCorrectTaxon( rawGeneCol, taxon );
        if ( geneCol == null || geneCol.isEmpty() ) {
            String msg = "Input genes do not match input taxon.";
            return buildBadResponse( document, msg );
        }
        geneService.thawLite( geneCol );

        int stringency = Integer.parseInt( string );

        // Collection<GeneCoexpressionAnalysis> analysisCol = geneCoexpressionAnalysisService.findByTaxon( taxon );
        // GeneCoexpressionAnalysis analysis2Use = null;
        //
        // // use the 1st canned analysis that isn't virtual for the given taxon (should be the all"Taxon" analysis)
        // for ( GeneCoexpressionAnalysis analysis : analysisCol ) {
        // if (analysis instanceof GeneCoexpressionVirtualAnalysis)
        // continue;
        // else{
        // analysis2Use = analysis;
        // break;
        // }
        // }

        // get Gene2GeneCoexpressio objects canned analysis
        CoexpressionMetaValueObject coexpressedGenes = geneCoexpressionService.coexpressionSearch( Long
                .parseLong( analysisId ), geneCol, stringency, MAX_RESULTS, queryGenesOnly );

        if ( coexpressedGenes == null || coexpressedGenes.getKnownGeneResults().isEmpty() ) {
            String msg = "No coexpressed genes can be found.";
            return buildBadResponse( document, msg );
        }

        final String QUERY_GENE_NAME = "query_gene";
        final String QUERY_GENE_ID = "query_id";
        final String FOUND_GENE_NAME = "found_gene";
        final String FOUND_GENE_ID = "found_id";
        final String SUPPORT_NAME = "support";
        final String SIGN_NAME = "sign";
        final String EEID_NAME = "eeIdList";

        Element responseWrapper = document.createElementNS( NAMESPACE_URI, LOCAL_NAME );
        Element responseElement = document.createElementNS( NAMESPACE_URI, LOCAL_NAME + RESPONSE );
        responseWrapper.appendChild( responseElement );

        for ( CoexpressionValueObjectExt cvo : coexpressedGenes.getKnownGeneResults() ) {

            Element e1 = document.createElement( QUERY_GENE_NAME );
            e1.appendChild( document.createTextNode( cvo.getQueryGene().getOfficialSymbol() ) );
            responseElement.appendChild( e1 );

            Element e2 = document.createElement( QUERY_GENE_ID );
            e2.appendChild( document.createTextNode( cvo.getQueryGene().getId().toString() ) );
            responseElement.appendChild( e2 );

            Element e3 = document.createElement( FOUND_GENE_NAME );
            e3.appendChild( document.createTextNode( cvo.getFoundGene().getOfficialSymbol() ) );
            responseElement.appendChild( e3 );

            Element e4 = document.createElement( FOUND_GENE_ID );
            e4.appendChild( document.createTextNode( cvo.getFoundGene().getId().toString() ) );
            responseElement.appendChild( e4 );

            Integer support = 0;
            String sign = "";

            if ( cvo.getPosSupp() > 0 ) {
                support = cvo.getPosSupp();
                sign = "+";
            } else if ( cvo.getNegSupp() > 0 ) {
                support = cvo.getNegSupp();
                sign = "-";
            }

            // If it happens that a result has both neg and pos links, then the pos link and sign will be used
            // TODO: Handle cases where a result can have both neg and pos links
            Element e5 = document.createElement( SUPPORT_NAME );
            e5.appendChild( document.createTextNode( support.toString() ) );
            responseElement.appendChild( e5 );

            Element e6 = document.createElement( SIGN_NAME );
            e6.appendChild( document.createTextNode( sign ) );
            responseElement.appendChild( e6 );

            Element e7 = document.createElement( EEID_NAME );
            e7.appendChild( document.createTextNode( encode( cvo.getSupportingExperiments().toArray() ) ) );
            responseElement.appendChild( e7 );

        }
        watch.stop();
        Long time = watch.getTime();

        log.info( "XML response for coexpression canned result built in " + time + "ms." );
        return responseWrapper;

    }

    private Collection<Gene> retainGenesInCorrectTaxon( Collection<Gene> rawGeneCol, Taxon taxon ) {
        Collection<Gene> genesToUse = new HashSet<Gene>();
        for ( Gene gene : rawGeneCol ) {
            if ( gene.getTaxon().getId().equals( taxon.getId() ) ) genesToUse.add( gene );
        }
        return genesToUse;
    }

}
