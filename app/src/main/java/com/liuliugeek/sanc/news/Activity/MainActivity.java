package com.liuliugeek.sanc.news.Activity;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.liuliugeek.sanc.news.Activity.Fragment.NewsListFragment;
import com.liuliugeek.sanc.news.Adapter.DrawerListAdapter;
import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.Model.DrawerListData;
import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.Parse.ParseListDom;
import com.liuliugeek.sanc.news.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

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
    private PagerTabStrip tabStrip;


    private ArrayList<DrawerListData> drawerListDatas;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SHOW_LIST:
                    parseListDom = new ParseListDom((String)msg.obj);
                    //parseListDom.getUrlList();
                    datas = new ArrayList<>();
                    //Log.v("count", String.valueOf(parseListDom.getTitleList().size()));
                    for(int i = 0; i<21; i++){
                        //Log.v("getUrl",parseListDom.getUrlList().get(i));
                        //此处content放的是页面地址
                        //Data data = new Data("("+parseListDom.getTimeList().get(i).substring(5) + ")" + parseListDom.getTitleList().get(i),parseListDom.getUrlList().get(i));
                        Data data = new Data();
                        data.setNewTitle(parseListDom.getTitleList().get(i));
                        data.setNewContent(parseListDom.getUrlList().get(i));
                        data.setNewUrl(parseListDom.getUrlList().get(i));
                        data.setNewTypeid(msg.arg2);
                        data.setNewArcid(Integer.valueOf(data.getNewContent().replaceAll(".*[^\\d](?=(\\d+))", "")));
                        data.setNewDate(parseListDom.getTimeList().get(i));
                       // Log.v("id", String.valueOf(data.getNewArcid()));
                        datas.add(data);
                    }
                    //将数据存进本地数据库
                    dbManager.add(datas);
                    getSupportActionBar().setTitle(drawerListDatas.get(msg.arg1).getItemName());
                    //newsTitle.setText(mPlanetTitles[msg.arg1]);
                    NewsListFragment newsListFragment = new NewsListFragment(fragmentManager, datas);
                    newsListFragment.setTypeID(msg.arg2);
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fl_content, newsListFragment);
                    fragmentTransaction.commit();
                    break;
                case SHOW_ERROR_NETWORK:
                    Toast.makeText(MainActivity.this, "error network", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(MainActivity.this,String.valueOf(position), Toast.LENGTH_SHORT).show();
        switch (position){
            case 0:
                sendMsgToUpdate(3,0);
                break;
            case 1:
               sendMsgToUpdate(4,1);
                break;
            case 2:
                sendMsgToUpdate(5,2);
                break;
            case 3:
                sendMsgToUpdate(8,3);
                break;
            case  4:
                sendMsgToUpdate(9,4);
                break;
            case 5:
                sendMsgToUpdate(10,5);
                break;
            case 6:
                sendMsgToUpdate(37746,6);
                break;
            case 7:
                sendMsgToUpdate(37748,7);
                break;
            case 8:
                sendMsgToUpdate(37745,8);
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void sendMsgToUpdate(final int typeid, final int titleid){
        this.nowtypeid = typeid;
        this.nowtitleid = titleid;
        Log.v("sendmsg_titleid", String.valueOf(titleid));
        //数据库中有数据则从本地加载
        if(!dbManager.isListEmpty(typeid)){
            Log.v("is_from_db", "now datas is from db");
            //设置标题：扬大要闻
            //newsTitle.setText(mPlanetTitles[titleid]);
            getSupportActionBar().setTitle(drawerListDatas.get(titleid).getItemName());
            ArrayList<Data> datas = new ArrayList<Data>();
            datas = dbManager.query(typeid);
            NewsListFragment newsListFragment = new NewsListFragment(fragmentManager, datas);
            //扬大要闻typeid
            newsListFragment.setTypeID(typeid);
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, newsListFragment);
            fragmentTransaction.commit();
        }else {
            Log.v("is_from_network", "now datas is from network");
            //设置标题：扬大要闻
            //newsTitle.setText(mPlanetTitles[titleid]);
            getSupportActionBar().setTitle(drawerListDatas.get(titleid).getItemName());
            ArrayList<Data> datas = new ArrayList();
            NewsListFragment newsListFragment = new NewsListFragment(fragmentManager, datas);
            //扬大要闻typeid
            newsListFragment.setTypeID(typeid);
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content, newsListFragment);
            fragmentTransaction.commit();
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
            case  R.id.action_refresh:
                if(MyHttp.getActiveNetwork(MainActivity.this) != null){
                    dbManager.deleteContent(MainActivity.this.nowtypeid);
                    Toast.makeText(MainActivity.this, "正在获取当前栏目新闻···", Toast.LENGTH_SHORT).show();
                    sendMsgToUpdate(nowtypeid, nowtitleid);
                }else{
                    Message message = new Message();
                    message.what = SHOW_ERROR_NETWORK;
                    handler.sendMessage(message);
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void blindView(){

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //openMenuBtn = (Button) findViewById(R.id.my_actionbar_left);
        //refreshBtn = (Button) findViewById(R.id.my_actionbar_right);
        //pager = (ViewPager) findViewById(R.id.viewpager);
        //tabStrip = (PagerTabStrip) findViewById(R.id.pagertitle);
    }


    private void init(){
        //启动db manage
        dbManager = new NewsDbManager(this);
        drawerListDatas = new ArrayList<>();
        initDrawerList();
        mContext = MainActivity.this;
        mDrawerList.setAdapter(new DrawerListAdapter(drawerListDatas, mContext));
        toolbar.setTitle("Yzu news");
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
        fragmentManager = getSupportFragmentManager();
        sendMsgToUpdate(3, 0);
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
        mDrawerList.setOnItemClickListener(this);

    }

    public void initDrawerList(){
        drawerListDatas.add(new DrawerListData("扬大要闻",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("媒体扬大",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("综合报道",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("校报传真",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("暖情校园",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("缤纷扬大",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("新闻中心",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("学术活动",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
        drawerListDatas.add(new DrawerListData("图片新闻",R.drawable.abc_scrubber_control_off_mtrl_alpha,1));
    }

}
