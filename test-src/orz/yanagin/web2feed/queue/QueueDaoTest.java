package orz.yanagin.web2feed.queue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import orz.yanagin.web2feed.datastore.TestBase;

public class QueueDaoTest extends TestBase {

	@Test
	public void test_memcache無し() {
		QueueDao target = new QueueDao() {
			@Override
			protected boolean isEnableMemcache() {
				return false;
			}
		};
		test(target);
		test_exists(target);
		test_regist(target);
	}

	@Test
	public void test_memcache有り() {
		QueueDao target = new QueueDao() {
			@Override
			protected boolean isEnableMemcache() {
				return true;
			}
		};
		test(target);
		test_exists(target);
		test_regist(target);
	}
	
	void test(QueueDao target) {
		Queue queue = new Queue();
		queue.setUrl("http://test");
		
		Queue result = target.getLatestQueue();
		assertThat(result == null, is(true));
		
		List<Queue> list = target.getQueues(10);
		assertThat(list.isEmpty(), is(true));
		
		target.regist(queue.getUrl());
		
		result = target.getLatestQueue();
		assertThat(result == null, is(false));
		assertThat(result.getUrl(), is("http://test"));

		list = target.getQueues(10);
		assertThat(list.isEmpty(), is(false));
		assertThat(list.size(), is(1));
		assertThat(list.get(0).getUrl(), is("http://test"));

		queue.setUpdatedAt(new Date());
	
		Date updatedAt = target.getQueues(1).get(0).getUpdatedAt();
		target.updateUpdatedAt(queue.getUrl());
		assertThat(target.getQueues(1).get(0).getUpdatedAt().equals(updatedAt), is(false));
	}
	
	public void test_exists(QueueDao dao) {
		Queue queue = new Queue();
		queue.setUrl("http://test_exists");
		
		assertThat(dao.exists(queue.getUrl()), is(false));
		
		dao.regist(queue.getUrl());
		
		assertThat(dao.exists(queue.getUrl()), is(true));
	}
	
	public void test_regist(QueueDao dao) {
		assertThat(dao.exists("http://test_regist1"), is(false));
		assertThat(dao.exists("http://test_regist2"), is(false));
		assertThat(dao.exists("http://test_regist3"), is(false));
		assertThat(dao.exists("http://test_regist4"), is(false));
		
		dao.regist(Arrays.asList("http://test_regist1", "http://test_regist2", "http://test_regist4"));

		assertThat(dao.exists("http://test_regist1"), is(true));
		assertThat(dao.exists("http://test_regist2"), is(true));
		assertThat(dao.exists("http://test_regist3"), is(false));
		assertThat(dao.exists("http://test_regist4"), is(true));
	}
	
	@Test
	public void test_remove() {
		QueueDao dao = new QueueDao();
		
		dao.regist(Arrays.asList(
				"http://test_regist1", 
				"http://test_regist2", 
				"http://test_regist3", 
				"http://test_regist4", 
				"http://test2_regist5", 
				"http://test2_regist6"));

		assertThat(dao.exists("http://test_regist1"), is(true));
		assertThat(dao.exists("http://test_regist2"), is(true));
		assertThat(dao.exists("http://test_regist3"), is(true));
		assertThat(dao.exists("http://test_regist4"), is(true));
		assertThat(dao.exists("http://test2_regist5"), is(true));
		assertThat(dao.exists("http://test2_regist6"), is(true));
		
		int count = dao.remove("http://test_");
		assertThat(count, is(4));

		assertThat(dao.exists("http://test_regist1"), is(false));
		assertThat(dao.exists("http://test_regist2"), is(false));
		assertThat(dao.exists("http://test_regist3"), is(false));
		assertThat(dao.exists("http://test_regist4"), is(false));
		assertThat(dao.exists("http://test2_regist5"), is(true));
		assertThat(dao.exists("http://test2_regist6"), is(true));

		dao.regist(Arrays.asList(
				"http://test_regist1", 
				"http://test_regist2", 
				"http://test_regist3", 
				"http://test_regist4", 
				"http://test2_regist5", 
				"http://test2_regist6"));
		
		count = dao.remove("http://test2");
		assertThat(count, is(2));

		assertThat(dao.exists("http://test_regist1"), is(true));
		assertThat(dao.exists("http://test_regist2"), is(true));
		assertThat(dao.exists("http://test_regist3"), is(true));
		assertThat(dao.exists("http://test_regist4"), is(true));
		assertThat(dao.exists("http://test2_regist5"), is(false));
		assertThat(dao.exists("http://test2_regist6"), is(false));

		dao.regist(Arrays.asList(
				"http://test_regist1", 
				"http://test_regist2", 
				"http://test_regist3", 
				"http://test_regist4", 
				"http://test2_regist5", 
				"http://test2_regist6"));
		
		count = dao.remove("http://test");
		assertThat(count, is(6));

		assertThat(dao.exists("http://test_regist1"), is(false));
		assertThat(dao.exists("http://test_regist2"), is(false));
		assertThat(dao.exists("http://test_regist3"), is(false));
		assertThat(dao.exists("http://test_regist4"), is(false));
		assertThat(dao.exists("http://test2_regist5"), is(false));
		assertThat(dao.exists("http://test2_regist6"), is(false));

		dao.regist(Arrays.asList(
				"http://test_regist1", 
				"http://test_regist2", 
				"http://test_regist3", 
				"http://test_regist4", 
				"http://test2_regist5", 
				"http://test2_regist6"));
		
		count = dao.remove("http://test3");
		assertThat(count, is(0));
	}
	
	@Test
	public void test_remove2() {
		QueueDao dao = new QueueDao();
		
		dao.regist(Arrays.asList(
				"http://www.lawson.co.jp/recommend/", 
				"http://www.lawson.co.jp/recommend/allergy/", 
				"http://www.lawson.co.jp/recommend/bentocake16s/", 
				"http://www.lawson.co.jp/recommend/allergy/a", 
				"http://www.lawson.co.jp/recommend/allergy/b", 
				"http://www.lawson.co.jp/recommend/allergy/c"));
		
		int count = dao.remove("http://www.lawson.co.jp/recommend/allergy/");
		assertThat(count, is(4));

		assertThat(dao.exists("http://www.lawson.co.jp/recommend/"), is(true));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/allergy/"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/bentocake16s/"), is(true));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/allergy/a"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/allergy/b"), is(false));
		assertThat(dao.exists("http://www.lawson.co.jp/recommend/allergy/c"), is(false));
	}
	
}
