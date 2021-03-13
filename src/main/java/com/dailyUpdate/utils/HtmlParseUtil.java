package com.dailyUpdate.utils;

import com.dailyUpdate.pojo.DataParse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取网页数据
 */
@Component
public class HtmlParseUtil {
    //main方法测试
    /*public static void main(String[] args) throws Exception {
        new HtmlParseUtil().parse("java").forEach(System.out::println);
    }*/

    public List<DataParse> parse(String keyWords) throws Exception {
        //获取请求
        String url = "https://search.jd.com/Search?keyword=" + keyWords +"&enc=utf-8";
        //解析网页
        Document document = Jsoup.parse(new URL(url),3000);
        //根据元素ID
        Element elementById = document.getElementById("J_goodsList");
        //System.out.println(elementById.html());
        //获取li标签
        Elements elements = elementById.getElementsByTag("li");

        ArrayList<DataParse> dataParses = new ArrayList<>();
        for (Element element : elements) {
            String img = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = element.getElementsByClass("p-price").eq(0).text();
            String title = element.getElementsByClass("p-name").eq(0).text();

            DataParse dataParse = new DataParse();
            dataParse.setImg(img);
            dataParse.setPrice(price);
            dataParse.setTitle(title);

            dataParses.add(dataParse);
        }
        return dataParses;
    }


}
