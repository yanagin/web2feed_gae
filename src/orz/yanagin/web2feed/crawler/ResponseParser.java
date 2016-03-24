package orz.yanagin.web2feed.crawler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.appengine.api.datastore.Text;

import orz.yanagin.web2feed.resource.Resource;

public class ResponseParser {

	private final String url;

	private final Response response;
	
	private final String charset;
	
	private final Document document;

	public ResponseParser(String url, Response response) throws IOException {
		this.url = url;
		this.response = response;
		charset = getCharset(Jsoup.parse(response.body()));
		document = Jsoup.parse(new ByteArrayInputStream(response.bodyAsBytes()), charset, url);
	}

	public Resource getResource() {
		Resource resource = new Resource();
		resource.setUrl(url);
		resource.setDate(response.header("date"));
		resource.setContentType(response.contentType());
		resource.setCharset(this.charset);
		if (response.body() != null) {
			resource.setTitle(document.title());
			resource.setDescription(document.select("meta[name=description]").attr("content"));
			resource.setBody(new Text(response.body().trim()));
			resource.setHashCode(response.body().hashCode());
		}
		return resource;
	}
	
	String getCharset(Document document) {
		String charset = response.charset();
		if (charset != null) {
			return charset;
		}
		
		charset = document.select("meta").attr("charset");
		if (charset != null && !"".equals(charset.trim())) {
			return charset;
		}
		
		try {
			charset = document.select("meta[http-equiv=Content-Type]").attr("content");
			charset = charset.substring(charset.indexOf("charset=") + 8);
		} catch (Exception e) {
		}
		if (charset != null) {
			return charset;
		}
		
		return "utf8";
	}

	public List<String> getLinks() {
		List<String> links = new ArrayList<>();
		
		Elements elements = document.getElementsByTag("a");
		if (elements == null) {
			return links;
		}
		
		for (Element element : elements) {
			links.add(element.attr("href"));
		}
		
		return links;
	}

	public List<String> getInternalLinks() {
		return getInternalLinks(url, getLinks());
	}
	
	static List<String> getInternalLinks(String url, List<String> links) {
		Set<String> internalLinks = new HashSet<>();
		
		if (url == null || links == null || links.isEmpty()) {
			return new ArrayList<>();
		}

		int from = url.indexOf("://");
		int to = url.indexOf("/", from + 3);
		if (to < 0) {
			to = url.length();
		}
		String protocol = url.substring(0, from);
		String domain = url.substring(from + 3, to);
		String path = "/";
		if (url.substring(to).contains("/")) {
			path = url.substring(to, to + url.substring(to).lastIndexOf("/") + 1);
		}

		String baseUrl = protocol + "://" + domain + "/";
		
		for (String link : links) {
			String internalLink = getInternalLink(protocol, domain, path, link);
			if (internalLink == null) {
				continue;
			}
			if (!internalLink.startsWith(baseUrl)) {
				continue;
			}
			internalLinks.add(internalLink);
		}
		return new ArrayList<>(internalLinks);
	}
	
	static String getInternalLink(String protocol, String domain, String path, String link) {
		if (link == null || "".equals(link)) {
			return null;
		}
		
		link = link.toLowerCase();

		if (link.startsWith("javascript")) {
			return null;
		}
		if (link.startsWith("#")) {
			return null;
		}
		if (link.startsWith("mailto:")) {
			return null;
		}
		if (link.startsWith("tel:")) {
			return null;
		}
		
		if (link.contains("#")) {
			link = link.substring(0, link.indexOf("#"));
		}
		
		if (link.toLowerCase().endsWith(".jpg")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".jpeg")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".gif")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".png")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".pdf")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".css")) {
			return null;
		}
		if (link.toLowerCase().endsWith(".js")) {
			return null;
		}

		if (link.toLowerCase().startsWith(".")) {
			return null;
		}

		if (link.contains(";")) {
			int from = link.indexOf(";");
			int to = link.indexOf("?", from);
			if (to >= 0) {
				link = link.substring(0, from) + link.substring(to);
			}
		}
		
		if (link.startsWith(protocol + "://" + domain)) {
			return link;
		}
		if (link.startsWith("/")) {
			return protocol + "://" + domain + link;
		}
		if (!link.startsWith("http")) {
			return protocol + "://" + domain + path + link;
		}
		
		return null;
	}

}
