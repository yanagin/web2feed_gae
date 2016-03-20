package orz.yanagin.web2feed.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import orz.yanagin.web2feed.queue.QueueController;
import orz.yanagin.web2feed.queue.QueueRegistController;
import orz.yanagin.web2feed.queue.QueueRemoveController;
import orz.yanagin.web2feed.resource.FeedController;
import orz.yanagin.web2feed.resource.ResourceController;
import orz.yanagin.web2feed.task.CrawlWorker;

public class RequestFacade implements Filter {
	
	static class Mapping {
		
		final String uri;
		
		final Controller controller;

		public Mapping(String uri, Controller controller) {
			this.uri = uri;
			this.controller = controller;
		}

		public String getUri() {
			return uri;
		}

		public Controller getController() {
			return controller;
		}
		
	}
	
	private final List<Mapping> mappings = new ArrayList<>();
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		mappings.add(new Mapping("/queue/regist", new QueueRegistController()));
		mappings.add(new Mapping("/queue/remove", new QueueRemoveController()));
		mappings.add(new Mapping("/queue", new QueueController()));

		mappings.add(new Mapping("/feed", new FeedController()));
		mappings.add(new Mapping("/resource", new ResourceController()));

		mappings.add(new Mapping("/task/crawl", new CrawlWorker()));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		doFilter((HttpServletRequest)request, (HttpServletResponse)response, chain);
	}
	
	void doFilter(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain)
			throws IOException, ServletException {
		String path = httpRequest.getRequestURI();
		Controller controller = getController(path);
		if (controller != null) {
			execute(httpRequest, httpResponse, controller);
		} else {
			chain.doFilter(httpRequest, httpResponse);
		}
	}
	
	Controller getController(String path) {
		for (Mapping mapping : mappings) {
			if (path.startsWith(mapping.getUri())) {
				return mapping.getController();
			}
		}
		return null;
	}
	
	void execute(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Controller controller) throws IOException, ServletException {
		Request request = new Request(httpRequest);
		for (Object key : httpRequest.getParameterMap().keySet()) {
			if (key == null) {
				continue;
			}
			request.getParams().put(key.toString(), httpRequest.getParameter(key.toString()));
		}
		
		Response response = controller.execute(request);
		if (response == null) {
			return;
		}
		
		response.execute(httpRequest, httpResponse);
	}

	@Override
	public void destroy() {
	}

}
