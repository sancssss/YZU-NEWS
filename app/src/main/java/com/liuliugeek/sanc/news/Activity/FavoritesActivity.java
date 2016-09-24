package com.liuliugeek.sanc.news.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        findView();
        dbManager = new NewsDbManager(FavoritesActivity.this);
        datas = dbManager.queryFavorites();
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
                intent.putExtra("favorite", datas.get(position).getIsFavorite());
                dbManager.closeDB();
                startActivity(intent);
            }
        });
    }

    public void findView(){
        listView = (ListView) findViewById(R.id.list_news);
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
