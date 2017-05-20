/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.viperfish.chatapplication.core.FilterException;
import net.viperfish.chatapplication.core.LSFilter;
import net.viperfish.chatapplication.core.LSFilterChain;
import net.viperfish.chatapplication.core.LSPayload;
import net.viperfish.chatapplication.core.LSRequest;
import net.viperfish.chatapplication.core.LSResponse;
import net.viperfish.chatapplication.core.RequestHandler;

/**
 * The default implementation of a container of filters. This class is not
 * designed with thread safety.
 * 
 * @author sdai
 *
 */
class DefaultFilterChain implements LSFilterChain {

	private RequestHandler endpoint;
	private final List<LSFilter> filters;
	private int current;
	private Logger logger;

	/**
	 * creates an empty filter chain
	 */
	public DefaultFilterChain() {
		filters = new LinkedList<>();
		logger = LogManager.getLogger();
	}

	/**
	 * gets the {@link RequestHandler} for the current request
	 * 
	 * @return the current request handler
	 */
	public RequestHandler getEndpoint() {
		return endpoint;
	}

	/**
	 * sets the {@link RequestHandler} at the end of the chain
	 * 
	 * @param endpoint
	 *            the {@link RequestHandler}
	 */
	public void setEndpoint(RequestHandler endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * adds a filter to the end of the chain
	 * 
	 * @param filter
	 *            the filter to add
	 */
	public void addFilter(LSFilter filter) {
		filters.add(filter);
	}

	/**
	 * process the given request through the chain. Each filters are invoked
	 * based on the orer of its addition into the filter chain.
	 * 
	 * @param req
	 *            the request to process
	 * @param payload
	 *            the payload to postprocess
	 * @return the status of the request
	 */
	public LSResponse process(LSRequest req, LSPayload payload) {
		current = 0;
		try {
			return doFilter(req, payload);
		} catch (FilterException ex) {
			// if an exceptional status occurs during the filtering process
			logger.info("exceptional filter status", ex);
			return ex.getStatus();
		}
	}

	/**
	 * process the request and payload through the chain of filters
	 * 
	 * @param req
	 *            the request to process
	 * @param resp
	 *            the payload to post process
	 * @return the status of the request
	 */
	@Override
	public LSResponse doFilter(LSRequest req, LSPayload resp) throws FilterException {
		if (current < filters.size()) {
			// go to the next filter if the current is not the last filter
			LSFilter filter = filters.get(current);
			current += 1;
			return filter.doFilter(req, resp, this);
		} else {
			// go to the request handler
			return endpoint.handleRequest(req, resp);
		}
	}

}
