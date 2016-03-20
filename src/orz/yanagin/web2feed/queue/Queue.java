package orz.yanagin.web2feed.queue;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

import orz.yanagin.web2feed.datastore.DatastoreUtils;
import orz.yanagin.web2feed.datastore.EntityConvertable;

public class Queue implements EntityConvertable {

	private String url;
	
	private Date updatedAt;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "url->" + url
				+ " updatedAt->" + updatedAt;
	}
	
	@Override
	public String getKind() {
		return "queue";
	}
	
	@Override
	public Key getKey() {
		return DatastoreUtils.createKey(getKind(), url);
	}

	@Override
	public void fromEntity(Entity entity) {
		url = DatastoreUtils.getString(entity, "url");
		updatedAt = DatastoreUtils.getDate(entity, "updatedAt");
	}

	@Override
	public Entity toEntity() {
		Entity entity = DatastoreUtils.createEntity(this);
		entity.setProperty("url", url);
		entity.setProperty("updatedAt", updatedAt);
		return entity;
	}
	
}
