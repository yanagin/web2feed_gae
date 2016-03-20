package orz.yanagin.web2feed.resource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.appengine.api.datastore.Text;

import orz.yanagin.web2feed.datastore.TestBase;

public class ResourceDaoTest extends TestBase {

	@Test
	public void test_memcache無し() {
		ResourceDao target = new ResourceDao() {
			@Override
			protected boolean isEnableMemcache() {
				return false;
			}
		};
		test(target);
	}

	@Test
	public void test_memcache有り() {
		ResourceDao target = new ResourceDao() {
			@Override
			protected boolean isEnableMemcache() {
				return true;
			}
		};
		test(target);
	}
	
	void test(ResourceDao target) {
		Resource resource = new Resource();
		resource.setUrl("http://test");
		resource.setBody(new Text("body"));
		resource.setDate("date");
		resource.setHashCode(100);
		
		Resource result = target.getResource(resource.getUrl());
		assertThat(result == null, is(true));
		
		target.regist(resource);
		
		result = target.getResource(resource.getUrl());
		assertThat(result == null, is(false));
		assertThat(result.getUrl(), is("http://test"));
		
		result = target.getResourceByKey(resource.getKey());
		assertThat(result == null, is(false));
		assertThat(result.getUrl(), is("http://test"));
	}
	
	@Test
	public void test_getResources() throws ParseException {
		ResourceDao dao = new ResourceDao();
		
		dao.regist(create("http://www.lawson.co.jp/recommend/", new SimpleDateFormat("yyyyMMdd").parse("20160101")));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/", new SimpleDateFormat("yyyyMMdd").parse("20160102")));
		dao.regist(create("http://www.lawson.co.jp/recommend/bentocake16s/", new SimpleDateFormat("yyyyMMdd").parse("20160103")));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/a", new SimpleDateFormat("yyyyMMdd").parse("20160104")));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/b", new SimpleDateFormat("yyyyMMdd").parse("20160105")));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/c", new SimpleDateFormat("yyyyMMdd").parse("20160106")));
		
		List<Resource> resources = dao.getResources(10);
		assertThat(resources.size(), is(6));

		resources = dao.getResources("http://www.lawson.co.jp/recommend/", 10);
		assertThat(resources.size(), is(6));
		assertThat(resources.get(0).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/c"));
		assertThat(resources.get(1).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/b"));
		assertThat(resources.get(2).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/a"));
		assertThat(resources.get(3).getUrl(), is("http://www.lawson.co.jp/recommend/bentocake16s/"));
		assertThat(resources.get(4).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/"));
		assertThat(resources.get(5).getUrl(), is("http://www.lawson.co.jp/recommend/"));

		resources = dao.getResources("http://www.lawson.co.jp/recommend/gallergy/", 10);
		assertThat(resources.size(), is(4));
		assertThat(resources.get(0).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/c"));
		assertThat(resources.get(1).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/b"));
		assertThat(resources.get(2).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/a"));
		assertThat(resources.get(3).getUrl(), is("http://www.lawson.co.jp/recommend/gallergy/"));
	}
	
	@Test
	public void test_remove() {
		ResourceDao dao = new ResourceDao();
		
		dao.regist(create("http://www.lawson.co.jp/recommend/"));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/"));
		dao.regist(create("http://www.lawson.co.jp/recommend/bentocake16s/"));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/a"));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/b"));
		dao.regist(create("http://www.lawson.co.jp/recommend/gallergy/c"));
		
		int count = dao.remove("http://www.lawson.co.jp/recommend/gallergy/");
		assertThat(count, is(4));

		assertThat(dao.exists("http://www.lawson.co.jp/recommend/"), is(true));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/gallergy/"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/bentocake16s/"), is(true));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/gallergy/a"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/gallergy/b"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/gallergy/c"), is(false));
	}
	
	static Resource create(String url, Date... updatedAt) {
		Resource result = new Resource();
		result.setUrl(url);
		if (updatedAt != null) {
			result.setUpdatedAt(updatedAt[0]);
		}
		return result;
	}
	
}
