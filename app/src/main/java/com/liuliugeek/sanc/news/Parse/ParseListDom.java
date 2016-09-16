package com.liuliugeek.sanc.news.Parse;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 73732 on 2016/8/24.
 */
public class ParseListDom {
    private String htmldata;
    private List<String> titleList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> urlList = new ArrayList<>();

    public ParseListDom(String htmldata){
        this.htmldata = htmldata;
    }

    public List<String> getTitleList(){
        Document document = Jsoup.parse(this.htmldata);
        Elements content = document.select("[class=blacklist listPage]");
        //Log.v("coo", String.valueOf(content.isEmpty()));
        Elements titles = content.first().getElementsByAttribute("title");
        //Log.v("coo", titles.html());
        for (Element title : titles){
            // Log.v("coo", title.text());
            String titleText = "";
            titleText = title.text();
            this.titleList.add(titleText);
        }
        return titleList;
    }

    public List<String> getTimeList(){
        Document document = Jsoup.parse(this.htmldata);
        Elements content = document.select("[class=blacklist listPage]");
       // Log.v("coc",content.html());
        Elements times = content.first().getElementsByClass("newstime");
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
        Elements content = document.select("[class=blacklist listPage]");
       // Log.v("content",content.html());
        Elements ids = content.first().getElementsByTag("a");
        //Log.v("ids_html",ids.html());
        for (Element id : ids){
            //Log.v("href", ids.attr("href"));
            String urlText = "";
            urlText = id.attr("href");
            urlText = "http://news.yzu.edu.cn/".concat(urlText);
            //Log.v("url_text",urlText);
            this.urlList.add(urlText);
        }
        return urlList;
    }

}
