package orz.yanagin.web2feed.queue;

import java.util.List;

import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class QueueController implements Controller {

	@Override
	public Response execute(Request request) {
		List<Queue> queues = new QueueDao().getQueues(10);
		
		return Response.forward("/queue/index.jsp")
				.setAttribute("queues", queues);
	}

}
