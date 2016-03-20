package orz.yanagin.web2feed.queue;

import orz.yanagin.web2feed.resource.ResourceDao;
import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class QueueRemoveController implements Controller {

	@Override
	public Response execute(Request request) {
		String url = request.getParameter("url");
		if (url != null) {
			int count = new QueueDao().remove(url.trim());
			new ResourceDao().remove(url.trim());
			return Response.redirect("/queue/remove?count=" + count);
		}
		
		return Response.forward("/queue/remove.jsp");
	}

}
