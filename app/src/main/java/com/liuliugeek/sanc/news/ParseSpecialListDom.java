package com.liuliugeek.sanc.news;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 73732 on 2016/8/24.
 */
public class ParseSpecialListDom {
    private String htmldata;
    private List<String> titleList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();

    ParseSpecialListDom(String htmldata){
        this.htmldata = htmldata;
    }

    public List<String> getTitleList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.getElementById("recordset");
        //Log.v("coo", String.valueOf(content.isEmpty()));
        Elements titles = content.getElementsByAttribute("title");
        //Log.v("coo", titles.html());
        for (Element title : titles){
            // Log.v("coo", title.text());
            String titleText = "";
           titleText = title.attr("href");
            this.titleList.add(titleText);
        }
        return titleList;
    }

    public List<String> getTimeList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.getElementById("recordset");
        // Log.v("coc",content.html());
        Elements times = content.getElementsByTag("span");
        //Log.v("coc",times.html());
        for (Element time :times){
            String timeText = "";
            timeText = time.text();
            this.timeList.add(timeText);
        }
        return timeList;
    }

    public List<String> getUrlList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.getElementById("recordset");
        //Log.v("coo", String.valueOf(content.isEmpty()));
        Elements ids = content.getElementsByAttribute("href");
        for (Element id : ids){
            //Log.v("href", ids.attr("href"));
            String urlText = "";
            urlText = id.attr("href");
            urlText = "http://www.yzu.edu.cn/".concat(urlText);
            //Log.v("url_text",urlText);
            this.urlList.add(urlText);
        }
        return urlList;
    }

}
