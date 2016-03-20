package orz.yanagin.web2feed.datastore;

import java.io.Serializable;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;

public interface EntityConvertable extends Serializable {
	
	String getKind();
	
	Key getKey();

	void fromEntity(Entity entity);

	Entity toEntity();
	
}
