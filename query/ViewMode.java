package com.replace.replace.api.pagination.query;

import com.fairfair.data_repository.api.pagination.annotation.Pagination;
import com.fairfair.data_repository.api.pagination.condition.Condition;
import com.fairfair.data_repository.api.pagination.exception.NotSupportedKey;
import com.fairfair.data_repository.api.pagination.exception.NotSupportedOperator;
import com.fairfair.data_repository.api.pagination.exception.NotSupportedValue;
import com.fairfair.data_repository.api.request.Request;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ViewMode {

    public Query get( Request request, final List< Condition > conditions, Pagination pagination ) throws NotSupportedValue, NotSupportedKey, NotSupportedOperator {
        final Query query = new Query();

        final StringBuilder sqlQuery = new StringBuilder( "SELECT {SELECTED} FROM " + pagination.view() );

        if ( !conditions.isEmpty() ) {
            sqlQuery.append( " WHERE" );
        }

        for ( int i = 0; i < conditions.size(); i++ ) {
            final Condition condition = conditions.get( i );

            if ( i == 0 ) {
                sqlQuery.append( " " );
            } else {
                String mode = request.getQueryString( "mode" );

                if ( !"include".equals( mode ) ) {
                    sqlQuery.append( " AND " );
                } else {
                    sqlQuery.append( " OR " );
                }
            }

            sqlQuery.append( condition.consume( i + 1, null ) );

            for ( final Map.Entry< String, String > entry : condition.getParameters().entrySet() ) {
                query.addParameter( entry.getKey(), entry.getValue() );
            }
        }

        query.setCountQuery( sqlQuery.toString().replace( "{SELECTED}", "COUNT(id)" ) );
        final String sortBy  = request.getQueryString( "sortBy" ) == null ? request.getQueryString( "sort_by" ) : request.getQueryString( "sortBy" );
        final String orderBy = request.getQueryString( "orderBy" ) == null ? request.getQueryString( "order_by" ) : request.getQueryString( "orderBy" );
        final String limit   = request.getQueryString( "per_page" );

        if ( !limit.matches( "[0-9]+" ) ) {
            throw new NotSupportedValue( "per_page", limit );
        }

        final int offset = Integer.parseInt( request.getQueryString( "per_page" ) ) * ( Integer.parseInt( request.getQueryString( "page" ) ) - 1 );

        sqlQuery.append( " " );

        if ( !Objects.equals( sortBy, "NONE" ) ) {
            sqlQuery.append( "ORDER BY" )
                    .append( " " )
                    .append( sortBy.replace( " ", "" ) )
                    .append( " " )
                    .append( orderBy.toUpperCase().equals( "ASC" ) ? "ASC" : "DESC" )
                    .append( " " );
        }

        sqlQuery.append( "LIMIT" )
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
