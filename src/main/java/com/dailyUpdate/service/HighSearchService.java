package com.dailyUpdate.service;

import com.dailyUpdate.pojo.Param;
import com.dailyUpdate.pojo.QueryParam;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class HighSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Object getSearch(QueryParam queryParam, int pageNum, int pageSize){

        String INDEX_NAME = "";
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        List<List<Param>> queryLists = queryParam.getQueryList();

        for (List<Param> queryList : queryLists) {

            if(queryLists.size() > 1){

                BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
                //between：区间  gt:大于  get：大于等于  lt：小于  lte:小于等于  eq：等于
                for (int i = 0; i < queryList.size(); i++) {
                    if(queryList.get(i).getOp().equals("between")){
                        boolQueryBuilder1.should(QueryBuilders.rangeQuery(queryList.get(i).getName()).gt(queryList.get(i).getVal1()).lt(queryList.get(i).getVal2()));

                    }else if(queryList.get(i).getOp().equals("gt")){

                    }else if(queryList.get(i).getOp().equals("gt")){

                    }

                }
                searchSourceBuilder.query(boolQueryBuilder1);
            }else{

            }

        }


        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.from(pageNum);
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return null;
    }






}
