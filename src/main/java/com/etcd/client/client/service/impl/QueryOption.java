package com.etcd.client.client.service.impl;

import io.etcd.jetcd.options.GetOption.SortOrder;

public class QueryOption
{
    private final SortOrder sortOrder;
    private String prefix;

    private QueryOption(QueryOptionBuilder builder)
    {
	this.sortOrder = builder.sortOrder;
	this.prefix = builder.prefix;
    }

    public SortOrder getSortOrder()
    {
	return sortOrder;
    }

    public String getPrefix()
    {
	return prefix;
    }

    public static QueryOptionBuilder newBuilder()
    {
	return new QueryOptionBuilder();
    }

    public static class QueryOptionBuilder
    {
	private SortOrder sortOrder = SortOrder.NONE;
	private String prefix;

	public QueryOptionBuilder withSortedOrder(SortOrder sortOrder)
	{
	    this.sortOrder = sortOrder;
	    return this;
	}

	public QueryOptionBuilder withPrefix(String prefix)
	{
	    this.prefix = prefix;
	    return this;
	}

	public QueryOption build()
	{
	    return new QueryOption(this);
	}
    }

    @Override
    public String toString()
    {
	return String.format("QueryOption [sortOrder=%s, prefix=%s]", sortOrder,
	    prefix);
    }

}
