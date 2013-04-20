package com.dummy.android.net.cmds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;

import com.google.gson.stream.JsonReader;

/**
 * Retrieves long textual descriptions of entities. Used when async loading
 * of text blocks is needed.
 * 
 * @author upelsin
 *
 */
public class DescriptionFetcher extends BaseCommand<String> {
	
	private String result;
	private String restUrl;

	/**
	 * Construct command by passing URI to entity to fetch
	 * 
	 * @param restUrl relative path to a single entity from API's host
	 */
	public DescriptionFetcher(String restUrl) {
		this.restUrl = restUrl;
	}

	@Override
	public String getResult() {
		return result;
	}

	/**
	 * Builds a request. Only URI path is used.
	 */
	@Override
	public HttpRequest buildRequest() {
		super.buildRequest();
    	uri.path(restUrl);
    	final HttpGet get = new HttpGet(uri.build().toString());
		
		return get;
	}

	/**
	 * Parses JSON response from REST server. Looks for 'data' block.
	 * If found, tries to read 'description' tag then returns it as String.
	 */
	@Override
	public boolean parseResponse(JsonReader reader) {
		try {
			while (reader.hasNext()) {
				String token = reader.nextName();

				if (token.equals("data")) {
					reader.beginObject();
					while (reader.hasNext()) {
						String subtoken = reader.nextName();

						if (subtoken.equals("description")) {
							result = reader.nextString();
							break;
						} else {
							reader.skipValue();
						}
					}
					break;
				} else {
					reader.skipValue();
				}
			}
			reader.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
