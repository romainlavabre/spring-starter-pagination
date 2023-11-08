package com.replace.replace.api.pagination.exception;

public class NotSupportedDtoType extends Exception {
    public NotSupportedDtoType() {
        super( "Dto must contains @Pagination annotation" );
    }
}
