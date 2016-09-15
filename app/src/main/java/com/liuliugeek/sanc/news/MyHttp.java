package com.liuliugeek.sanc.news;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    private String postVal;

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

    //带有post数据的CONN
    public void startSpecialCon(){
        HttpURLConnection conn = null;
        try {
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Accept", "text/xml;charset=UTF-8");
            conn.connect();
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(this.postVal);
            out.flush();
            InputStream in = conn.getInputStream();
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
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    public void setUrl(String url){
        this.url = url;
    }

    public void setPostVal(String val){
        this.postVal = val;
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
