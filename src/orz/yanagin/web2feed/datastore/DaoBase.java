package orz.yanagin.web2feed.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterator;

public abstract class DaoBase {

	private DatastoreService getDatastoreService() {
		return DatastoreServiceFactory.getDatastoreService();
	}
	
	protected boolean isEnableMemcache() {
		return true;
	}
	
	public static class QueryOptions {
		
		List<Filter> filters = new ArrayList<>();
		
		FetchOptions fetchOptions;
		
		List<String> sortKeys = new ArrayList<>();
		
		List<SortDirection> sortDirections = new ArrayList<>();
		
		public QueryOptions addFilter(Filter filter) {
			filters.add(filter);
			return this;
		}
		
		public QueryOptions setFetchOptions(FetchOptions fetchOptions) {
			this.fetchOptions = fetchOptions;
			return this;
		}
		
		public QueryOptions addSort(String name, SortDirection sortDirection) {
			sortKeys.add(name);
			sortDirections.add(sortDirection);
			return this;
		}
 		
	}
	
	protected static QueryOptions createQueryOptions() {
		return new QueryOptions();
	}

	protected <T extends EntityConvertable> T getByKey(Key key, Class<T> type) {
		Entity entity = null;
		
		if (isEnableMemcache()) {
			entity = MemcacheUtils.get(key);
			if (entity != null) {
				T instance = createInstance(type);
				instance.fromEntity(entity);
				return instance;
			}
		}
		
		try {
			entity = getDatastoreService().get(key);
			if (entity != null) {
				T instance = createInstance(type);
				instance.fromEntity(entity);
				return instance;
			}
		} catch (EntityNotFoundException e) {
		}
		
		return null;
	}
	
	protected List<Key> getKeys(QueryOptions queryOptions) {
		Query query = new Query();
		query.setKeysOnly();
		
		if (queryOptions.filters.size() == 1) {
			query.setFilter(queryOptions.filters.get(0));
		} else if (queryOptions.filters.size() > 1) {
			Filter filter = CompositeFilterOperator.and(queryOptions.filters);
			query.setFilter(filter);
		}
		
		if (!queryOptions.sortKeys.isEmpty()) {
			for (int i = 0; i < queryOptions.sortKeys.size(); i++) {
				query.addSort(queryOptions.sortKeys.get(i), queryOptions.sortDirections.get(i));
			}
		}
		
		PreparedQuery preparedQuery = getDatastoreService().prepare(query);
		
		QueryResultIterator<Entity> queryResults = null;
		if (queryOptions.fetchOptions != null) {
			queryResults = preparedQuery.asQueryResultIterator(queryOptions.fetchOptions);
		} else {
			queryResults = preparedQuery.asQueryResultIterator();
		}
	
		List<Key> result = new ArrayList<>();
		while (queryResults.hasNext()) {
			Entity entity = queryResults.next();
 			result.add(entity.getKey());
		}
		
 		return result;
	}

	protected <T extends EntityConvertable> List<T> getResults(QueryOptions queryOptions, Class<T> type) {
		T instance = createInstance(type);
		
		Query query = new Query(instance.getKind());
		query.setKeysOnly();
		
		if (queryOptions.filters.size() == 1) {
			query.setFilter(queryOptions.filters.get(0));
		} else if (queryOptions.filters.size() > 1) {
			Filter filter = CompositeFilterOperator.and(queryOptions.filters);
			query.setFilter(filter);
		}
		
		if (!queryOptions.sortKeys.isEmpty()) {
			for (int i = 0; i < queryOptions.sortKeys.size(); i++) {
				query.addSort(queryOptions.sortKeys.get(i), queryOptions.sortDirections.get(i));
			}
		}
		
		PreparedQuery preparedQuery = getDatastoreService().prepare(query);
		
		QueryResultIterator<Entity> queryResults = null;
		if (queryOptions.fetchOptions != null) {
			queryResults = preparedQuery.asQueryResultIterator(queryOptions.fetchOptions);
		} else {
			queryResults = preparedQuery.asQueryResultIterator();
		}
	
		List<T> result = new ArrayList<>();
		while (queryResults.hasNext()) {
			Entity entity = queryResults.next();
			Key key = entity.getKey();
			T bean = getByKey(key, type);
 			result.add(bean);
		}
		
 		return result;
	}

	protected <T extends EntityConvertable> T getSingleResult(QueryOptions queryOptions, Class<T> type) {
		queryOptions.setFetchOptions(FetchOptions.Builder.withLimit(1));
		List<T> result = getResults(queryOptions, type);
		return (result != null && !result.isEmpty()) ? result.get(0) : null;
	}
	
	protected boolean exists(Key key) {
		Query query = new Query(key.getKind());
		query.setKeysOnly();
		query.setFilter(new Query.FilterPredicate(
				Entity.KEY_RESERVED_PROPERTY,
				FilterOperator.EQUAL, 
				key));
		PreparedQuery preparedQuery = getDatastoreService().prepare(query);
		Entity entity = preparedQuery.asSingleEntity();
		return entity != null;
	}
	
	static <T> T createInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected Key put(EntityConvertable bean) {
		Entity entity = bean.toEntity();
		if (isEnableMemcache()) {
			MemcacheUtils.put(entity);
		}
		return getDatastoreService().put(entity);
	}
	
	protected List<Key> putList(List<? extends EntityConvertable> list) {
		if (list == null || list.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Entity> entities = new ArrayList<>();
		for (EntityConvertable bean : list) {
			Entity entity = bean.toEntity();
			if (isEnableMemcache()) {
				MemcacheUtils.put(entity);
			}
			entities.add(entity);
		}
		
		return getDatastoreService().put(entities);
	}

	protected void remove(Key key) {
		getDatastoreService().delete(key);
	}

	protected void remove(List<Key> keys) {
		getDatastoreService().delete(keys);
	}
	
}
