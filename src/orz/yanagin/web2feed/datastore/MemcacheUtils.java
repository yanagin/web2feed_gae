package orz.yanagin.web2feed.datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheUtils {
	
	public static void put(Entity entity) {
		MemcacheServiceFactory.getMemcacheService().put(KeyFactory.keyToString(entity.getKey()), entity);
	}
	
	public static Entity get(Key key) {
		Object obj = MemcacheServiceFactory.getMemcacheService().get(KeyFactory.keyToString(key));
		if (obj == null) {
			return null;
		}
		return (Entity)obj;
	}

}
