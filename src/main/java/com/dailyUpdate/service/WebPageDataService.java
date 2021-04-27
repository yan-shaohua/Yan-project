package com.dailyUpdate.service;

import com.alibaba.fastjson.JSON;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.dailyUpdate.pojo.DataParse;
import com.dailyUpdate.utils.HtmlParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class WebPageDataService {
    private static final Logger logger = LogManager.getLogger(WebPageDataService.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${init.name}")
    private String name;

    private final String SEARCH_HIGHLIGHT = "searchHighlight";


    /**
     * 把数据添加到ES中
     */
    public Boolean parseAdd(String keywords) throws Exception {
        List<DataParse> dataParseList = new HtmlParseUtil().parse(keywords);

        //批量添加数据
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < dataParseList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jd_data")
                            .source(JSON.toJSONString(dataParseList.get(i)), XContentType.JSON));
        }
        logger.debug("抓取数据存库完成: " + dataParseList);
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }


    public List<Map<String ,Object>> searchPage(String keyword,int pageNo,int pageSize) throws IOException {
        if(pageNo <= 1){
            pageNo = 1;
        }
        SearchRequest searchRequest = new SearchRequest("jd_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
        MatchQueryBuilder title = QueryBuilders.matchQuery("title", keyword);
        sourceBuilder.query(title);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String,Object>> mapArrayList = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            mapArrayList.add(documentFields.getSourceAsMap());
        }

        return mapArrayList;
    }


    // @CustomizedRedisCache(cacheName = "模块名", key = " '项目名:模块名:方法名:' + #参数1 +‘-’+ #参数2")
    @Cached(name = SEARCH_HIGHLIGHT,
            expire = 300,
            cacheType = CacheType.LOCAL,
            key = "args[0] + args[1] + args[2] ")
    public List<Map<String ,Object>> searchHighlight(String keyword,int pageNo,int pageSize) throws Exception {
        if(pageNo <= 1){
            pageNo = 1;
        }
        SearchRequest searchRequest = new SearchRequest("jd_data");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //关键字高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        //精准匹配
        TermQueryBuilder title = QueryBuilders.termQuery("title", keyword);
        sourceBuilder.query(title);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayList<Map<String,Object>> mapArrayList = null;
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        //判断是否查询到数据，查询不到先执行parseAdd方法后再执行searchHighlight方法
        if(searchHits.length == 0){
            Boolean isTrue = parseAdd(keyword);
            if(isTrue.equals(true)){
                searchHighlight(keyword,pageNo,pageSize);
            }

        }else{
            //解析结果
            mapArrayList = new ArrayList<>();
            for (SearchHit hit : searchResponse.getHits().getHits()) {

                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField titles = highlightFields.get("title");
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();  //没高亮前的title

                if(titles != null){
                    Text[] fragments = titles.fragments();
                    String new_title = "";
                    for (Text fragment : fragments) {
                        new_title += fragment;
                    }
                    sourceAsMap.put("title",new_title);
                }
                mapArrayList.add(sourceAsMap);
            }

        }
        return mapArrayList;
    }






}
