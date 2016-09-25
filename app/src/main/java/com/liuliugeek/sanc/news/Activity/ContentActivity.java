package com.liuliugeek.sanc.news.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ContentActivity extends AppCompatActivity {
    static int THREAD_COUNT = 0;
    private final static int UPDATE_CONTENT = 0;
    private final static int SHOW_ERROR_NETWORK = 2;
    private TextView newsContent, newsTitle, newsDate;
    private Toolbar toolbar;
    private String picName;
    private int screenWidth;
    private int screenHeight;
    private Bundle bundle;
    private Intent intent;
    private String content;
    private MenuItem menuItem = null;
    private int isFavorite;
    private int arcid;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentActivity.this.finish();
            }
        });
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.v("size", String.valueOf(screenHeight) + "+" + screenWidth);
        isFavorite = bundle.getInt("favorite");
        arcid = bundle.getInt("arcid");
        content = bundle.getString("content");
        newsTitle.setText(bundle.getString("title"));
        newsDate.setText("时间：" + bundle.getString("date"));
        URLImageParser parser = new URLImageParser(newsContent, this);
        Spanned htmlSpanned = Html.fromHtml(content, parser, null);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableDisplayPic = sharedPreferences.getBoolean("display_pic", true);
        if(enableDisplayPic){
            newsContent.setText(htmlSpanned);
        }else{
            newsContent.setText(Html.fromHtml(content));
        }
        //Log.v("spannd",spanned.toString());
        //获取bd对象
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        menuItem = menu.findItem(R.id.action_favorite);
        if(isFavorite== 1){
            menuItem.setTitle("取消收藏");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            shareMsg(ContentActivity.this, bundle.getString("title"), bundle.getString("title"), bundle.getString("title") + bundle.getString("url") + "（来自扬大新闻客户端）", "");
            return true;
        }

        if(id == R.id.action_favorite){
            NewsDbManager dbManager = new NewsDbManager(ContentActivity.this);
            //取消收藏
            if(isFavorite == 1){
                dbManager.removeFavorite(arcid);
                menuItem.setTitle("收藏");
                isFavorite = 0;
                Log.v("when delete fdata", String.valueOf(isFavorite));
                //刷新菜单
            }else{
                Data data = new Data();
                data.setNewArcid(bundle.getInt("arcid"));
                data.setNewTitle(bundle.getString("title"));
                data.setNewTypeid(bundle.getInt("typeid"));
                data.setNewContent(bundle.getString("content"));
                data.setNewUrl(bundle.getString("url"));
                data.setNewDate(bundle.getString("date"));
                dbManager.addToFavorites(data);
                isFavorite = 1;
                menuItem.setTitle("取消收藏");
                Log.v("when create fdata", String.valueOf(isFavorite));
                //刷新菜单
            }
            dbManager.closeDB();
        }

        if(id == R.id.action_open_url){
            Uri uri = Uri.parse(bundle.getString("url"));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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

    public class URLDrawable extends BitmapDrawable {
        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        protected Drawable drawable;
        protected  String imgName;

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setImgName(String imgName) {
            this.imgName = imgName;
        }
    }

    public class URLImageParser implements Html.ImageGetter {
        Context c;
        TextView container;

        /***
         * Construct the URLImageParser which will execute AsyncTask and refresh the container
         *
         * @param t
         * @param c
         */
        public URLImageParser(TextView t, Context c) {
            this.c = c;
            this.container = t;
        }

        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            picName = source.substring(11);
            URLDrawable urlDrawable = new URLDrawable();
            urlDrawable.setImgName(picName);
            File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/yzunew_pic/", picName);
            Log.v("file.getAbsolutePath()", file.getAbsolutePath());
            if (file.exists()) {
                drawable = Drawable.createFromPath(file.getAbsolutePath());
                //2k 1080p屏幕适配
                if(drawable == null){
                    return drawable;
                }
                if (screenHeight > 1600 && screenWidth > 900) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 4 , drawable.getIntrinsicHeight() * 4);
                } else {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 10 , drawable.getIntrinsicHeight() * 10);
                }
                return drawable;
            } else {
                Log.v("file_not_exists", "file_not_exists");
                if (MyHttp.getActiveNetwork(ContentActivity.this) == null) {
                    Message message = new Message();
                    message.what = SHOW_ERROR_NETWORK;
                    handler.sendMessage(message);
                } else {
                    // get the actual source
                    ImageGetterAsyncTask asyncTask =
                            new ImageGetterAsyncTask(urlDrawable);

                    String imgUrl = null;
                    Log.v("content_arc_typeid", String.valueOf(bundle.getInt("typeid")));
                    if (bundle.getInt("typeid") < 100) {
                        imgUrl = "http://news.yzu.edu.cn/uploadfile/" + picName;
                    } else {
                        imgUrl = "http://www.yzu.edu.cn/picture/0/" + picName;
                    }
                    asyncTask.execute(imgUrl);

                    // return reference to URLDrawable where I will change with actual image from
                    // the src tag
                    return urlDrawable;
                }
            }
            return null;
        }

        public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
           URLDrawable urlDrawable;

            public ImageGetterAsyncTask(URLDrawable d) {
                this.urlDrawable = d;
            }

            @Override
            protected Drawable doInBackground(String... params) {
                String source = params[0];
                return fetchDrawable(source);
            }

            @Override
            protected void onPostExecute(Drawable result) {
                // set the correct bound according to the result from HTTP call
                if (screenHeight > 1600 && screenWidth > 900) {
                   urlDrawable.setBounds(0, 0, result.getIntrinsicWidth() * 4, result.getIntrinsicHeight() * 4);
                } else {
                    urlDrawable.setBounds(0, 0, result.getIntrinsicWidth() * 10,result.getIntrinsicHeight() * 10);
                }

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();

                container.setText(container.getText());
            }

            /***
             * Get the Drawable from URL
             *
             * @param urlString
             * @return
             */
            public Drawable fetchDrawable(String urlString) {
                try {
                    File file = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/yzunew_pic/", urlDrawable.imgName);
                    InputStream is = fetch(urlString);
                    OutputStream out = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    Drawable drawable = Drawable.createFromPath(file.getAbsolutePath());
                    if (screenHeight > 1600 && screenWidth > 900) {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 4, drawable.getIntrinsicHeight() * 4);
                    } else {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 4, drawable.getIntrinsicHeight() * 4);
                    }
                    return drawable;
                } catch (Exception e) {
                    return null;
                }
            }

            private InputStream fetch(String urlString) throws MalformedURLException, IOException {
                Log.v("urlString", urlString);
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = urlConnection.getInputStream();
                return stream;
            }

        }

    }
}
