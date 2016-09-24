package com.liuliugeek.sanc.news.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.Model.DrawerListData;
import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.Parse.ParseListDom;
import com.liuliugeek.sanc.news.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int SHOW_LIST = 0;
    private static final int SHOW_ERROR_NETWORK = 1;
    //private static final int SHOW_CONTENT = 1;

    private Context mContext;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    private AnimationDrawable animationDrawable;

    private Button openMenuBtn;
    private Button refreshBtn;

    private ArrayList<Data> datas = null;

    private FragmentManager fragmentManager = null;
    private long exitTime = 0;

    private MyHttp myHttp;
    private ParseListDom parseListDom;

    private NewsDbManager dbManager;
    //当前标题名称
    public int nowtypeid;
    public int nowtitleid;

    private ViewPager pager;
    private TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    private NavigationView navigationView;


    private ArrayList<DrawerListData> drawerListDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blindView();
        init();
        setOnC();

    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount() == 0){
            if((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                super.onBackPressed();
            }
        }else{
            fragmentManager.popBackStack();
            getSupportActionBar().show();
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // newsTitle.setText(mPlanetTitles[nowtitleid]);
           // openMenuBtn.setVisibility(View.VISIBLE);
           // refreshBtn.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mDrawerToggle.syncState();// 这个必须要，没有的话进去的默认是个箭头。。正常应该是三横杠的
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void blindView(){

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_FindFragment_title);
        pager = (ViewPager) findViewById(R.id.vp_FindFragment_pager);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
    }


    private void init(){
        //启动db manage
        dbManager = new NewsDbManager(this);
        drawerListDatas = new ArrayList<>();
        initDrawerList();
        mContext = MainActivity.this;
        toolbar.setTitle("扬大新闻");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        for(int i = 0; i < drawerListDatas.size(); i++){
            tabLayout.addTab(tabLayout.newTab().setText(drawerListDatas.get(i).getItemName()));
        }
        tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);
        pagerAdapter = new com.liuliugeek.sanc.news.Adapter.PagerAdapter(getSupportFragmentManager(), drawerListDatas, MainActivity.this);
        pager.setAdapter(pagerAdapter);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        setupDrawerContent(navigationView);
        fragmentManager = getSupportFragmentManager();
       // sendMsgToUpdate(3, 0);
    }



    private void setOnC(){
       /* openMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("mainactity", String.valueOf(MainActivity.this.nowtypeid));
                dbManager.deleteContent(MainActivity.this.nowtypeid);
                Toast.makeText(MainActivity.this, "正在获取最新新闻···", Toast.LENGTH_SHORT).show();
                sendMsgToUpdate(3, 0);
            }
        });
        */
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navigation_item_setting:
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.navigation_item_about:
                        Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.navigation_item_favorite:
                        Intent intent2 = new Intent(MainActivity.this, FavoritesActivity.class);
                        startActivity(intent2);
                        break;
                    default:
                        Log.v("navigationView","ni click navi");
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    public void initDrawerList(){
        drawerListDatas.add(new DrawerListData("新闻中心",R.drawable.abc_scrubber_control_off_mtrl_alpha,37746));
        drawerListDatas.add(new DrawerListData("学术活动",R.drawable.abc_scrubber_control_off_mtrl_alpha,37748));
        drawerListDatas.add(new DrawerListData("图片新闻",R.drawable.abc_scrubber_control_off_mtrl_alpha,37745));
        drawerListDatas.add(new DrawerListData("扬大要闻",R.drawable.abc_scrubber_control_off_mtrl_alpha,3));
        drawerListDatas.add(new DrawerListData("媒体扬大",R.drawable.abc_scrubber_control_off_mtrl_alpha,4));
        drawerListDatas.add(new DrawerListData("综合报道",R.drawable.abc_scrubber_control_off_mtrl_alpha,5));
        drawerListDatas.add(new DrawerListData("校报传真",R.drawable.abc_scrubber_control_off_mtrl_alpha,8));
        drawerListDatas.add(new DrawerListData("暖情校园",R.drawable.abc_scrubber_control_off_mtrl_alpha,9));
        drawerListDatas.add(new DrawerListData("缤纷扬大",R.drawable.abc_scrubber_control_off_mtrl_alpha,10));
    }

}
