package com.replace.replace.api.pagination.query;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class Query {

    private String dataQuery;

    private String countQuery;

    private final Map< String, String > parameters;

    private int offset;

    private int limit;


    public Query() {
        this.parameters = new HashMap<>();
    }


    public String getDataQuery() {
        return this.dataQuery;
    }


    public void setDataQuery( final String dataQuery ) {
        this.dataQuery = dataQuery;
    }


    public String getCountQuery() {
        return this.countQuery;
    }


    public void setCountQuery( final String countQuery ) {
        this.countQuery = countQuery;
    }


    public Map< String, String > getParameters() {
        return this.parameters;
    }


    public void addParameter( final String key, final String value ) {
        this.parameters.put( key, value );
    }


    public int getOffset() {
        return this.offset;
    }


    public void setOffset( final int offset ) {
        this.offset = offset;
    }


    public int getLimit() {
        return this.limit;
    }


    public void setLimit( final int limit ) {
        this.limit = limit;
    }
}
