package com.dummy.android.net.cmds;

import org.apache.http.HttpRequest;

import com.dummy.android.net.RestExecutor;

import com.google.gson.stream.JsonReader;

/**
 * Interface which describes generic network command. Implementation of this command 
 * is then taken by {@link RestExecutor} which uses http client 
 * to send request to remote REST server.
 * 
 * @author upelsin
 *
 * @param <T> type of data enclosed in server response, any POJO from dm.entities can be used
 */
public interface INetworkCommand<T> {

	/**
	 * Builds a request which should contain URL, verb and headers.
	 * 
	 * @return concrete subclass of HttpRequest
	 */
	public HttpRequest buildRequest();

	/**
	 * Parses JSON response from REST server which is the result of execution of this command.
	 * Generic structure of JSON file is as follows:
	 * {"success":true,"message":"Records Retrieved Successfully","data":[{}, {}, ...]}
	 * 
	 * @param reader JsonReader object to parse response with
	 * 
	 * @return true is parsing succeeded
	 * 
	 */
	public boolean parseResponse(JsonReader reader);
	
	/**
	 * Gets POJO instance of type T which was enclosed in JSON server response
	 * 
	 * @return instance of POJO of type T
	 */
	public T getResult();
	
}