package com.replace.replace.api.pagination;

import com.replace.replace.api.json.Encoder;
import com.replace.replace.api.json.annotation.Group;
import com.replace.replace.api.json.annotation.Json;
import com.replace.replace.configuration.json.GroupType;

import java.util.List;
import java.util.Map;

/**
 * @author Romain Lavabre <romainlavabre98@gmail.com>
 */
public class Pagination {

    @Json( groups = {
            @Group( key = "current_page" )
    } )
    private int currentPage;

    @Json( groups = {
            @Group( key = "from" )
    } )
    private int from;

    @Json( groups = {
            @Group( key = "to" )
    } )
    private int to;

    private List< Object > data;

    @Json( groups = {
            @Group( key = "per_page" )
    } )
    private int perPage;

    @Json( groups = {
            @Group()
    } )
    private long total;

    @Json( groups = {
            @Group( key = "last_page" )
    } )
    private int lastPage;


    public int getCurrentPage() {
        return this.currentPage;
    }


    public Pagination setCurrentPage( final int currentPage ) {
        this.currentPage = currentPage;

        return this;
    }


    public int getFrom() {
        return this.from;
    }


    public Pagination setFrom( final int from ) {
        this.from = from;

        return this;
    }


    public int getTo() {
        return this.to;
    }


    public Pagination setTo( final int to ) {
        this.to = to;

        return this;
    }


    public List< Object > getData() {
        return this.data;
    }


    public Pagination setData( final List< Object > data ) {
        this.data = data;

        return this;
    }


    public int getPerPage() {
        return this.perPage;
    }


    public Pagination setPerPage( final int perPage ) {
        this.perPage = perPage;

        return this;
    }


    public long getTotal() {
        return this.total;
    }


    public Pagination setTotal( final long total ) {
        this.total = total;

        return this;
    }


    public int getLastPage() {
        return this.lastPage;
    }


    public Pagination setLastPage( final int lastPage ) {
        this.lastPage = lastPage;

        return this;
    }


    public Map< String, Object > encode( final String group ) {
        final Map< String, Object > result = Encoder.encode( this, GroupType.DEFAULT );

        result.put( "data", Encoder.encode( this.data, group ) );

        return result;
    }
}
