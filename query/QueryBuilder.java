package com.replace.replace.api.pagination.query;

import com.replace.replace.api.pagination.condition.Condition;
import com.replace.replace.api.pagination.exception.NotSupportedKey;
import com.replace.replace.api.pagination.exception.NotSupportedOperator;
import com.replace.replace.api.pagination.exception.NotSupportedValue;
import com.replace.replace.api.request.Request;

import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class QueryBuilder {

    public static Query build( final Request request, final List< Condition > conditions, final String view )
            throws NotSupportedKey, NotSupportedOperator, NotSupportedValue {
        final Query query = new Query();

        final StringBuilder sqlQuery = new StringBuilder( "SELECT {SELECTED} FROM " + view );

        if ( !conditions.isEmpty() ) {
            sqlQuery.append( " WHERE" );
        }

        for ( int i = 0; i < conditions.size(); i++ ) {
            final Condition condition = conditions.get( i );

            if ( i == 0 ) {
                sqlQuery.append( " " );
            } else {
                sqlQuery.append( " AND " );
            }

            sqlQuery.append( condition.consume( i + 1 ) );

            for ( final Map.Entry< String, String > entry : condition.getParameters().entrySet() ) {
                query.addParameter( entry.getKey(), entry.getValue() );
            }
        }

        query.setCountQuery( sqlQuery.toString().replace( "{SELECTED}", "COUNT(id)" ) );
        final String sortBy  = request.getQueryString( "sortBy" );
        final String orderBy = request.getQueryString( "orderBy" );
        final String limit   = request.getQueryString( "per_page" );

        if ( !limit.matches( "[0-9]+" ) ) {
            throw new NotSupportedValue( "per_page", limit );
        }

        final int offset = Integer.parseInt( request.getQueryString( "per_page" ) ) * (Integer.parseInt( request.getQueryString( "page" ) ) - 1);


        sqlQuery.append( " " )
                .append( "ORDER BY" )
                .append( " " )
                .append( sortBy.replace( " ", "" ) )
                .append( " " )
                .append( orderBy.toUpperCase().equals( "ASC" ) ? "ASC" : "DESC" )
                .append( " " )
                .append( "LIMIT" )
                .append( " " )
                .append( limit )
                .append( " " )
                .append( "OFFSET" )
                .append( " " )
                .append( offset )
                .append( " " )
                .append( ";" );

        query.setOffset( offset );
        query.setLimit( Integer.parseInt( limit ) );
        query.setDataQuery( sqlQuery.toString().replace( "{SELECTED}", "*" ) );

        return query;
    }
}
