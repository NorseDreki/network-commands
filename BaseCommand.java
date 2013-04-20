package com.dummy.android.net.cmds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpRequest;

import com.dummy.android.core.Logging;
import com.dummy.android.net.RestUrl;

import android.net.Uri;
import android.util.Log;

import com.google.gson.stream.JsonReader;

/**
 * Basic implementation of interface NetworkCommand<T>. Every command comes with
 * these query parameters:
 *   - SK - dummy key to protect API from random person access in browser
 *   - noCache - hash which solves issue of requests caching by some Internet providers
 *   
 * @author upelsin
 *
 * @param <T> see {@link INetworkCommand}
 */
public abstract class BaseCommand<T> implements INetworkCommand<T> {
	
	protected final String TAG = getClass().getSimpleName();
	
	protected Uri.Builder uri;

	/**
	 * Starts building URI for request, adding necessary query parameters
	 */
	@Override
	public HttpRequest buildRequest() {
		uri = new Uri.Builder();
		uri.appendQueryParameter("SK", RestUrl.SK);
		uri.appendQueryParameter("noCache", System.currentTimeMillis() + "");
		return null;
	}

	/**
	 * Pre-parses JSON response. Looks for status tag which is present in every response.
	 * Sets current success per this tag.
	 * 
	 * @param reader JSON reader to read response from
	 * 
	 * @return overall status of response
	 */
	@Override
	public boolean parseResponse(JsonReader reader) {
		boolean success = false;
		try {
			reader.beginObject();
			while (reader.hasNext()) {
				String token = reader.nextName();
				if (token.equals("status")) {
					success = reader.nextBoolean();
					if (Logging.NET) Log.d(TAG, "BaseCommand status: " + success);
					break;
				} else {
					reader.skipValue();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * see {@link INetworkCommand}
	 */
	@Override
	public abstract T getResult();
}
