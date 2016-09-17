package com.liuliugeek.sanc.news.Activity.Fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by 73732 on 2016/8/19.
 */
public class NewContentFragemnt extends Fragment {
    static int THREAD_COUNT = 0;
    private final static int UPDATE_CONTENT = 0;
    private static final int SHOW_ERROR_NETWORK = 2;
    private TextView newsContent;
    private Button shareButton;
    private String picName;
    private NetWorkImageGetter imageGetter;
    private int screenWidth;
    private int screenHeight;
    private ExecutorService fixedThreadPool;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            synchronized (this){
                switch (msg.what){
                    case UPDATE_CONTENT:
                        Log.v("Handler", "handler");
                        Log.v("poolThreadcount", String.valueOf(((ThreadPoolExecutor)fixedThreadPool).getActiveCount()));
                        if(THREAD_COUNT == 0){
                            newsContent.setText(Html.fromHtml(getArguments().getString("content"), imageGetter, null));
                        }
                        break;
                    case SHOW_ERROR_NETWORK:
                        Toast.makeText(getActivity(), "网络错误无法加载网络图片！", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        File dirFile = new File((Environment.getExternalStorageDirectory())+"/yzunew_pic/");
        if(!dirFile.exists())
            dirFile.mkdir();
        View view = inflater.inflate(R.layout.news_content, container, false);
        shareButton = (Button) view.findViewById(R.id.share_news);
        newsContent = (TextView) view.findViewById(R.id.news_content);
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.v("size", String.valueOf(screenHeight)+"+"+screenWidth);
        imageGetter = new NetWorkImageGetter();
        // Message message = new Message();
        // message.what = UPDATE_CONTENT;
        //  message.obj = spanned;
        // handler.sendMessage(message);
        // Spanned spanned = (Spanned) msg.obj;
        //Log.v("spannd", (String) msg.obj);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        String content = getArguments().getString("content");
        newsContent.setText(Html.fromHtml(content, imageGetter, null));
        //Log.v("spannd",spanned.toString());
        //获取bd对象
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMsg(getActivity(), getArguments().getString("title"), getArguments().getString("title"),getArguments().getString("title") + getArguments().getString("url")+"（来自扬大新闻客户端）","");
            }
        });
        return view;
    }

    private final class NetWorkImageGetter implements Html.ImageGetter {

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
                        if(getActiveNetwork(getActivity()) == null){
                            Message message = new Message();
                            message.what = SHOW_ERROR_NETWORK;
                            handler.sendMessage(message);
                        }else{
                            fixedThreadPool = Executors.newFixedThreadPool(8);
                            //http://www.yzu.edu.cn/picture/0/1609101755022806394.jpg,http://news.yzu.edu.cn/uploadfile/20160905100808136.jpg
                            String imgUrl = null;
                            Log.v("content_arc_typeid", String.valueOf(getArguments().getInt("typeid")));
                            if(getArguments().getInt("typeid") < 100){
                                imgUrl = "http://news.yzu.edu.cn/uploadfile/" + picName;
                            }else{
                                imgUrl = "http://www.yzu.edu.cn/picture/0/"+picName;
                            }
                            fixedThreadPool.execute(new AsyncLoadNetWorkPic(imgUrl));
                            //AsyncLoadNetWorkPic netWorkPic = new AsyncLoadNetWorkPic("http://news.yzu.edu.cn/" + source);
                            //Thread loadpic = new Thread(netWorkPic);
                            //loadpic.start();
                           // Log.v("activeCountPool", String.valueOf( String.valueOf(((ThreadPoolExecutor) fixedThreadPool).getActiveCount())));
                           // try {
                               // loadpic.join();
                           // } catch (InterruptedException e) {
                            //    e.printStackTrace();
                           // }
                        }
                    }
                return drawable;
        }
    }
    private final class AsyncLoadNetWorkPic implements  Runnable{
        private String url;

       public AsyncLoadNetWorkPic(String... params) {
           url = params[0];
           Log.v("param", params[0]);
        }
        //新线程下载图片保存
        public void run() {
            //
           // Log.v("loadpic thread","load pic thread");
            //再创建一个线程以后台下载
            // new Thread(new Runnable() {
                // @Override
                // public void run() {
                     THREAD_COUNT++;
                     String path = AsyncLoadNetWorkPic.this.url;
                     File file = new File(String.valueOf(Environment.getExternalStorageDirectory())+"/yzunew_pic/",picName);
                     InputStream in = null;
                     FileOutputStream out = null;
                     try{
                         URL url = new URL(path);
                         HttpURLConnection con = (HttpURLConnection)url.openConnection();
                         con.setConnectTimeout(5000);
                         con.setRequestMethod("GET");
                         if(con.getResponseCode() == 200){
                             in = con.getInputStream();
                             out = new FileOutputStream(file);
                             byte[] buffer = new byte[1024];
                             int len;
                             while((len = in.read(buffer))!=-1){
                                 out.write(buffer, 0, len);
                             }
                             THREAD_COUNT--;
                             Message message = new Message();
                             message.what = UPDATE_CONTENT;
                             handler.sendMessage(message);
                         }else{
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
                     }finally {
                         if(in != null){
                             try {
                                 in.close();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }
                         if(out != null){
                             try {
                                 out.close();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }
                     }
                 }
   //          }).start();

        }
   // }
    public static NetworkInfo getActiveNetwork(Context context){
        if (context == null)
            return null;
        ConnectivityManager mConnMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnMgr == null)
            return null;
        NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo();
        // 获取活动网络连接信息
        return aActiveInfo;
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
