package orz.yanagin.web2feed.queue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import orz.yanagin.web2feed.datastore.DaoBase;

public class QueueDao extends DaoBase {
	
	private static Date DEFAULT_DATE;
	static {
		try {
			DEFAULT_DATE = new SimpleDateFormat("yyyy/MM/dd").parse("2016/01/01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Queue getLatestQueue() {
		QueryOptions queryOptions = createQueryOptions();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		queryOptions.addFilter(new Query.FilterPredicate(
				"updatedAt",
				FilterOperator.LESS_THAN, 
				cal.getTime()));
		
		queryOptions.addSort("updatedAt", SortDirection.ASCENDING);
		
		return getSingleResult(queryOptions, Queue.class);
	}
	
	public List<Queue> getQueues(int limit) {
		QueryOptions queryOptions = createQueryOptions();
		
		queryOptions.addSort("updatedAt", SortDirection.ASCENDING);
		
		queryOptions.setFetchOptions(FetchOptions.Builder.withLimit(limit));
		
		return getResults(queryOptions, Queue.class);
	}
	
	public boolean exists(String url) {
		Queue queue = new Queue();
		queue.setUrl(url);
		return exists(queue.getKey());
	}
	
	public void regist(String url) {
		Queue queue = new Queue();
		queue.setUrl(url);
		queue.setUpdatedAt(DEFAULT_DATE);
		put(queue);
	}
	
	public void regist(List<String> urls) {
		if (urls == null || urls.isEmpty()) {
			return;
		}
		
		List<Queue> queues = new ArrayList<>();
		for (String url : urls) {
			Queue queue = new Queue();
			queue.setUrl(url);
			queue.setUpdatedAt(DEFAULT_DATE);
			queues.add(queue);
		}

		putList(queues);
	}
	
	public void updateUpdatedAt(String url) {
		Queue queue = new Queue();
		queue.setUrl(url);
		queue.setUpdatedAt(new Date());
		put(queue);
	}

	public int remove(String url) {
		QueryOptions queryOptions = createQueryOptions();
		queryOptions.addFilter(new Query.FilterPredicate(
				"url",
				FilterOperator.GREATER_THAN_OR_EQUAL, 
				url));
		queryOptions.addSort("url", SortDirection.ASCENDING);
		queryOptions.setFetchOptions(FetchOptions.Builder.withLimit(100));
		
		List<Queue> queues = getResults(queryOptions, Queue.class);
		if (queues == null || queues.isEmpty()) {
			return 0;
		}
		
		List<Key> keys = new ArrayList<>();
		for (Queue queue : queues) {
			if (!queue.getUrl().startsWith(url)) {
				continue;
			}
			
			keys.add(queue.getKey());
		}
		
		if (keys == null || keys.isEmpty()) {
			return 0;
		}
		
		remove(keys);
		
		return keys.size();
	}
	
}
