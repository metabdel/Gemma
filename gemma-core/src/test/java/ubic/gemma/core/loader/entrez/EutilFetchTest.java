/*
 * The Gemma project
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
package ubic.gemma.core.loader.entrez;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author paul
 */
public class EutilFetchTest extends TestCase {

    private static final Log log = LogFactory.getLog( EutilFetchTest.class.getName() );

    final public void testFetch() throws Exception {
        try {
            String result = EutilFetch.fetch( "gds", "GSE4595", 2 );
            TestCase.assertNotNull( result );
            TestCase.assertTrue( "Got " + result, result.startsWith( "<?xml" ) );
        } catch ( Exception e ) {
            if ( e.getCause() instanceof IOException && e.getCause().getMessage().contains( "502" ) ) {
                EutilFetchTest.log.warn( "Error 502 from NCBI, skipping test" );
                return;
            } else if ( e.getCause() instanceof IOException && e.getCause().getMessage().contains( "503" ) ) {
                EutilFetchTest.log.warn( "Error 503 from NCBI, skipping test" );
                return;
            } else if ( e.getCause() instanceof FileNotFoundException ) {
                EutilFetchTest.log.warn( "FileNotFound - is Eutil down? Skipping test" );
                return;
            }
            throw ( e );
        }
    }
}
