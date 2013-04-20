package com.dummy.android.net.cmds;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.dummy.android.core.Logging;
import com.dummy.android.dm.RestUrlProvider;
import com.dummy.android.dm.entities.User;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Class for generic event posting. Events come in list and should contain information
 * about appropriate server path to be posted to (see {@link RestUrlProvider}). 
 * Server returns true or false.
 * 
 * @author upelsin
 *
 */
public class EventPoster extends BooleanResultCommand {

	private List<? extends RestUrlProvider> events;

	/**
	 * Constructs EventPoster instance. Needs authentication headers.
	 * 
	 * @param user user whose credentials are used to post events
	 * @param events list of events having information on server paths
	 */
	public EventPoster(User user, List<? extends RestUrlProvider> events) {
		this.user = user;
		this.events = events;
	}

	/**
	 * Creates POST request to post events to {@link #getRestUrl()} endpoint
	 */
	@Override
	public HttpRequest buildRequest() {
		if (events == null || events.size() == 0) {
			Log.w(TAG, "No events to send");
			return null;
		}

		super.buildRequest();
		uri.path(events.get(0).getRestUrl());		
		HttpPost post = new HttpPost(uri.build().toString());
		
		addAuthHeaders(post);

		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		String json = gson.toJson(events);
		json = json.replace("\u003d", "=");
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("data", json));
		if (Logging.NET) Log.d(TAG, "Events: " + json);

		try {
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			post.setEntity(ent);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return post;
	}
}