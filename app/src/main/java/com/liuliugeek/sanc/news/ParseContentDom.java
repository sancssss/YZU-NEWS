package com.liuliugeek.sanc.news;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by 73732 on 2016/8/24.
 */
public class ParseContentDom {
    private String htmldata;
    private String title;
    private String content;

    ParseContentDom(String htmldata){
        this.htmldata = htmldata;
    }
    ParseContentDom(){}

    public void setHtmldata(String htmldata){
        this.htmldata = htmldata;
    }
    public String getTitle(){
        Document document = Jsoup.parse(this.htmldata);
        Elements titles = document.select("[class=title]");
        title = titles.first().text();
        return title;
    }

    public String getContent(){
        Document document = Jsoup.parse(this.htmldata.replaceAll("&amp;", "&"));
       // Log.v("document",document.html());
        Elements contents = document.select("[class=articleContent]");
        content = contents.first().html();
        Log.v("content_html",content);
        return content;
    }
}
