package orz.yanagin.web2feed.resource;

import java.util.List;

import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class FeedController implements Controller {

	@Override
	public Response execute(Request request) {
		String queryString = request.getQueryString();
		List<Resource> resources = new ResourceDao().getResources(queryString, 20);
		
		return Response.forward("/feed/feed.jsp")
				.setAttribute("resources", resources);
	}

}
