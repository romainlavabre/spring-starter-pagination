package com.replace.replace.api.pagination.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface Pagination {
    /**
     * Pagination mode <br/>
     * View : Search view in database (based on Pagination.view field)<br/>
     * File : Parse and execute query location in Pagination.filePath field
     */
    ModeType mode() default ModeType.VIEW;


    /**
     * View name
     */
    String view() default "";


    /**
     * File path of sql query
     * Sample classpath:sql/my-query.sql
     */
    String filePath() default "";


    boolean allowSorting() default true;
}
