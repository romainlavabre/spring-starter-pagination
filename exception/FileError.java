package com.replace.replace.api.pagination.exception;

public class FileError extends Exception {
    public FileError( String filePath ) {
        super( "Unable to get content of " + filePath + ( !filePath.contains( "classpath" ) ? ", consider using classpath" : "" ) );
    }
}
