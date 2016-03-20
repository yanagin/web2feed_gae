package orz.yanagin.web2feed.datastore;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

public class DatastoreUtils {
	
	public static Key createKey(String kind, long id) {
		return KeyFactory.createKey(kind, id);
	}
	
	public static Key createKey(String kind, String s) {
		return KeyFactory.createKey(kind, s);
	}
	
	public static Entity createEntity(EntityConvertable bean) {
		return new Entity(bean.getKey());
	}
	
	public static int getInt(Entity entity, String name) {
		try {
			return (int)entity.getProperty(name);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	
	public static long getLong(Entity entity, String name) {
		try {
			return (long)entity.getProperty(name);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static String getString(Entity entity, String name) {
		try {
			return (String)entity.getProperty(name);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Text getText(Entity entity, String name) {
		try {
			return (Text)entity.getProperty(name);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Date getDate(Entity entity, String name) {
		try {
			return (Date)entity.getProperty(name);
		} catch (Exception e) {
			return null;
		}
	}
	
}
