/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Collection;

/**
 * a container of filters. The filter chain is used by the filters to pass the
 * request to the next level of filters. If the calling filter is the last
 * filter of the chain, a request handler is invoked.
 * 
 * @author sdai
 */
public interface LSFilterChain {
	/**
	 * process an incoming request and outgoing payload & status.
	 * 
	 * @param req
	 *            the incoming request
	 * @param resp
	 *            the outgoing status
	 * @return the final status returned to the client
	 * @throws FilterException
	 *             if any error or exceptional condition occurs
	 */
	public LSResponse doFilter(LSRequest req, Collection<LSPayload> resp) throws FilterException;

}
