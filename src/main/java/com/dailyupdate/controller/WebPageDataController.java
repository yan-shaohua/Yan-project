package com.unknownproject.controller;

import com.unknownproject.service.WebPageDataService;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


@RestController
public class WebPageDataController {

    private final Logger logger = LogManager.getLogger(WebPageDataController.class);

    @Autowired
    private WebPageDataService webPageDataService;

    /**
     * 解析数据并添加到ES中
     * @param keywords
     * @return
     */
    @GetMapping("/parse/{keywords}")
    public Boolean parse(@PathVariable("keywords") String keywords) throws Exception {
        return webPageDataService.parseAdd(keywords);
    }

    /**
     * 精确查询
     */
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword
                ,@PathVariable("pageNo") int pageNo
                ,@PathVariable("pageSize") int pageSize) throws IOException {

        return webPageDataService.searchPage(keyword, pageNo, pageSize);
    }

    /**
     * 精确查询并高亮
     */
    @GetMapping("/searchHighlight/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchHighlight(@PathVariable("keyword") String keyword
            ,@PathVariable("pageNo") int pageNo
            ,@PathVariable("pageSize") int pageSize) throws Exception {

        String parseKeyWord = URLDecoder.decode(keyword, "utf-8");
        return webPageDataService.searchHighlight(parseKeyWord, pageNo, pageSize);
    }







}
