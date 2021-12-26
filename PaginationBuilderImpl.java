package com.replace.replace.api.pagination;

import com.replace.replace.api.pagination.condition.Condition;
import com.replace.replace.api.pagination.condition.ConditionBuilder;
import com.replace.replace.api.pagination.exception.NotSupportedKey;
import com.replace.replace.api.pagination.exception.NotSupportedOperator;
import com.replace.replace.api.pagination.exception.NotSupportedValue;
import com.replace.replace.api.pagination.query.Query;
import com.replace.replace.api.pagination.query.QueryBuilder;
import com.replace.replace.api.request.Request;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Service
public class PaginationBuilderImpl implements PaginationBuilder {

    protected final EntityManager entityManager;


    public PaginationBuilderImpl( final EntityManager entityManager ) {
        this.entityManager = entityManager;
    }


    @Override
    public < T > Pagination getResult( final Request request, final Class< T > dtoType, final String view )
            throws NotSupportedOperator, NotSupportedKey, NotSupportedValue {

        this.setDefaultRequiredValues( request );

        final List< Condition > conditions = ConditionBuilder.getConditions( request );
        final Query             query      = QueryBuilder.build( request, conditions, view );

        final int perPage = Integer.parseInt( request.getQueryString( "perPage" ) );
        final int page    = Integer.parseInt( request.getQueryString( "page" ) );
        final int offset  = this.getOffset( perPage, page );

        final Pagination pagination = new Pagination();
        pagination.setPerPage( perPage )
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
        final javax.persistence.Query persistentQuery =
                this.entityManager.createNativeQuery( query.getDataQuery(), type );


        for ( final Map.Entry< String, String > entry : query.getParameters().entrySet() ) {
            persistentQuery.setParameter( entry.getKey(), entry.getValue() );
        }

        return persistentQuery.getResultList();
    }


    protected int executeCountQuery( final Query query ) {
        final javax.persistence.Query persistentQuery = this.entityManager.createNativeQuery( query.getCountQuery() );

        for ( final Map.Entry< String, String > entry : query.getParameters().entrySet() ) {
            persistentQuery.setParameter( entry.getKey(), entry.getValue() );
        }

        return Integer.parseInt( String.valueOf( persistentQuery.getResultList().get( 0 ) ) );
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

        if ( request.getQueryString( "orderBy" ) == null ) {
            request.setQueryString( "orderBy", "DESC" );
        }

        if ( request.getQueryString( "sortBy" ) == null ) {
            request.setQueryString( "sortBy", "id" );
        }
    }
}
