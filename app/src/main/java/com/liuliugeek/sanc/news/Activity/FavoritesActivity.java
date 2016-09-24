package com.liuliugeek.sanc.news.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.liuliugeek.sanc.news.Adapter.NewsAdapter;
import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.R;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Data> datas;
    private NewsAdapter newsAdapter;
    private NewsDbManager dbManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        findView();
        dbManager = new NewsDbManager(FavoritesActivity.this);
        datas = dbManager.queryFavorites();
        toolbar.setTitle("我的收藏");
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_48dp);
        toolbar.setSubtitle("共有 " + datas.size() + "条");
        setSupportActionBar(toolbar);
        newsAdapter = new NewsAdapter(datas, FavoritesActivity.this);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoritesActivity.this, ContentActivity.class);
                intent.putExtra("content", datas.get(position).getNewContent());
                intent.putExtra("arcid", datas.get(position).getNewArcid());
                intent.putExtra("url", datas.get(position).getNewUrl());
                intent.putExtra("title", datas.get(position).getNewTitle());
                intent.putExtra("typeid", datas.get(position).getNewTypeid());
                intent.putExtra("date", datas.get(position).getNewDate());
                intent.putExtra("favorite", 1);
                dbManager.closeDB();
                startActivity(intent);
            }
        });
    }

    public void findView(){
        listView = (ListView) findViewById(R.id.favorite_list_news);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        dbManager = new NewsDbManager(FavoritesActivity.this);
        datas = dbManager.queryFavorites();
        newsAdapter = new NewsAdapter(datas, FavoritesActivity.this);
        listView.setAdapter(newsAdapter);
        newsAdapter.notifyDataSetChanged();
        toolbar.setSubtitle("共有 " + datas.size() + "条");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
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
}
