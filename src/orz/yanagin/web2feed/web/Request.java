package orz.yanagin.web2feed.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Request {
	
	private final String requestUri;
	
	private final Map<String, String> params = new HashMap<>();
	
	private final String queryString;

	public Request(HttpServletRequest request) {
		this.requestUri = request.getRequestURI();
		this.queryString = request.getQueryString();
	}

	public String getRequestUri() {
		return requestUri;
	}

	Map<String, String> getParams() {
		return params;
	}
	
	public String getParameter(String name) {
		return params.get(name);
	}

	public String getQueryString() {
		return queryString;
	}
	
}
