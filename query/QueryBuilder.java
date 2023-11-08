package com.replace.replace.api.pagination.query;

import com.fairfair.data_repository.api.pagination.annotation.ModeType;
import com.fairfair.data_repository.api.pagination.annotation.Pagination;
import com.fairfair.data_repository.api.pagination.condition.Condition;
import com.fairfair.data_repository.api.pagination.exception.*;
import com.fairfair.data_repository.api.request.Request;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
@Service( "PaginationQueryBuilder" )
public class QueryBuilder {

    protected final ViewMode viewMode;
    protected final FileMode fileMode;


    public QueryBuilder( ViewMode viewMode, FileMode fileMode ) {
        this.viewMode = viewMode;
        this.fileMode = fileMode;
    }


    public Query build( final Request request, final List< Condition > conditions, Class< ? > dtoType )
            throws NotSupportedKey, NotSupportedOperator, NotSupportedValue, NotSupportedDtoType, FileError {
        Pagination pagination = dtoType.getDeclaredAnnotation( Pagination.class );

        if ( pagination == null ) {
            throw new NotSupportedDtoType();
        }

        return pagination.mode() == ModeType.VIEW
                ? viewMode.get( request, conditions, pagination )
                : fileMode.get( request, conditions, pagination );
    }


}
