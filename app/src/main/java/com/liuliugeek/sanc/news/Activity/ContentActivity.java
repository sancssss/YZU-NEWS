package com.liuliugeek.sanc.news.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContentActivity extends AppCompatActivity {
    static Integer THREAD_COUNT = 0;
    private final static int UPDATE_CONTENT = 0;
    private final static int SHOW_ERROR_NETWORK = 2;
    private TextView newsContent,newsTitle,newsDate;
    private Toolbar toolbar;
    private String picName;
    private NetWorkImageGetter imageGetter;
    private int screenWidth;
    private int screenHeight;
    private ExecutorService fixedThreadPool;
    private Bundle bundle;
    private Intent intent;
    private String content;
    private AsyncLoadNetWorkPic netWorkPic;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_CONTENT:
                    Log.v("Handler", "handler");
                    Log.v("poolThreadcount", String.valueOf(THREAD_COUNT));
                    if (THREAD_COUNT == 0) {
                        Log.v("refreshView","now refreshView");
                        newsContent.setText(Html.fromHtml(content, imageGetter, null));
                    }
                    break;
                case SHOW_ERROR_NETWORK:
                    Toast.makeText(ContentActivity.this, "网络错误无法加载网络图片！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_content);
            intent = getIntent();
            bundle = intent.getExtras();
            File dirFile = new File((Environment.getExternalStorageDirectory()) + "/yzunew_pic/");
            if (!dirFile.exists())
                dirFile.mkdir();
            newsContent = (TextView) findViewById(R.id.news_content);
            newsDate = (TextView) findViewById(R.id.news_content_date);
            newsTitle = (TextView) findViewById(R.id.news_content_title);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(bundle.getString("title"));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
            Log.v("size", String.valueOf(screenHeight) + "+" + screenWidth);
            content = bundle.getString("content");
            netWorkPic = new AsyncLoadNetWorkPic();
            imageGetter = new NetWorkImageGetter();
            newsTitle.setText(bundle.getString("title"));
            newsDate.setText("时间：" + bundle.getString("date"));
            newsContent.setText(Html.fromHtml(content, imageGetter, null));
            //Log.v("spannd",spanned.toString());
            //获取bd对象
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_content, menu);
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
                shareMsg(ContentActivity.this, bundle.getString("title"), bundle.getString("title"), bundle.getString("title") + bundle.getString("url") + "（来自扬大新闻客户端）", "");
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    private class NetWorkImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            picName = source.substring(11);
            Log.v("source", source);
            Log.v("picName", picName);
            //Log.v("sourceName",source);
            //Log.v("path", String.valueOf(Environment.getExternalStorageDirectory())+"/yzunew_pic/"+source.substring(11));
            File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/yzunew_pic/", source.substring(11));
            //file.mkdir();
            Log.v("file.getAbsolutePath()", file.getAbsolutePath());
            if (file.exists()) {
                drawable = Drawable.createFromPath(file.getAbsolutePath());
                //2k 1080p屏幕适配
                if(screenHeight > 1600 && screenWidth > 900){
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 4, drawable.getIntrinsicHeight() * 4);
                }else{
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 10, drawable.getIntrinsicHeight() * 10);
                }
            } else {
                Log.v("file_not_exists", "file_not_exists");
                if(MyHttp.getActiveNetwork(ContentActivity.this) == null){
                    Message message = new Message();
                    message.what = SHOW_ERROR_NETWORK;
                    handler.sendMessage(message);
                }else{
                    fixedThreadPool = Executors.newCachedThreadPool();
                    //http://www.yzu.edu.cn/picture/0/1609101755022806394.jpg,http://news.yzu.edu.cn/uploadfile/20160905100808136.jpg
                    String imgUrl = null;
                    Log.v("content_arc_typeid", String.valueOf(bundle.getInt("typeid")));
                    if(bundle.getInt("typeid") < 100){
                        imgUrl = "http://news.yzu.edu.cn/uploadfile/" + picName;
                    }else{
                        imgUrl = "http://www.yzu.edu.cn/picture/0/"+picName;
                    }
                    netWorkPic.setUrl(imgUrl);
                    fixedThreadPool.execute(netWorkPic);
                   /* netWorkPic.setUrl(imgUrl);
                    Thread loadpic = new Thread(netWorkPic);
                    loadpic.start();
                    // Log.v("activeCountPool", String.valueOf( String.valueOf(((ThreadPoolExecutor) fixedThreadPool).getActiveCount())));
                   /* try {
                    loadpic.join();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                    }*/
                }
            }
            return drawable;
        }
    }
    private final class AsyncLoadNetWorkPic implements  Runnable{
        private String url;

        public void setUrl(String... params) {
            this.url = params[0];
        }

        //新线程下载图片保存
        public void run() {
            //
            // Log.v("loadpic thread","load pic thread");
            //再创建一个线程以后台下载
           /* new Thread(new Runnable() {
                @Override
                public void run() {*/
            synchronized (THREAD_COUNT) {
                THREAD_COUNT++;
            }
                    String path = AsyncLoadNetWorkPic.this.url;
                    File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/yzunew_pic/", picName);
                    InputStream in = null;
                    FileOutputStream out = null;
                    try {
                        URL url = new URL(path);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setConnectTimeout(20000);
                        con.setRequestMethod("GET");
                        if (con.getResponseCode() == 200) {
                            in = con.getInputStream();
                            out = new FileOutputStream(file);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                            }
                            synchronized (THREAD_COUNT){
                                THREAD_COUNT--;
                            }


                            Message message = new Message();
                            message.what = UPDATE_CONTENT;
                            handler.sendMessage(message);
                        } else {
                            //Log.i(TAG, connUrl.getResponseCode() + "");
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                /*}
            }).start();*/
        }
    }


    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }
}
