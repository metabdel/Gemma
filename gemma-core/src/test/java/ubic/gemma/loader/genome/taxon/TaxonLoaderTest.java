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
package ubic.gemma.loader.genome.taxon;

import java.io.InputStream;

import ubic.gemma.testing.BaseSpringContextTest;

/**
 * @author pavlidis
 * @version $Id$
 */
public class TaxonLoaderTest extends BaseSpringContextTest {
    InputStream is;

    @Override
    protected void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        is = this.getClass().getResourceAsStream( "/data/loader/genome/taxon.names.dmp.sample.txt" );
    }

    public void testLoadInputStream() throws Exception {
        TaxonLoader tl = new TaxonLoader();
        tl.setFilter( false);
        tl.setPersisterHelper( persisterHelper );
        int actualValue = tl.load( is );
        assertEquals( 75, actualValue );
    }

    @Override
    protected void onTearDownInTransaction() throws Exception {
        is.close();
    }
}
