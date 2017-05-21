/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.chatapplication.core;

import java.util.Collection;

/**
 * A filter that processes {@link LSRequest} before the {@link RequestHandler}s
 * and {@link LSPayload} after the {@link RequestHandler}s. This class is used
 * in conjecture with {@link LSFilterChain} to preprocess and postprocess any
 * incoming {@link LSRequest} or outgoing {@link LSResponse}.
 * 
 * @author sdai
 */
public interface LSFilter {
	/**
	 * processes incoming {@link LSRequest} and outgoing {@link LSPayload}. Any
	 * implementation of this class is expected to pass the parameters forward
	 * through the {@link LSFilterChain} and return a status based on the status
	 * from the {@link LSFilterChain}.
	 * 
	 * @param req
	 *            The incoming {@link LSRequest}
	 * @param resp
	 *            The outgoing {@link LSPayload}
	 * @param chain
	 *            the chain of filters that contains other filters. The
	 *            implementation is responsible for passing the parameters
	 *            forward to the chain, and to process and return the status
	 *            from the chain.
	 * @return the processed and/or returned status from the chain.
	 * @throws FilterException
	 *             if any excpetional conditions occur.
	 */
	public LSResponse doFilter(LSRequest req, Collection<LSPayload> resp, LSFilterChain chain) throws FilterException;
}
