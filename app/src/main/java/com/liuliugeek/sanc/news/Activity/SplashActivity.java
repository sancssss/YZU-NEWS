package com.liuliugeek.sanc.news.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.Parse.ParseSpecialListDom;
import com.liuliugeek.sanc.news.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SplashActivity extends AppCompatActivity {
    private final static int UPDATE_INDEX_DATA = 0;
    private static final int SHOW_ERROR_NETWORK = 1;
    private MyHttp myHttp;
    private int indexDataId;
    private NewsDbManager manager;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_ERROR_NETWORK:
                    Snackbar.make(getWindow().getDecorView(), "网络错误！", Snackbar.LENGTH_SHORT).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        manager = new NewsDbManager(SplashActivity.this);
        //首页数据TYPEID= 37748
        indexDataId = 37746;
        handler.postDelayed(new SplashHandler(), 2000);
        if (! manager.isRefresh(indexDataId) ||  manager.isListEmpty(indexDataId)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (null != MyHttp.getActiveNetwork(SplashActivity.this)) {
                        myHttp = new MyHttp("http://www.yzu.edu.cn/module/jslib/jquery/jpage/dataproxy.jsp?startrecord=1&endrecord=60&perpage=20","UTF-8");
                        myHttp.setPostVal("col=1&appid=1&webid=100&path=%2F&columnid=" +indexDataId + "&sourceContentType=1&unitid=55987&webname=%E6%89%AC%E5%B7%9E%E5%A4%A7%E5%AD%A6&permissiontype=0");
                        myHttp.startSpecialCon();
                        String tempHtmlText = myHttp.getResult();
                        ParseSpecialListDom parseSpecialListDom = new ParseSpecialListDom(tempHtmlText);
                        List<String> tempTitleList = parseSpecialListDom.getTitleList();
                        List<String> tempUrlList = parseSpecialListDom.getUrlList();
                        List<String> tempTimeList = parseSpecialListDom.getTimeList();
                        List<Data> datas = new ArrayList<>();
                        //Log.v("count", String.valueOf(parseListDom.getTitleList().size()));
                        for(int i = 0; i<60; i++){
                            //Log.v("getUrl",parseListDom.getUrlList().get(i));
                            //此处content放的是页面地址
                            //Data data = new Data("("+parseListDom.getTimeList().get(i).substring(5) + ")" + parseListDom.getTitleList().get(i),parseListDom.getUrlList().get(i));
                            Data data = new Data();
                            data.setNewTitle(tempTitleList.get(i));
                            data.setNewContent(tempUrlList.get(i));
                            data.setNewUrl(tempUrlList.get(i));
                            data.setNewTypeid(indexDataId);
                            data.setNewArcid(Integer.valueOf(data.getNewContent().replaceAll(".*[^\\d](?=(\\d+))", "").replace(".html", "")));
                            data.setNewDate(tempTimeList.get(i));
                            // Log.v("id", String.valueOf(data.getNewArcid()));
                            datas.add(data);
                        }
                        manager.deleteContent(indexDataId);
                        //将数据存进本地数据库
                        manager.add((ArrayList<Data>) datas);
                        manager.setBoardRefreshDate(indexDataId, getDate());
                    } else {
                        Message message = new Message();
                        message.what = SHOW_ERROR_NETWORK;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }
    }


    class SplashHandler implements Runnable{

        @Override
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));
            SplashActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }
}
