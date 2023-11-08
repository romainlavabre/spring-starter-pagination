package com.replace.replace.api.pagination.query.file;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QueryFileParser {

    private final Map< String, ParsingResult >    CACHE                   = new HashMap<>();
    private final Map< String, KeywordsPosition > KEYWORDS_POSITION_CACHE = new HashMap<>();


    /**
     * @param filePath    Used for cache
     * @param fileContent
     * @return
     */
    public ParsingResult getFilter( String filePath, String fileContent ) {
        if ( CACHE.containsKey( filePath ) ) {
            return CACHE.get( filePath );
        }

        KeywordsPosition keywordsPosition = solvePositionOfPrincipalKeyword( filePath, fileContent );

        String selectPart = fileContent.substring( keywordsPosition.getSelectEnd() + 1, keywordsPosition.getFromStart() ).trim();

        String[] asPart = selectPart.split( " AS " );

        Map< String, String > filters = new HashMap<>();

        for ( int i = 0; i < asPart.length; i++ ) {
            if ( i == asPart.length - 1 ) {
                break;
            }
            String formula =
                    i == 0
                            ? asPart[ i ].trim()
                            : asPart[ i ].replaceFirst( ",", "--split--" ).split( "--split--" )[ 1 ].trim();
            String key = asPart[ i + 1 ].replaceFirst( ",", "--split--" ).split( "--split--" )[ 0 ].trim();

            filters.put( key, formula );


        }

        ParsingResult parsingResult = new ParsingResult( filters );

        CACHE.put( filePath, parsingResult );

        return parsingResult;
    }


    public String putCondition( String computedCondition, String fileContent ) {
        if ( computedCondition.isBlank() ) {
            return fileContent.replace( "{{condition}}", "1" );
        }

        return fileContent.replace( "{{condition}}", computedCondition );
    }


    public String toCountQuery( String filePath, String fileContent, String formula ) {
        KeywordsPosition keywordsPosition = solvePositionOfPrincipalKeyword( filePath, fileContent );

        return "SELECT COUNT(" + formula + ") FROM " + fileContent.substring( keywordsPosition.fromEnd + 1 );
    }


    public String putSort( String sortBy, String orderBy, String fileContent ) {
        return fileContent + "\nORDER BY " + sortBy + " " + ( orderBy.equalsIgnoreCase( "ASC" ) ? orderBy : "DESC" );
    }


    public String putLimitAndOffset( int limit, int offset, String fileContent ) {
        return fileContent + "\nLIMIT " + limit + " OFFSET " + offset;
    }


    private KeywordsPosition solvePositionOfPrincipalKeyword( String filePath, String fileContent ) {
        if ( KEYWORDS_POSITION_CACHE.containsKey( filePath ) ) {
            return KEYWORDS_POSITION_CACHE.get( filePath );
        }

        int selectStart     = -1;
        int selectEnd       = -1;
        int fromStart       = -1;
        int fromEnd         = -1;
        int depthOfSubQuery = 0;

        char[] chars = fileContent.toCharArray();

        for ( int i = 0; i < chars.length; i++ ) {
            if ( chars[ i ] == '(' ) {
                depthOfSubQuery++;
            }

            if ( chars[ i ] == ')' ) {
                depthOfSubQuery--;
            }

            if ( depthOfSubQuery == 0 && chars[ i ] == 'S' && chars[ i + 1 ] == 'E' && chars[ i + 2 ] == 'L' && chars[ i + 3 ] == 'E' && chars[ i + 4 ] == 'C' && chars[ i + 5 ] == 'T' ) {
                selectStart = i;
                selectEnd   = i + 5;
            }

            if ( depthOfSubQuery == 0 && chars[ i ] == 'F' && chars[ i + 1 ] == 'R' && chars[ i + 2 ] == 'O' && chars[ i + 3 ] == 'M' ) {
                fromStart = i;
                fromEnd   = i + 4;
            }
        }

        KeywordsPosition keywordsPosition = new KeywordsPosition( selectStart, selectEnd, fromStart, fromEnd );

        KEYWORDS_POSITION_CACHE.put( filePath, keywordsPosition );

        return keywordsPosition;
    }


    private class KeywordsPosition {
        private final int selectStart;
        private final int selectEnd;
        private final int fromStart;
        private final int fromEnd;


        private KeywordsPosition( int selectStart, int selectEnd, int fromStart, int fromEnd ) {
            this.selectStart = selectStart;
            this.selectEnd   = selectEnd;
            this.fromStart   = fromStart;
            this.fromEnd     = fromEnd;
        }


        public int getSelectStart() {
            return selectStart;
        }


        public int getSelectEnd() {
            return selectEnd;
        }


        public int getFromStart() {
            return fromStart;
        }


        public int getFromEnd() {
            return fromEnd;
        }


        @Override
        public String toString() {
            return "selectStart=" + selectStart + ";selectEnd=" + selectEnd + ";fromStart=" + fromStart + ";fromEnd=" + fromEnd;
        }
    }


    public class ParsingResult {
        /**
         * Key / Formula
         */
        private final Map< String, String > filters;


        public ParsingResult( Map< String, String > filters ) {
            this.filters = filters;
        }


        public Map< String, String > getFilters() {
            return filters;
        }
    }
}
