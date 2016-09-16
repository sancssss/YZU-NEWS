package com.liuliugeek.sanc.news.Parse;


import android.util.Log;

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

    public ParseSpecialListDom(String htmldata){
        this.htmldata = htmldata;
    }

    public List<String> getTitleList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.select("recordset").first();
       // Log.v("coo", String.valueOf(content.html()));
        //Element t = content.select("record").first();
        Elements titles = content.getElementsByTag("a");
       // Log.v("titles_html", titles.html());
        Log.v("countParese", String.valueOf(titles.size()));
        for (Element title : titles){
             //Log.v("every_title", title.text());
            String titleText = "";
            titleText = title.attr("title");
            //Log.v("finally_title",  titleText);
            this.titleList.add(titleText);
        }
        return titleList;
    }

    public List<String> getTimeList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.select("recordset").first();
        // Log.v("coc",content.html());
        Elements times = content.getElementsByTag("span");
        // Log.v("coc",times.html());
        for (Element time :times){
            String timeText = "";
            timeText = time.text();
            this.timeList.add(timeText);
        }
        return timeList;
    }

    public List<String> getUrlList(){
        Document document = Jsoup.parse(this.htmldata);
        Element content = document.select("recordset").first();
      //  Log.v("coo", String.valueOf(content.isEmpty()));
        Elements ids = content.select("a");
        for (Element id : ids){
            //Log.v("href", ids.attr("href"));
            String urlText = "";
            urlText = id.attr("href");
            urlText = "http://www.yzu.edu.cn".concat(urlText);
           // Log.v("url_text",urlText);
            this.urlList.add(urlText);
        }
        return urlList;
    }

}
