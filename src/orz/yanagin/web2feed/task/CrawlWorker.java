package orz.yanagin.web2feed.task;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import orz.yanagin.web2feed.crawler.Crawler;
import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class CrawlWorker implements Controller {

	@Override
	public Response execute(Request request) {
		int count = new Crawler().start();
		if (count > 0) {
			 Queue queue = QueueFactory.getQueue("crawl");
		     queue.add(TaskOptions.Builder.withUrl("/task/crawl"));
		}
		return null;
	}

}
