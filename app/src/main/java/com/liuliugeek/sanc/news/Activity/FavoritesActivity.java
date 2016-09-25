package com.liuliugeek.sanc.news.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());
        findView();
        dbManager = new NewsDbManager(FavoritesActivity.this);
        datas = dbManager.queryFavorites();
        toolbar.setTitle("我的收藏");
        toolbar.setNavigationIcon(R.drawable.ic_favorite_white_48dp);
        toolbar.setSubtitle("共有 " + datas.size() + "条");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesActivity.this.finish();
            }
        });
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



}
