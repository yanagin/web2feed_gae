package orz.yanagin.web2feed.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class Response {
	
	public static class ForwardResponse extends Response {
		
		private final String path;
		
		private final Map<String, Object> attributes = new HashMap<>();
		
		private ForwardResponse(String path) {
			this.path = path;
		}
		
		@Override
		void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			for (Map.Entry<String, Object> entry : getAttributes().entrySet()) {
				request.setAttribute(entry.getKey(), entry.getValue());
			}
			
			request.getRequestDispatcher("/WEB-INF/view" + path).forward(request, response);
		}

		public Response setAttribute(String name, Object value) {
			attributes.put(name, value);
			return this;
		}

		public Map<String, Object> getAttributes() {
			return attributes;
		}
		
	}
	
	public static ForwardResponse forward(String path) {
		return new ForwardResponse(path);
	}
	
	public static class RedirectResponse extends Response {
		
		private final String path;

		private RedirectResponse(String path) {
			this.path = path;
		}
		
		@Override
		void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			response.sendRedirect(path);
		}
			
	}
	
	public static RedirectResponse redirect(String path) {
		return new RedirectResponse(path);
	}
	
	public static class PrintResponse extends Response {
		
		private final String body;
		
		private String contentType = "txt/html";

		private PrintResponse(String body) {
			this.body = body;
		}
		
		@Override
		void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			response.setContentType(contentType);
			response.getWriter().write(body);
		}
		
		public PrintResponse as(String contentType) {
			this.contentType = contentType;
			return this;
		}
			
	}
	
	public static PrintResponse write(String body) {
		return new PrintResponse(body);
	}

	abstract void execute(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

}
