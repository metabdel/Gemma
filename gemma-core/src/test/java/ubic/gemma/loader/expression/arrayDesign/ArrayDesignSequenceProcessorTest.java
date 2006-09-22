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
package ubic.gemma.loader.expression.arrayDesign;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;

import ubic.gemma.model.expression.arrayDesign.ArrayDesign;
import ubic.gemma.model.expression.designElement.CompositeSequence;
import ubic.gemma.model.expression.designElement.DesignElement;
import ubic.gemma.model.genome.Taxon;
import ubic.gemma.model.genome.TaxonService;
import ubic.gemma.model.genome.biosequence.SequenceType;
import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author pavlidis
 * @version $Id$
 */
public class ArrayDesignSequenceProcessorTest extends BaseSpringContextTest {

    Collection<CompositeSequence> designElements = new HashSet<CompositeSequence>();
    InputStream seqFile;
    InputStream probeFile;
    InputStream designElementStream;
    Taxon taxon;

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();

        designElementStream = this.getClass().getResourceAsStream( "/data/loader/expression/arrayDesign/MG-U74A.txt" );

        seqFile = this.getClass().getResourceAsStream( "/data/loader/expression/arrayDesign/MG-U74A_target" );

        probeFile = this.getClass().getResourceAsStream( "/data/loader/expression/arrayDesign/MG-U74A_probe" );

        taxon = ( ( TaxonService ) getBean( "taxonService" ) ).findByScientificName( "Mus musculus" );
        assert taxon != null;
    }

    public void testAssignSequencesToDesignElements() throws Exception {
        ArrayDesignSequenceProcessingService app = ( ArrayDesignSequenceProcessingService ) getBean( "arrayDesignSequenceProcessingService" );
        app.assignSequencesToDesignElements( designElements, seqFile );
        CompositeSequenceParser parser = new CompositeSequenceParser();
        parser.parse( designElementStream );
        designElements = parser.getResults();
        for ( DesignElement de : designElements ) {
            assertTrue( ( ( CompositeSequence ) de ).getBiologicalCharacteristic() != null );
        }

    }

    public void testAssignSequencesToDesignElementsMissingSequence() throws Exception {
        ArrayDesignSequenceProcessingService app = ( ArrayDesignSequenceProcessingService ) getBean( "arrayDesignSequenceProcessingService" );

        CompositeSequenceParser parser = new CompositeSequenceParser();
        parser.parse( designElementStream );
        designElements = parser.getResults();

        CompositeSequence doesntExist = CompositeSequence.Factory.newInstance();
        String fakeName = "I'm not real";
        doesntExist.setName( fakeName );
        designElements.add( doesntExist );

        app.assignSequencesToDesignElements( designElements, seqFile );

        boolean found = false;
        assertEquals( 34, designElements.size() ); // 33 from file plus one fake.

        for ( DesignElement de : designElements ) {

            if ( de.getName().equals( fakeName ) ) {
                found = true;
                if ( ( ( CompositeSequence ) de ).getBiologicalCharacteristic() != null ) {
                    fail( "Shouldn't have found a biological characteristic for this sequence" );

                    continue;
                }
            } else {
                assertTrue( de.getName() + " biological sequence not found", ( ( CompositeSequence ) de )
                        .getBiologicalCharacteristic() != null );
            }

        }

        assertTrue( found ); // sanity check.

    }

    public void testProcessAffymetrixDesign() throws Exception {
        ArrayDesignSequenceProcessingService app = ( ArrayDesignSequenceProcessingService ) getBean( "arrayDesignSequenceProcessingService" );
        ArrayDesign result = app.processAffymetrixDesign( "MG-U74A", designElementStream, probeFile, taxon );

        assertEquals( "composite sequence count", 33, result.getCompositeSequences().size() );
        // assertEquals( "reporter count", 528, result.getReporters().size() );
        assertEquals( "reporter per composite sequence", 16, result.getCompositeSequences().iterator().next()
                .getComponentReporters().size() );
        assertTrue( result.getCompositeSequences().iterator().next().getArrayDesign() == result );

    }

    public void testProcessNonAffyDesign() throws Exception {

        ArrayDesignSequenceProcessingService app = ( ArrayDesignSequenceProcessingService ) getBean( "arrayDesignSequenceProcessingService" );
        ArrayDesign arrayDesign = app.processAffymetrixDesign( "MG-U74A", designElementStream, probeFile, taxon );

        assertNotNull( arrayDesign.getId() );

        app.processArrayDesign( arrayDesign, seqFile, SequenceType.EST, taxon );

        assertEquals( "composite sequence count", 33, arrayDesign.getCompositeSequences().size() );
       
        assertEquals( "reporter per composite sequence", 17, arrayDesign.getCompositeSequences().iterator().next()
                .getComponentReporters().size() );
        assertTrue( arrayDesign.getCompositeSequences().iterator().next().getArrayDesign() == arrayDesign );
    }

}
