package com.liuliugeek.sanc.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by 73732 on 2016/8/25.
 */
public class MyHttp {
    private String url;
    private String unicode;
    private String result;

    MyHttp(String url, String unicode){
        this.url = url;
        this.unicode = unicode;
    }

    public void startCon(){
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setConnectTimeout(8000);
            //connection.setReadTimeout(8000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in,this.unicode));
            StringBuilder response = new StringBuilder();
            String line = new String();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            this.result = response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void setUrl(String url){
        this.url = url;
    }

    public String getResult(){
        return this.result;
    }

    /**
     * 获取是否有网络连接方法
     *
     * @param context
     * @return
     */
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


}
