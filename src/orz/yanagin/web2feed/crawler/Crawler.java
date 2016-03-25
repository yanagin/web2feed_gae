package orz.yanagin.web2feed.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import orz.yanagin.web2feed.queue.Queue;
import orz.yanagin.web2feed.queue.QueueDao;
import orz.yanagin.web2feed.resource.Resource;
import orz.yanagin.web2feed.resource.ResourceDao;

public class Crawler {
	
	private static final String PATTERN_RFC2822 = "EEE, dd MMM yyyy HH:mm:ss z";
	
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	public int start() {
		QueueDao queueDao = new QueueDao();
		Queue queue = queueDao.getLatestQueue();
		if (queue == null) {
			logger.info("キューの登録がないため終了します。");
			return 0;
		}
		
		logger.info("キューの処理を行います。url->" + queue.getUrl());
		
		try {
			crawl(queue);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
		} finally {
			queueDao.updateUpdatedAt(queue.getUrl());
		}
		
		logger.info("キューの処理を終了します。");
		
		return 1;
	}
	
	void crawl(Queue queue) throws IOException {
		String url = queue.getUrl();
		
		try {
			logger.info("クロールを開始します。url->" + url);
			
			ResourceDao resourceDao = new ResourceDao();
			QueueDao queueDao = new QueueDao();

			Response response = getResponse(url, queue.getUpdatedAt());
			if (!isModified(response, queue.getUpdatedAt())) {
				logger.info("コンテンツが更新されていません。url->" + url + " status->" + response.statusCode());
				return;
			}
			if (response.statusCode() != 200) {
				logger.info("ステータスコードが不正です。url->" + url + " status->" + response.statusCode());
				return;
			}
			if (!response.contentType().toLowerCase().contains("text/html")) {
				logger.info("content-typeがhtmlではありません。url->" + url + " content-type->" + response.contentType());
				return;
			}
			
			ResponseParser responseParser = new ResponseParser(url, response);
			Resource resource = responseParser.getResource();
			if (isNotUpdated(resource, resourceDao)) {
				logger.info("更新されていないのでリソースの登録を行いません。url->" + url + " Date->" + resource.getDate());
				return;
			}
			
			resource.setUpdatedAt(new Date());
			resourceDao.regist(resource);
			logger.info("リソースを登録しました。url->" + url);
			
			List<String> internalLinks = responseParser.getInternalLinks();
			logger.info("外部リンクを取得しました。size->" + internalLinks.size());
			
			List<String> links = new ArrayList<>();
			for (String internalLink : internalLinks) {
				if (queueDao.exists(internalLink)) {
//					logger.info("既にキューに存在するため内部リンクを登録しませんでした。internalLink->" + internalLink);
					continue;
				}
				
				links.add(internalLink);
				logger.info("キューに内部リンクを登録しました。internalLink->" + internalLink);
			}
			queueDao.regist(links);
		} catch (MalformedURLException e) {
			logger.warning("クロールでエラーが発生しました。url->" + url + " error->" + e.getMessage());
			throw e;
		} catch (IOException e) {
			logger.warning("クロールでエラーが発生しました。url->" + url + " error->" + e.getMessage());
			throw e;
		} finally {
			logger.info("クロールを終了します。url->" + url);
		}
	}
	
	Response getResponse(String url, Date updatedAt) throws MalformedURLException, IOException {
		return Jsoup
				.connect(url)
				.timeout(15 * 1000)
				.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36")
				.header("If-Modified-Since", new SimpleDateFormat(PATTERN_RFC2822, Locale.US).format(updatedAt))
				.execute();
	}
	
	boolean isModified(Response response, Date updatedAt) {
		if (response.statusCode() == 304) {
			return false;
		}

		String lastModified = response.header("Last-Modified");
		if (lastModified == null) {
			return true;
		}
		
		if (updatedAt == null) {
			return true;
		}
		
		Date date = formatLastModified(lastModified);
		if (date != null && updatedAt.getTime() >= date.getTime()) {
			return false;
		}
		
		return true;
	}
	
	Date formatLastModified(String lastModified) {
		try {
			return new SimpleDateFormat(PATTERN_RFC2822, Locale.US).parse(lastModified);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	boolean isNotUpdated(Resource resource, ResourceDao resourceDao) {
		Resource existsResource = resourceDao.getResource(resource.getUrl());
		if (existsResource == null) {
			return false;
		}
		if (existsResource.getDate() != null
				&& !existsResource.getDate().equals(resource.getDate())) {
			return false;
		}
		if (existsResource.getHashCode() != resource.getHashCode()) {
			return false;
		}
		return true;
	}
	
}
