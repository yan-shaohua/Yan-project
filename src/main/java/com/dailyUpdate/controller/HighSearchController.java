package com.dailyUpdate.controller;

import com.alibaba.fastjson.JSON;
import com.dailyUpdate.pojo.QueryParam;
import com.dailyUpdate.service.HighSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("/highsearch")
public class HighSearchController {

    @Autowired
    private HighSearchService highSearchService;

    @GetMapping("search")
    public Object search(@RequestParam(name = "query") String data,
                         @RequestParam(value = "page_num") int pageNum,
                         @RequestParam(value = "page_size") int pageSize) throws UnsupportedEncodingException {

        //解码
        String decode = java.net.URLDecoder.decode(data, "UTF-8");
        QueryParam queryParam = JSON.parseObject(decode, QueryParam.class);

        highSearchService.getSearch(queryParam, pageNum, pageSize);


        return null;
    }


}
