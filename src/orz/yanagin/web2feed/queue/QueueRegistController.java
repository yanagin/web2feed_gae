package orz.yanagin.web2feed.queue;

import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class QueueRegistController implements Controller {

	@Override
	public Response execute(Request request) {
		String url = request.getParameter("url");
		if (url != null &&!"".equals(url)) {
			new QueueDao().regist(url.trim());
		}
		
		return Response.redirect("/queue");
	}

}
