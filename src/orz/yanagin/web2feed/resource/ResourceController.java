package orz.yanagin.web2feed.resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.appengine.api.datastore.KeyFactory;

import orz.yanagin.web2feed.web.Controller;
import orz.yanagin.web2feed.web.Request;
import orz.yanagin.web2feed.web.Response;

public class ResourceController implements Controller {

	@Override
	public Response execute(Request request) {
		String requestUri = request.getRequestUri();
		String key = requestUri.substring(requestUri.lastIndexOf("/") + 1);
		Resource resource = new ResourceDao().getResourceByKey(KeyFactory.stringToKey(key));
		
		Document document = Jsoup.parse(resource.getBody().getValue());
		document.select("head").append("<base href=\"" + resource.getUrl() + "\" />");
		
		return Response.write(document.html())
				.as(resource.getContentType() + "; charset=" + resource.getCharset());
	}

}
