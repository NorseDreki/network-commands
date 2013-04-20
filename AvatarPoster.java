package com.dummy.android.net.cmds;

import java.io.File;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import com.dummy.android.dm.entities.User;
import com.dummy.android.net.RestUrl;

/**
 * This command changes user avatar picture on profile page
 * 
 * @author upelsin
 *
 */
public class AvatarPoster extends BooleanResultCommand {

	private String filePath;

	/**
	 * Constructs instance of AvatarPoster
	 * 
	 * @param user user whose avatar to change
	 * @param filePath local path to new avatar picture
	 */
	public AvatarPoster(User user, String filePath) {
		this.filePath = filePath;
		this.user = user;
	}

	@Override
	public HttpRequest buildRequest() {
		super.buildRequest();
		uri.path(RestUrl.API_CHANGE_AVATAR);		
		HttpPost post = new HttpPost(uri.build().toString());
		
		addAuthHeaders(post);

		MultipartEntity entity = new MultipartEntity();
		FileBody body = new FileBody(new File(filePath), "image/jpeg");
		entity.addPart("avatar", body);

		post.setEntity(entity);

		return post;
	}
}
