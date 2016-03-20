package orz.yanagin.web2feed.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import orz.yanagin.web2feed.datastore.DaoBase;

public class ResourceDao extends DaoBase {
	
	public Resource getResource(String url) {
		Resource resource = new Resource();
		resource.setUrl(url);
		return getResourceByKey(resource.getKey());
	}
	
	public Resource getResourceByKey(Key key) {
		return getByKey(key, Resource.class);
	}
	
	public List<Resource> getResources(int limit) {
		return getResources(null, limit);
	}
	
	public List<Resource> getResources(String url, int limit) {
		QueryOptions queryOptions = createQueryOptions();
		
		if (url != null && !"".equals(url)) {
			queryOptions.addFilter(new Query.FilterPredicate(
					"url",
					FilterOperator.GREATER_THAN_OR_EQUAL, 
					url));
		}

		queryOptions.addSort("url", SortDirection.ASCENDING);
		queryOptions.addSort("updatedAt", SortDirection.DESCENDING);
		
		queryOptions.setFetchOptions(FetchOptions.Builder.withLimit(limit));
		
		List<Resource> resources = getResults(queryOptions, Resource.class);
		
		if (resources != null) {
			Collections.sort(resources, new Comparator<Resource>() {
				@Override
				public int compare(Resource o1, Resource o2) {
					if (o1.getUpdatedAt() == null) {
						return -1;
					}
					if (o2.getUpdatedAt() == null) {
						return 1;
					}
					return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
				}
			});
		}
		
		if (url == null || "".equals(url)) {
			return resources;
		}
		
		List<Resource> result = new ArrayList<>();
		for (Resource resource : resources) {
			if (resource.getUrl().startsWith(url)) {
				result.add(resource);
			}
		}
		return result;
	}
	
	public boolean exists(String url) {
		Resource resource = new Resource();
		resource.setUrl(url);
		return exists(resource.getKey());
	}
	
	public void regist(Resource resource) {
		put(resource);
	}

	public int remove(String url) {
		QueryOptions queryOptions = createQueryOptions();
		queryOptions.addFilter(new Query.FilterPredicate(
				"url",
				FilterOperator.GREATER_THAN_OR_EQUAL, 
				url));
		queryOptions.addSort("url", SortDirection.ASCENDING);
		queryOptions.setFetchOptions(FetchOptions.Builder.withLimit(100));
		
		List<Resource> resources = getResults(queryOptions, Resource.class);
		if (resources == null || resources.isEmpty()) {
			return 0;
		}
		
		List<Key> keys = new ArrayList<>();
		for (Resource resource : resources) {
			if (!resource.getUrl().startsWith(url)) {
				continue;
			}
			
			keys.add(resource.getKey());
		}
		
		if (keys == null || keys.isEmpty()) {
			return 0;
		}
		
		remove(keys);
		
		return keys.size();
	}
	
}
