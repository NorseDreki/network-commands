package com.dummy.android.net.cmds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;

import com.dummy.android.core.Logging;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

/**
 * This class is used to retrieve any type of entity in dm.entities from REST server.
 * Server returns collection of entities which are then parsed in list of elements of type T.
 * Version of records is taken in account.
 * 
 * This class is mostly used in database update mechanics.
 * 
 * @author upelsin
 *
 * @param <T> type of elements in response from server
 */
public class EntityFetcher<T> extends BaseCommand<List<T>> {
	
	private Class<T> clazz;
	private List<T> result;
	private String restUrl;
	private int version;
	
	/**
	 * Constructs an instance of EntityFetcher.
	 * 
	 * @param clazz class of object of type T
	 * @param restUrl path to entity on server
	 * @param version stamp of newest record. All records modified since stamp are returned.
	 * 	If version == 0 all records are retrieved.
	 * 
	 */
	public EntityFetcher(Class<T> clazz, String restUrl, int version) {
		this.clazz = clazz;
		this.restUrl = restUrl;
		this.version = version;
	}
	
	/**
	 * Takes in account version control.
	 */
	@Override
	public HttpRequest buildRequest() {
		super.buildRequest();
    	uri.path(restUrl);
    	if (version != 0)
    		uri.appendQueryParameter("version", String.valueOf(version));
    	
    	final HttpGet get = new HttpGet(uri.build().toString());
    	if (Logging.NET) Log.d(TAG, get.getURI().toString());
		
		return get;
	}
	
	/**
	 * Parses server response in list of records of type T
	 */
	@Override
	public boolean parseResponse(JsonReader reader) {
		try {
			result = new ArrayList<T>();
        	GsonBuilder b = new GsonBuilder();
        	b.registerTypeAdapter(Boolean.class, new BooleanSerializer());
        	Gson gson = b.create();
			
			while (reader.hasNext()) {
				String token = reader.nextName();
				
				if (token.equals("data")) {
					reader.beginArray();
			        while (reader.hasNext()) {
			            T entry = gson.fromJson(reader, clazz);
			            result.add(entry);
			        }
			        reader.endArray();
			        break;
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
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
	
	@Override
	public List<T> getResult() {
		return result;
	}
	
	/**
	 * Class to convert int representation of boolean to true / false form.
	 * 
	 * @author upelsin
	 *
	 */
	private static class BooleanSerializer implements JsonDeserializer<Boolean> {
	    @Override
	    public Boolean deserialize(JsonElement arg0, Type arg1, 
	    		JsonDeserializationContext arg2) throws JsonParseException {
	    	
	        return arg0.getAsInt() == 1 ? true : false;
	    }
	}
}
