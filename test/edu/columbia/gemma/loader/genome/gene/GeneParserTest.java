package edu.columbia.gemma.loader.genome.gene;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.columbia.gemma.BaseServiceTestCase;
import edu.columbia.gemma.loader.loaderutils.Loader;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 - 2005 Columbia University
 * 
 * @author keshav
 * @version $Id$
 */
public class GeneParserTest extends BaseServiceTestCase {

    protected static final Log log = LogFactory.getLog( GeneParserTest.class );

    private Loader geneLoader = null;
    private GeneParser geneParser = null;
    private Map map = null;

    public void testParseFileValidFile() throws Exception {
        InputStream is = this.getClass().getResourceAsStream( "/data/loader/genome/gene/geneinfo" );
        Method m = geneParser.findParseLineMethod( "geneinfo" );
        geneParser.parse( is, m );

        InputStream is2 = this.getClass().getResourceAsStream( "/data/loader/genome/gene/gene2accession" );
        Method m2 = geneParser.findParseLineMethod( "gene2accession" );
        map = geneParser.parse( is2, m2 );

        // add a new stream for each file.

        geneLoader.create( map.values() );

        assertEquals( null, null );

    }

    protected void setUp() throws Exception {
        super.setUp();
        geneParser = new GeneParserImpl();
        geneLoader = new GeneLoaderImpl();
        map = new HashMap();

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        // TODO can you pass arguments to JUnit tests so I can select this option at runtime?
        // geneLoader.removeAll( map.values() );
        geneParser = null;
        geneLoader = null;
        map = null;
    }

    // public void testParseFileInvalidFile() throws Exception {
    // try {
    // geneParser.parseFile( "badfilename" );
    // } catch ( IOException e ) {
    // e.printStackTrace();
    // assertEquals( null, null );
    // }
    // }
}
