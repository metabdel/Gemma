/*
 * The Gemma project Copyright (c) 2010 University of British Columbia Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package ubic.gemma.web.services;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ubic.gemma.model.genome.Chromosome;
import ubic.gemma.model.genome.ChromosomeService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.PhysicalLocation;
import ubic.gemma.model.genome.RelativeLocationData;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;
import ubic.gemma.model.genome.gene.GeneService;

public class GenesAtPhysicalLocationEndpoint extends AbstractGemmaEndpoint {

    private static Log log = LogFactory.getLog( GenesAtPhysicalLocationEndpoint.class );

    private GeneService geneService;
    private ChromosomeService chromosomeService;
    private TaxonService taxonService;

    /**
     * The local name of the expected Request/Response.
     */
    public static final String GENES_PLOC_LOCAL_NAME = "genesAtPhysicalLocation";

    /**
     * Sets the "business service" to delegate to.
     */
    public void setGeneService( GeneService geneS ) {
        this.geneService = geneS;
    }
    
    /**
     * @param chromosomeS
     */
    public void setChromosomeService( ChromosomeService chromosomeS){
        this.chromosomeService = chromosomeS;
    }
    
    /**
     * @param taxonS
     */
    public void setTaxonService(TaxonService taxonS){
        this.taxonService = taxonS;
    }
    /**
     * Reads the given <code>requestElement</code>, and sends a the response back.
     * 
     * @param requestElement the contents of the SOAP message as DOM elements
     * @param document a DOM document to be used for constructing <code>Node</code>s
     * @return the response element
     */
    @Override
    protected Element invokeInternal( Element requestElement, Document document ) {
        StopWatch watch = new StopWatch();
        watch.start();

        setLocalName( GENES_PLOC_LOCAL_NAME );
        String startNucleotide = getLastSingleNodeValue( requestElement, "startNucleotide" );
        String endNucleotide = getLastSingleNodeValue( requestElement, "endNucleotide");
        String taxId = getLastSingleNodeValue( requestElement, "taxon_id" );
        String chromosomeName= getLastSingleNodeValue( requestElement, "chromosome" );
        
        Long taxonId = Long.parseLong( taxId );
        Taxon taxon = taxonService.load( taxonId );
        
        Long startN = Long.parseLong( startNucleotide );
        Long endN = Long.parseLong( endNucleotide );
        Long length = endN-startN;
        
        
        
        log.debug( "XML input read: startNucleotide,endNucleotide,taxon,chromosome " + startN + " ," + endN + ", " +taxonId + " ," + chromosomeName  );

        Chromosome chrom = this.chromosomeService.find( chromosomeName, taxon );
        
        PhysicalLocation physicalLocation  = PhysicalLocation.Factory.newInstance();
        physicalLocation.setNucleotide( startN );
        physicalLocation.setNucleotideLength(length.intValue());
        physicalLocation.setStrand( null );
        physicalLocation.setChromosome( chrom );
        
        RelativeLocationData rld  = geneService.findNearest( physicalLocation, false );

        Gene nearestGene = rld.getNearestGene();
        buildNearestGeneWrapper(document, nearestGene);
        watch.stop();
        Long time = watch.getTime();
        log.debug( "XML response for physical location result built in " + time + "ms." );

        return null;
    }


    private Element buildNearestGeneWrapper( Document document, Gene nearestGene ) {

        Element responseWrapper = document.createElementNS( NAMESPACE_URI, GENES_PLOC_LOCAL_NAME );
        Element responseElement = document.createElementNS( NAMESPACE_URI, GENES_PLOC_LOCAL_NAME + RESPONSE );
        responseWrapper.appendChild( responseElement );

        Element e1 = document.createElement( "genesAtPhysical_location" );
        e1.appendChild( document.createTextNode( nearestGene.getId().toString() ) );
        responseElement.appendChild( e1 );

       
        return responseWrapper;
    }
    

}
