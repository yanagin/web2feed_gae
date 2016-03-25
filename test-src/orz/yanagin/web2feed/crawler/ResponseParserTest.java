package orz.yanagin.web2feed.crawler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.junit.Test;

public class ResponseParserTest {

	@Test
	public void testGetInternalLinks() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com/index.html", 
				"http://abc.com/index.html", 
				Arrays.asList(
						"index1.html", 
						"/index2.html",
						"http://abc.com/index3.html",
						"https://abc.com/index4.html",
						"http://abc.jp/index5.html",
						"javascript:alert(0);",
						"#main",
						"style.css",
						"script.js",
						"mailto:xxx@abc.com",
						"tel:09000000000",
						"/image/img.jpg",
						"img2.jpeg",
						"img3.gif",
						"img4.png",
						"data.pdf",
						"servlet/123",
						"/do/index1",
						"/index2.do",
						"/index1.html",
						"/index1.html#main",
						"./index3.html",
						null));

		assertThat(links.size(), is(6));
		assertThat(links.contains("http://abc.com/index1.html"), is(true));
		assertThat(links.contains("http://abc.com/index2.html"), is(true));
		assertThat(links.contains("http://abc.com/index3.html"), is(true));
		assertThat(links.contains("http://abc.com/servlet/123"), is(true));
		assertThat(links.contains("http://abc.com/do/index1"), is(true));
		assertThat(links.contains("http://abc.com/index2.do"), is(true));
	}

	@Test
	public void testGetInternalLinks2() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com/path/index.html", 
				"http://abc.com/path/index.html", 
				Arrays.asList(
						"index1.html", 
						"/index2.html",
						"http://abc.com/path/index3.html",
						"http://abc.com/index4.html",
						"http://abc.com/path2/index5.html",
						"https://abc.com/path/index6.html",
						"/path/index7.html",
						null));

		assertThat(links.size(), is(6));
		assertThat(links.contains("http://abc.com/path/index1.html"), is(true));
		assertThat(links.contains("http://abc.com/index2.html"), is(true));
		assertThat(links.contains("http://abc.com/path/index3.html"), is(true));
		assertThat(links.contains("http://abc.com/index4.html"), is(true));
		assertThat(links.contains("http://abc.com/path2/index5.html"), is(true));
		assertThat(links.contains("http://abc.com/path/index7.html"), is(true));
	}

	@Test
	public void testGetInternalLinks3() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com/path/", 
				"http://abc.com/path/", 
				Arrays.asList(
						"index1.html", 
						"/index2.html",
						"http://abc.com/path/index3.html",
						"http://abc.com/index4.html",
						"http://abc.com/path2/index5.html",
						"https://abc.com/path/index6.html",
						null));

		assertThat(links.size(), is(5));
		assertThat(links.contains("http://abc.com/path/index1.html"), is(true));
		assertThat(links.contains("http://abc.com/index2.html"), is(true));
		assertThat(links.contains("http://abc.com/path/index3.html"), is(true));
		assertThat(links.contains("http://abc.com/index4.html"), is(true));
		assertThat(links.contains("http://abc.com/path2/index5.html"), is(true));
	}

	@Test
	public void testGetInternalLinks4() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com", 
				"http://abc.com", 
				Arrays.asList(
						"index1.html", 
						"/index2.html",
						"http://abc.com/path/index3.html",
						"http://abc.com/index4.html",
						"http://abc.com/path2/index5.html",
						"https://abc.com/path/index6.html",
						null));

		assertThat(links.size(), is(5));
		assertThat(links.contains("http://abc.com/index1.html"), is(true));
		assertThat(links.contains("http://abc.com/index2.html"), is(true));
		assertThat(links.contains("http://abc.com/path/index3.html"), is(true));
		assertThat(links.contains("http://abc.com/index4.html"), is(true));
		assertThat(links.contains("http://abc.com/path2/index5.html"), is(true));
	}

	@Test
	public void testGetInternalLinks5() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com/", 
				"http://abc.com/", 
				Arrays.asList(
						"index1.html", 
						"/index2.html",
						"http://abc.com/path/index3.html",
						"http://abc.com/index4.html",
						"http://abc.com/path2/index5.html",
						"https://abc.com/path/index6.html",
						null));

		assertThat(links.size(), is(5));
		assertThat(links.contains("http://abc.com/index1.html"), is(true));
		assertThat(links.contains("http://abc.com/index2.html"), is(true));
		assertThat(links.contains("http://abc.com/path/index3.html"), is(true));
		assertThat(links.contains("http://abc.com/index4.html"), is(true));
		assertThat(links.contains("http://abc.com/path2/index5.html"), is(true));
	}

	@Test
	public void testGetInternalLinks6() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://abc.com/", 
				"http://abc.com/", 
				Arrays.asList(
						"index1.html;jsessionid=069d9dfb52c60eeef0b215e60f22e1cd.scswaf04?category=1&area_data_id=6", 
						"/product/index.html/;jsessionid=0cd617053c7e7157f597b63284bf3abf.scswaf04?category=1&area_data_id=3",
						null));

		assertThat(links.size(), is(2));
		assertThat(links.contains("http://abc.com/index1.html?category=1&area_data_id=6"), is(true));
		assertThat(links.contains("http://abc.com/product/index.html/?category=1&area_data_id=3"), is(true));
	}

	@Test
	public void testGetInternalLinks7() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://fc.lawson.co.jp", 
				"http://fc.lawson.co.jp", 
				Arrays.asList(
						"affiliate/index1.html",
						"/affiliate/index1.html",
						"/affiliate",
						"affiliate/",
						"/affiliate/",
						null));

		assertThat(links.size(), is(3));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate/index1.html"), is(true));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate"), is(true));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate/"), is(true));
	}

	@Test
	public void testGetInternalLinks8() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://fc.lawson.co.jp/affiliate/", 
				"http://fc.lawson.co.jp/affiliate/", 
				Arrays.asList(
						"/affiliate/index1.html", 
						"/affiliate",
						"/affiliate/",
						"affiliate/",
						null));

		assertThat(links.size(), is(4));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate/index1.html"), is(true));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate"), is(true));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate/"), is(true));
		assertThat(links.contains("http://fc.lawson.co.jp/affiliate/affiliate/"), is(true));
	}

	@Test
	public void testGetInternalLinks9() {
		List<String> links = ResponseParser.getInternalLinks(
				"http://www.lawson.co.jp/company/fc/affiliate/", 
				"http://fc.lawson.co.jp/", 
				Arrays.asList(
						"/affiliate/index1.html", 
						"/affiliate",
						"/affiliate/",
						"affiliate/",
						null));

		assertThat(links.size(), is(0));
	}
	
	@Test
	public void testGetCharsetUTF8() throws IOException {
		Response response = Jsoup.connect("http://www.lawson.co.jp/recommend/")
				.timeout(15 * 1000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36")
				.execute();
		ResponseParser responseParser = new ResponseParser("http://www.lawson.co.jp/recommend/", response);
		assertThat(responseParser.getCharset(Jsoup.parse(response.body())), is("utf-8"));
	}

	@Test
	public void testGetCharsetShiftJIS() throws IOException {
		Response response = Jsoup.connect("http://www.family.co.jp/goods/aboutarea.html")
				.timeout(15 * 1000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36")
				.execute();
		ResponseParser responseParser = new ResponseParser("http://www.family.co.jp/goods/aboutarea.html", response);
		assertThat(responseParser.getCharset(Jsoup.parse(response.body())), is("shift_jis"));
	}
	
	@Test
	public void testGetBaseUrl() {
		assertThat(ResponseParser.getBaseUrl("http://abc.com/123/456/789.html"), is("http://abc.com"));
		assertThat(ResponseParser.getBaseUrl("https://abc.com/123/456/789.html"), is("https://abc.com"));
	}
	
	@Test
	public void getPath() {
		assertThat(ResponseParser.getPath("http://abc.com/123/456/789.html"), is("/123/456/"));
		assertThat(ResponseParser.getPath("https://abc.com/123/456"), is("/123/"));
		assertThat(ResponseParser.getPath("https://abc.com/"), is("/"));
		assertThat(ResponseParser.getPath("https://abc.com"), is("/"));
	}

}
