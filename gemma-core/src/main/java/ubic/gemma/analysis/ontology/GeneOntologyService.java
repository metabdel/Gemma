/*

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
package ubic.gemma.analysis.ontology;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import ubic.basecode.dataStructure.graph.DirectedGraph;
import ubic.basecode.dataStructure.graph.DirectedGraphNode;
import ubic.gemma.model.association.Gene2GOAssociationService;
import ubic.gemma.model.common.description.OntologyEntry;
import ubic.gemma.model.common.description.OntologyEntryService;
import ubic.gemma.model.genome.Gene;
import ubic.gemma.model.genome.gene.GeneService;
import ubic.gemma.util.ConfigUtils;

/**
 * Holds a complete copy of the GeneOntology. This gets loaded on startup. Where possible, use this instead of calling
 * OntologyEntryService.getChildren etc.
 * 
 * @author pavlidis
 * @version $Id$
 * @spring.bean id="geneOntologyService"
 * @spring.property name="ontologyEntryService" ref="ontologyEntryService"
 * @spring.property name="gene2GOAssociationService" ref="gene2GOAssociationService"
 * @spring.property name="geneService" ref="geneService"
 */
public class GeneOntologyService implements InitializingBean {

    private static Log log = LogFactory.getLog( GeneOntologyService.class.getName() );

    private OntologyEntryService ontologyEntryService;

    private Gene2GOAssociationService gene2GOAssociationService;

    private GeneService geneService;

    private DirectedGraph graph = null;

    private AtomicBoolean ready = new AtomicBoolean( false );

    private AtomicBoolean running = new AtomicBoolean( false );

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        // FIXME: this should look for GeneOntology specifically; this is not guaranteed by this.

        if ( running.get() ) {
            log.warn( "GO initialization is already running" );
            return;
        }

        init();

    }

    protected synchronized void init() {

        boolean loadOntology = ConfigUtils.getBoolean( "loadOntology", true );

        // if loading ontologies is disabled in the configuration, return
        if ( !loadOntology ) {
            return;
        }

        if ( running.get() ) return;
        running.set( true );
        // Need to pause while gemma's context loads

        graph = new DirectedGraph();

        Thread loadThread = new Thread( new Runnable() {
            public void run() {

                ready.set( false );
                log.info( "Loading Gene Ontology..." );
                try {

                    Collection<OntologyEntry> all = ontologyEntryService.loadAll();

                    for ( OntologyEntry oe : all ) {
                        Collection<OntologyEntry> children = ontologyEntryService.getChildren( oe ); // thaw
                        graph.addNode( new DirectedGraphNode( oe, oe, graph ) );
                        for ( OntologyEntry child : children ) {
                            graph.addChildTo( oe, child );
                        }

                        log.debug( "Adding " + oe + " to graph with: " + graph.getItems().size() );

                    }
                } catch ( Exception e ) {
                    log.error( e, e );
                    ready.set( false );
                    running.set( false );
                }
                ready.set( true );
                running.set( false );
                log.info( "Gene Ontology loaded, " + graph.getItems().size() + " items." );
            }
        } );

        loadThread.start();

    }

    /**
     * Return the immediate parent(s) of the given entry. The root node is never returned.
     * 
     * @param entry
     * @return collection, because entries can have multiple parents. (root is excluded)
     */
    @SuppressWarnings("unchecked")
    public Collection<OntologyEntry> getParents( OntologyEntry entry ) {
        if ( !ready.get() ) {
            return ontologyEntryService.getParents( entry );
        }

        DirectedGraphNode childNode = ( DirectedGraphNode ) graph.get( entry );
        if ( childNode == null ) {
            log.warn( "GO does not contain " + entry );
            return null;
        }

        Collection<DirectedGraphNode> parents = childNode.getParentNodes();

        Collection<OntologyEntry> returnVal = new HashSet<OntologyEntry>();
        for ( DirectedGraphNode node : parents ) {
            if ( node == null ) continue;
            OntologyEntry goEntry = ( OntologyEntry ) node.getItem();
            if ( isRoot( goEntry ) ) continue;
            if ( goEntry == null ) continue;
            returnVal.add( goEntry );
        }
        return returnVal;
    }

    /**
     * Return all the parents of an OntologyEntry, up to the root. NOTE: the term itself is NOT included; nor is the
     * root.
     * 
     * @param entry
     * @return parents (excluding the root)
     */
    public Collection<OntologyEntry> getAllParents( OntologyEntry entry ) {
        Collection<OntologyEntry> result = new HashSet<OntologyEntry>();
        getAllParents( entry, result );
        return result;
    }

    /**
     * @param entry
     * @param parents (excluding the root)
     */
    private void getAllParents( OntologyEntry entry, Collection<OntologyEntry> parents ) {
        if ( parents == null ) throw new IllegalArgumentException();
        Collection<OntologyEntry> immediateParents = getParents( entry );
        if ( immediateParents == null ) return;
        for ( OntologyEntry entry2 : immediateParents ) {
            if ( isRoot( entry2 ) ) continue;
            parents.add( entry2 );
            getAllParents( entry2, parents );
        }
    }

    /**
     * @param entries NOTE terms that are in this collection are NOT explicitly included; however, some of them may be
     *        included incidentally if they are parents of other terms in the collection.
     * @return
     */
    public Collection<OntologyEntry> getAllParents( Collection<OntologyEntry> entries ) {
        if ( entries == null ) return null;
        Collection<OntologyEntry> result = new HashSet<OntologyEntry>();
        for ( OntologyEntry entry : entries ) {
            getAllParents( entry, result );
        }
        return result;
    }

    /**
     * @param entry
     * @return
     */
    public Collection<OntologyEntry> getAllChildren( OntologyEntry entry ) {
        Collection<OntologyEntry> result = new HashSet<OntologyEntry>();
        getAllChildren( entry, result );
        return result;
    }

    /**
     * @param entry
     * @param children
     */
    private void getAllChildren( OntologyEntry entry, Collection<OntologyEntry> children ) {
        if ( children == null ) throw new IllegalArgumentException();
        Collection<OntologyEntry> immediateChildren = getChildren( entry );
        if ( immediateChildren == null ) return;
        for ( OntologyEntry entry2 : immediateChildren ) {
            children.add( entry2 );
            getAllChildren( entry2, children );
        }
    }

    /**
     * Determines if one ontology entry is a parent (direct or otherwise) of a given child term.
     * 
     * @param child
     * @param potentialParent
     * @return True if potentialParent is in the parent graph of the child; false otherwise.
     */
    public Boolean isAParentOf( OntologyEntry child, OntologyEntry potentialParent ) {
        if ( isRoot( potentialParent ) ) return true;
        Collection<OntologyEntry> parents = getAllParents( child );
        return parents.contains( potentialParent );
    }

    /**
     * @param entity
     * @return
     */
    protected Boolean isRoot( OntologyEntry entity ) {
        return entity.getAccession().equals( "all" ); // FIXME, use the line below instead.
        // return entity.equals( this.graph.getRoot().getItem() );
    }

    /**
     * Determins if one ontology entry is a child (direct or otherwise) of a given parent term.
     * 
     * @param parent
     * @param potentialChild
     * @return
     */
    public Boolean isAChildOf( OntologyEntry parent, OntologyEntry potentialChild ) {
        return isAParentOf( potentialChild, parent );
    }

    /**
     * Returns the immediate children of the given entry
     * 
     * @param entry
     * @return children of entry, or null if there are no children (or if entry is null)
     */
    @SuppressWarnings("unchecked")
    public Collection<OntologyEntry> getChildren( OntologyEntry entry ) {
        if ( entry == null ) return null;
        if ( !ready.get() ) {
            return ontologyEntryService.getChildren( entry );
        }
        DirectedGraphNode parentNode = ( DirectedGraphNode ) graph.get( entry );
        if ( parentNode == null ) return null;
        Collection<DirectedGraphNode> children = parentNode.getChildNodes();
        Collection<OntologyEntry> returnVal = new HashSet<OntologyEntry>();
        for ( DirectedGraphNode node : children ) {
            if ( node == null ) continue;
            OntologyEntry goEntry = ( OntologyEntry ) node.getItem();
            if ( goEntry == null ) continue;
            returnVal.add( goEntry );
        }
        return returnVal;
    }

    public Map<Long, Collection<OntologyEntry>> calculateGoTermOverlap( Gene masterGene, Collection geneIds )
            throws Exception {

        if ( masterGene == null ) return null;
        if ( geneIds.size() == 0 ) return null;

        Collection<OntologyEntry> masterOntos = getGOTerms( masterGene );

        // nothing to do.
        if ( ( masterOntos == null ) || ( masterOntos.isEmpty() ) ) return null;

        Map<Long, Collection<OntologyEntry>> overlap = new HashMap<Long, Collection<OntologyEntry>>();
        overlap.put( masterGene.getId(), masterOntos ); // include the master gene in the list. Clearly 100% overlap
        // with itself!

        if ( ( geneIds == null ) || ( geneIds.isEmpty() ) ) return overlap;

        Collection<Gene> genes = this.geneService.load( geneIds );

        for ( Object obj : genes ) {
            Gene gene = ( Gene ) obj;
            Collection<OntologyEntry> comparisonOntos = getGOTerms( gene );

            if ( ( comparisonOntos == null ) || ( comparisonOntos.isEmpty() ) ) {
                overlap.put( gene.getId(), new HashSet<OntologyEntry>() );
                continue;
            }

            overlap.put( gene.getId(), computerOverlap( masterOntos, comparisonOntos ) );
        }

        return overlap;
    }

    /**
     * @param Take a gene and return a set of all GO terms including the parents of each GO term
     * @param geneOntologyTerms
     */
    @SuppressWarnings("unchecked")
    private Collection<OntologyEntry> getGOTerms( Gene gene ) {

        Collection<OntologyEntry> ontEntry = gene2GOAssociationService.findByGene( gene );
        Collection<OntologyEntry> allGOTermSet = new HashSet<OntologyEntry>( ontEntry );

        if ( ( ontEntry == null ) || ontEntry.isEmpty() ) return null;

        allGOTermSet = getAllParents( ontEntry );

        return allGOTermSet;
    }

    private Collection<OntologyEntry> computerOverlap( Collection<OntologyEntry> masterOntos,
            Collection<OntologyEntry> comparisonOntos ) {
        Collection<OntologyEntry> overlapTerms = new HashSet<OntologyEntry>( masterOntos );
        overlapTerms.retainAll( comparisonOntos );

        return overlapTerms;
    }

    public void setOntologyEntryService( OntologyEntryService ontologyEntryService ) {
        this.ontologyEntryService = ontologyEntryService;
    }

    /**
     * @param gene2GOAssociationService the gene2GOAssociationService to set
     */
    public void setGene2GOAssociationService( Gene2GOAssociationService gene2GOAssociationService ) {
        this.gene2GOAssociationService = gene2GOAssociationService;
    }

    /**
     * @param geneService the geneService to set
     */
    public void setGeneService( GeneService geneService ) {
        this.geneService = geneService;
    }

}
