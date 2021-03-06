package ubic.gemma.core.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryParser.QueryParser;
import ubic.gemma.model.common.search.SearchSettings;
import ubic.gemma.model.genome.Taxon;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

class SearchSettingsStringUtils {

    /**
     * Add anything that should be removed from the search string here. Lowercase.
     */
    private static final String[] STRINGS_TO_REMOVE = new String[] { "all", "results", "for" };

    public static String stripShortTerms( String query ) {
        String[] searchTerms = query.split( "\\s+" );

        if ( searchTerms.length > 0 ) {
            StringBuilder queryBuilder = new StringBuilder();
            for ( String sTerm : searchTerms ) {
                if ( sTerm.length() > 1 ) {
                    queryBuilder.append( " " ).append( sTerm );
                }
            }
            query = queryBuilder.toString();
            query = query.trim();
        }
        return query;
    }

    /**
     * Checks whether there is a taxon set in the given SearchSettings, and if not, tries to extract a taxon from the
     * SearchSettings query
     *
     * @param  settings settings
     * @return          search settings
     */
    public static SearchSettings processSettings( SearchSettings settings, Map<String, Taxon> nameToTaxonMap ) {

        if ( settings != null && settings.getTaxon() == null ) {

            settings = SearchSettingsStringUtils.processSearchString( settings );
            String searchString = settings.getQuery();

            // split the query around whitespace characters, limit the splitting to 4 terms (may be excessive)
            String[] searchTerms = searchString.split( "\\s+", 4 );

            List<String> searchTermsList = Arrays.asList( searchTerms );

            // this Set is ordered by insertion order(LinkedHashMap)
            Set<String> keywords = nameToTaxonMap.keySet();

            // only strip out taxon terms if there is more than one search term in query and if the entire search string
            // is not itself a keyword
            if ( searchTerms.length > 1 && !keywords.contains( searchString ) ) {

                for ( String keyword : keywords ) {

                    // make sure that the keyword occurs in the searchString
                    if ( searchString.contains( keyword ) ) {

                        // make sure that either the keyword is multi-term or that it occurs as a single term(not as
                        // part of another word)
                        if ( keyword.contains( " " ) || searchTermsList.contains( keyword ) ) {
                            searchString = searchString.replaceFirst( "(?i)" + keyword, "" ).trim();
                            settings.setTaxon( nameToTaxonMap.get( keyword ) );
                            // break on first term found in keywords since they should be(more or less) ordered by
                            // precedence
                            break;
                        }
                    }
                }
            }

            settings.setQuery( searchString );
        }

        return settings;
    }

    /**
     * Makes the query lower case, removes quotes and removes all (sub)strings in STRINGS_TO_REMOVE array from it.
     *
     * @param  settings settings
     * @return          search settings
     */
    private static SearchSettings processSearchString( SearchSettings settings ) {
        String searchString = QueryParser.escape( settings.getQuery().toLowerCase() );

        StringBuilder newString = new StringBuilder();
        String[] searchTerms = searchString.split( "\\s+" );

        for ( String term : searchTerms ) {
            boolean skip = false;
            // this is probably a relic of some weird way we passed some canned queries, but they are reasonable stop words.
            for ( String s : SearchSettingsStringUtils.STRINGS_TO_REMOVE ) {
                if ( s.equals( term ) ) {
                    skip = true;
                }
            }
            if ( skip )
                continue;

            newString.append( term.replaceAll( "['\"]", "" ) ).append( " " );
        }

        settings.setQuery( StringUtils.strip( newString.toString() ) );

        return settings;
    }
}
