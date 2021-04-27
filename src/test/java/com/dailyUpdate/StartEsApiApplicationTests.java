package com.dailyUpdate;




import com.alibaba.fastjson.JSON;
import com.dailyUpdate.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class StartEsApiApplicationTests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	//创建索引
	@Test
	void testCreatIndex() throws IOException {
		//创建索引请求
		CreateIndexRequest request = new CreateIndexRequest("test1");
		//客户端执行请求
		CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
		System.out.println(createIndexResponse);
	}

	//测试获取索引，判断是否存在
	@Test
	void testExistIndex() throws IOException {
		GetIndexRequest request = new GetIndexRequest("test1");
		boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
		System.out.println(exists);
	}

	//删除索引

	@Test
	void testDeleteIndex() throws IOException {
		DeleteIndexRequest deleteRequest = new DeleteIndexRequest("test1");
		AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println(delete.isAcknowledged());
	}

	//添加文档
	@Test
	void testAddDocument() throws IOException {
		//创建对象
		User user = new User("张三",20);

		//创建请求
		IndexRequest indexRequest = new IndexRequest("test6");

		indexRequest.id("1");
		indexRequest.timeout("1s");

		//将我们的数据放入请求JSON
		IndexRequest source = indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

		IndexResponse indexResponse = restHighLevelClient.index(source, RequestOptions.DEFAULT);
		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status());
	}


	//获取文档 判断是否存在
	@Test
	void testIsExists() throws IOException {
		GetRequest getRequest = new GetRequest("test6", "1");
		getRequest.fetchSourceContext(new FetchSourceContext(false));
		getRequest.storedFields("_none_");
		boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
		System.out.println(exists);
	}


	//获取文档信息
	@Test
	void testGetDocument() throws IOException {
		GetRequest getRequest = new GetRequest("test6", "1");
		GetResponse documentFields = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		System.out.println(documentFields.getSourceAsString());
	}

	//更新文档信息
	@Test
	void testUpdateDocument() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest("test6", "2");

		User user = new User("闫绍华",18);
		updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
		System.out.println(updateResponse.status());
	}

	//删除文档信息
	@Test
	void testDeleteDocument() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest("test6","2");

		DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		System.out.println(deleteResponse.status());
	}

	//批量插入数据
	@Test
	void testBlukRequest() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		bulkRequest.timeout("10s");
		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("yanshaohua",15));
		userList.add(new User("yanshaohua",15));
		userList.add(new User("yanshaohua",15));
		userList.add(new User("sunwukong",15));
		userList.add(new User("sunwukong",15));
		userList.add(new User("sunwukong",15));

		//批处理请求
		for (int i = 0; i < userList.size(); i++) {
			bulkRequest.add(
					new IndexRequest("test6")
							.id("" + i+1)
							.source(JSON.toJSONString(userList.get(i)),XContentType.JSON));

		}

		BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		System.out.println(bulkResponse.hasFailures());
	}


	@Test
	void testSearch() throws IOException {

		SearchRequest searchRequest = new SearchRequest("test6");

		//构建搜索条件
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		//查询条件
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "yanshaohua");   //精确查询
		//MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();    //查询全部
		searchSourceBuilder.query(termQueryBuilder);

		searchRequest.source(searchSourceBuilder);
		SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		System.out.println(JSON.toJSONString(search.getHits()));
		System.out.println("=====================================================");
		for (SearchHit doucmentFields : search.getHits().getHits()) {
			System.out.println(doucmentFields.getSourceAsMap());
		}
	}




}
