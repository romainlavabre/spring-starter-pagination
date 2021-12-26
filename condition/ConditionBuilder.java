package com.replace.replace.api.pagination.condition;

import com.replace.replace.api.request.Request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class ConditionBuilder {

    public static List< Condition > getConditions( final Request request ) {


        final String            queryString  = request.getRawQueryString();
        final String[]          queryStrings = queryString.split( "&" );
        final List< Condition > result       = new ArrayList<>();

        for ( int i = 0; i < queryStrings.length; i++ ) {
            queryStrings[ i ] = URLDecoder.decode( queryStrings[ i ], StandardCharsets.UTF_8 );

            if ( !queryStrings[ i ].matches( "[a-zA-Z0-9]+\\[[a-z]+\\]=.+" ) ) {
                continue;
            }


            final String[] pair     = queryStrings[ i ].split( "=" );
            final String   key      = pair[ 0 ].split( "\\[" )[ 0 ];
            final String   operator = pair[ 0 ].replace( key, "" ).replaceAll( "\\[|\\]", "" );
            final String   value    = pair[ 1 ];

            Condition conditionFound = null;

            for ( final Condition condition : result ) {
                if ( condition.isKey( key ) && condition.isOperator( operator ) ) {
                    conditionFound = condition;
                    break;
                }
            }

            if ( conditionFound == null ) {
                final Condition condition = new Condition( key, operator );
                condition.addValue( value );
                result.add( condition );
                continue;
            }

            conditionFound.addValue( value );
        }

        return result;
    }
}
