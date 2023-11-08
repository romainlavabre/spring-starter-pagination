package com.replace.replace.api.pagination;

import com.fairfair.data_repository.api.environment.Environment;
import com.fairfair.data_repository.api.pagination.condition.Condition;
import com.fairfair.data_repository.api.pagination.condition.ConditionBuilder;
import com.fairfair.data_repository.api.pagination.exception.*;
import com.fairfair.data_repository.api.pagination.query.Query;
import com.fairfair.data_repository.api.pagination.query.QueryBuilder;
import com.fairfair.data_repository.api.request.Request;
import com.fairfair.data_repository.configuration.environment.Variable;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Service
public class PaginationHandlerImpl implements PaginationHandler {

    protected final EntityManager             entityManager;
    protected final Environment               environment;
    protected final QueryBuilder              queryBuilder;
    protected final Map< String, QueryCount > COUNT_QUERY_CACHE = new HashMap<>();


    public PaginationHandlerImpl( final EntityManager entityManager, Environment environment, QueryBuilder queryBuilder ) {
        this.entityManager = entityManager;
        this.environment   = environment;
        this.queryBuilder  = queryBuilder;
    }


    @Override
    public < T > Pagination getResult( Request request, Class< T > dtoType ) throws NotSupportedOperator, NotSupportedKey, NotSupportedValue, NotSupportedDtoType, FileError {
        this.setDefaultRequiredValues( request );

        final List< Condition > conditions = ConditionBuilder.getConditions( request );
        final Query             query      = queryBuilder.build( request, conditions, dtoType );

        final int perPage = Integer.parseInt( request.getQueryString( "perPage" ) != null ? request.getQueryString( "perPage" ) : request.getQueryString( "per_page" ) );
        final int page    = Integer.parseInt( request.getQueryString( "page" ) );
        final int offset  = this.getOffset( perPage, page );

        final Pagination pagination = new Pagination();
        pagination
                .setPerPage( perPage )
                .setTotal( this.executeCountQuery( query ) )
                .setFrom( query.getOffset() + 1 )
                .setTo( query.getOffset() + query.getLimit() > pagination.getTotal() ? Integer.parseInt( String.valueOf( pagination.getTotal() ) ) : query.getOffset() + query.getLimit() )
                .setCurrentPage( page )
                .setLastPage( this.getLastPage( perPage, pagination.getTotal() ) );

        query.setOffset( offset );
        query.setLimit( pagination.getTo() );

        pagination.setData( this.executeDataQuery( query, ( Class ) dtoType ) );


        return pagination;
    }


    protected < T > List< T > executeDataQuery( final Query query, final Class< T > type ) {
        final jakarta.persistence.Query persistentQuery =
                this.entityManager.createNativeQuery( query.getDataQuery(), type );


        for ( final Map.Entry< String, String > entry : query.getParameters().entrySet() ) {
            persistentQuery.setParameter( entry.getKey(), entry.getValue() );
        }

        return persistentQuery.getResultList();
    }


    protected int executeCountQuery( final Query query ) {
        String countQuery = query.getCountQuery();

        final jakarta.persistence.Query persistentQuery = this.entityManager.createNativeQuery( countQuery );

        String computedQuery = countQuery;

        for ( final Map.Entry< String, String > entry : query.getParameters().entrySet() ) {
            persistentQuery.setParameter( entry.getKey(), entry.getValue() );
            computedQuery = computedQuery.replace( ":" + entry.getKey(), entry.getValue() );
        }


        if ( COUNT_QUERY_CACHE.containsKey( computedQuery ) ) {
            QueryCount queryCount = COUNT_QUERY_CACHE.get( computedQuery );

            if ( queryCount.isValid() ) {
                return queryCount.getResult();
            }
        }

        int result = Integer.parseInt( String.valueOf( persistentQuery.getResultList().get( 0 ) ) );

        COUNT_QUERY_CACHE.put( computedQuery, new QueryCount( result ) );

        return result;
    }


    protected int getOffset( final int perPage, final int currentPage ) {
        return perPage * currentPage;
    }


    protected int getLastPage( final int perPage, final long totalLines ) {
        final double totalPage = totalLines / ( double ) perPage;
        final double modulo    = totalPage % ( double ) perPage;

        if ( modulo > 0 ) {
            return ( int ) totalPage + 1;
        }

        return ( int ) totalPage;
    }


    protected void setDefaultRequiredValues( final Request request ) {
        if ( request.getQueryString( "per_page" ) == null ) {
            request.setQueryString( "per_page", "20" );
        }

        if ( request.getQueryString( "page" ) == null ) {
            request.setQueryString( "page", "1" );
        }

        if ( request.getQueryString( "orderBy" ) == null && request.getQueryString( "order_by" ) == null ) {
            request.setQueryString( "order_by", "DESC" );
        }

        if ( request.getQueryString( "sortBy" ) == null && request.getQueryString( "sort_by" ) == null ) {
            request.setQueryString( "sort_by", "id" );
        }
    }


    private class QueryCount {
        private int result;

        private final ZonedDateTime generatedAt;


        public QueryCount( int result ) {
            this.result = result;
            generatedAt = ZonedDateTime.now( ZoneOffset.UTC );
        }


        public int getResult() {
            return result;
        }


        public ZonedDateTime getGeneratedAt() {
            return generatedAt;
        }


        public boolean isValid() {
            return generatedAt.plusSeconds( environment.getEnv( Variable.PAGINATION_COUNT_CACHE_SECONDS ) != null ? Long.parseLong( environment.getEnv( Variable.PAGINATION_COUNT_CACHE_SECONDS ) ) : 30 ).toEpochSecond()
                    > ZonedDateTime.now( ZoneOffset.UTC ).toEpochSecond();
        }
    }
}
