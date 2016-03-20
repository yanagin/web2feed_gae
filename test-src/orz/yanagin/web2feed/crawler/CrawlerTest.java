package orz.yanagin.web2feed.crawler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import orz.yanagin.web2feed.datastore.TestBase;
import orz.yanagin.web2feed.queue.Queue;
import orz.yanagin.web2feed.resource.Resource;
import orz.yanagin.web2feed.resource.ResourceDao;

public class CrawlerTest extends TestBase {

	@Test
	public void testExists() {
		ResourceDao resourceDao = new ResourceDao();
		
		Resource resource = new Resource();
		resource.setUrl("http://abc.com/123.html");
		resource.setDate("2016/1/1");
		resource.setBody(new Text("<html><body>abc</body></html>"));
		resource.setHashCode(resource.getBody().getValue().hashCode());
		
		assertThat(new Crawler().isNotUpdated(resource, resourceDao), is(false));
		
		Resource resource2 = new Resource();
		resource2.setUrl("http://abc.com/123.html");
		resource2.setDate("2016/1/1");
		resource2.setBody(new Text("<html><body>abc</body></html>"));
		resource2.setHashCode(resource2.getBody().getValue().hashCode());
		resourceDao.regist(resource2);
		assertThat(new Crawler().isNotUpdated(resource, resourceDao), is(true));

		Resource resource3 = new Resource();
		resource3.setUrl("http://abc.com/123.html");
		resource3.setDate("2016/1/2");
		resource3.setBody(new Text("<html><body>abc</body></html>"));
		resource3.setHashCode(resource3.getBody().getValue().hashCode());
		resourceDao.regist(resource3);
		assertThat(new Crawler().isNotUpdated(resource, resourceDao), is(false));

		Resource resource4 = new Resource();
		resource4.setUrl("http://abc.com/123.html");
		resource4.setDate("2016/1/1");
		resource4.setBody(new Text("<html><body>ABC</body></html>"));
		resource4.setHashCode(resource4.getBody().getValue().hashCode());
		resourceDao.regist(resource4);
		assertThat(new Crawler().isNotUpdated(resource, resourceDao), is(false));
	}
	
	private static final String PATTERN_RFC2822 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	
	@Test
	public void test() throws IOException, ParseException {
		Response response = Jsoup
				.connect("https://www.google.co.jp/services/?fg=1")
				.timeout(3000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36")
				.header("If-Modified-Since", new SimpleDateFormat(PATTERN_RFC2822).format(new SimpleDateFormat("yyyy/MM/dd").parse("2016/01/01")))
				.execute();
		assertThat(response.statusCode(), is(304));
		String lastModified = response.header("Last-Modified");
		assertThat(lastModified != null, is(true));
		
		response = Jsoup
				.connect("https://www.google.co.jp/services/?fg=1")
				.timeout(3000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36")
				.execute();
		assertThat(response.statusCode(), is(200));
	}

	@Test
	public void testNotModifired() throws IOException, ParseException {
		Queue queue = new Queue();
		queue.setUrl("https://www.google.co.jp/services/?fg=1");
		queue.setUpdatedAt(new SimpleDateFormat("yyyy/MM/dd").parse("2016/01/01"));
		new Crawler() {
			boolean isModified(Response response, java.util.Date updatedAt) {
				boolean modified = super.isModified(response, updatedAt);
				assertThat(modified, is(false));
				return modified;
			};
		}.crawl(queue);
	}

	@Test
	public void testModifired() throws IOException, ParseException {
		Queue queue = new Queue();
		queue.setUrl("https://www.google.co.jp/services/?fg=1");
		queue.setUpdatedAt(new SimpleDateFormat("yyyy/MM/dd").parse("2014/01/01"));
		new Crawler() {
			boolean isModified(Response response, java.util.Date updatedAt) {
				boolean modified = super.isModified(response, updatedAt);
				assertThat(modified, is(true));
				return modified;
			};
		}.crawl(queue);
		
		queue.setUrl("https://www.google.com/doodles");
		queue.setUpdatedAt(new SimpleDateFormat("yyyy/MM/dd").parse("2014/01/01"));
		new Crawler() {
			boolean isModified(Response response, java.util.Date updatedAt) {
				boolean modified = super.isModified(response, updatedAt);
				assertThat(modified, is(true));
				return modified;
			};
		}.crawl(queue);
	}

	@Test
	public void test_formatLastModified() {
		Date date = new Crawler().formatLastModified("Tue, 15 Sep 2015 17:05:35 GMT");
		assertNotNull(date);
	}
	
	@Test
	public void test_crawl_lawson() throws IOException {
		Queue queue = new Queue();
		queue.setUrl("http://www.lawson.co.jp/recommend/");
		queue.setUpdatedAt(new Date());
		new Crawler().crawl(queue);
	}
	
	@Test
	public void test_crawl_familymart() throws IOException {
		Queue queue = new Queue();
		queue.setUrl("http://www.family.co.jp/goods/thisweek/kanto_01.html");
		queue.setUpdatedAt(new Date());
		new Crawler().crawl(queue);
	}
	
}
