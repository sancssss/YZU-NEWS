package com.liuliugeek.sanc.news.Parse;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by 73732 on 2016/9/16.
 */
public class ParseSpecialContentDom {
    private String htmldata;
    private String title;
    private String content;

    public ParseSpecialContentDom(String htmldata){
        this.htmldata = htmldata;
    }
    ParseSpecialContentDom(){}

    public void setHtmldata(String htmldata){
        this.htmldata = htmldata;
    }

    public String getContent(){
        Document document = Jsoup.parse(this.htmldata.replaceAll("&amp;", "&"));
        Log.v("document",document.html());
        Elements contents = document.select("[class=bt_content]");
        content = contents.first().html();
        Log.v("content_html", content);
        return content;
    }
}
