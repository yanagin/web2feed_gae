package orz.yanagin.web2feed.resource;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import orz.yanagin.web2feed.datastore.DatastoreUtils;
import orz.yanagin.web2feed.datastore.EntityConvertable;

public class Resource implements EntityConvertable {
	
	private String url;
	
	private String date;
	
	private String contentType;
	
	private String charset;
	
	private String title;
	
	private String description;
	
	private Text body;
	
	private long hashCode;
	
	private Date updatedAt;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Text getBody() {
		return body;
	}

	public void setBody(Text body) {
		this.body = body;
	}

	public long getHashCode() {
		return hashCode;
	}

	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
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
				+ " date->" + date
				+ " contentType->" + contentType
				+ " charset->" + charset
				+ " title->" + title
				+ " description->" + description
				+ " hashCode->" + hashCode
				+ " updatedAt->" + updatedAt;
	}
	
	public String getKeyString() {
		return KeyFactory.keyToString(getKey());
	}
	
	@Override
	public String getKind() {
		return "resource";
	}
	
	@Override
	public Key getKey() {
		return DatastoreUtils.createKey(getKind(), url);
	}
	
	@Override
	public void fromEntity(Entity entity) {
		url = DatastoreUtils.getString(entity, "url");
		date = DatastoreUtils.getString(entity, "date");
		contentType = DatastoreUtils.getString(entity, "contentType");
		charset = DatastoreUtils.getString(entity, "charset");
		title = DatastoreUtils.getString(entity, "title");
		description = DatastoreUtils.getString(entity, "description");
		body = DatastoreUtils.getText(entity, "body");
		hashCode = DatastoreUtils.getLong(entity, "hashCode");
		updatedAt = DatastoreUtils.getDate(entity, "updatedAt");
	}

	@Override
	public Entity toEntity() {
		Entity entity = DatastoreUtils.createEntity(this);
		entity.setProperty("url", url);
		entity.setProperty("date", date);
		entity.setProperty("contentType", contentType);
		entity.setProperty("charset", charset);
		entity.setProperty("title", title);
		entity.setProperty("description", description);
		entity.setProperty("body", body);
		entity.setProperty("hashCode", hashCode);
		entity.setProperty("updatedAt", updatedAt);
		return entity;
	}

}
