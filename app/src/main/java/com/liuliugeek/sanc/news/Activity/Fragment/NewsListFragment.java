package com.liuliugeek.sanc.news.Activity.Fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuliugeek.sanc.news.Activity.ContentActivity;
import com.liuliugeek.sanc.news.Adapter.NewsAdapter;
import com.liuliugeek.sanc.news.DBManager.NewsDbManager;
import com.liuliugeek.sanc.news.Model.Data;
import com.liuliugeek.sanc.news.MyHttp.MyHttp;
import com.liuliugeek.sanc.news.Parse.ParseContentDom;
import com.liuliugeek.sanc.news.Parse.ParseListDom;
import com.liuliugeek.sanc.news.Parse.ParseSpecialContentDom;
import com.liuliugeek.sanc.news.Parse.ParseSpecialListDom;
import com.liuliugeek.sanc.news.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;


/**
 * Created by 73732 on 2016/8/19.
 */
@SuppressLint("ValidFragment")
public class NewsListFragment extends Fragment implements AbsListView.OnScrollListener{
    private static final int SHOW_CONTENT = 0;
    private static  final int ADD_LIST = 1;
    private static final int SHOW_ERROR_NETWORK = 2;
    private static final int REFRESH_LIST = 3;
    private static int THREAD_COUNT = 0;

    private int typeid = 0;

    private FragmentManager fragmentManager;
    private ArrayList<Data> datas;
    private ListView newsList;
    private MyHttp myHttp;
    private ParseContentDom parseContentDom;
    private ParseSpecialContentDom parseSpecialContentDom;
    private ParseListDom parseListDom;
    private ParseSpecialListDom parseSpecialListDom;
    private int tempPosition;
    private NewsAdapter newsAdapter;

    private TextView loadMoreTextView;
    private View loadMoreView;
    private PtrClassicFrameLayout ptrClassicFrameLayout;

    private int visibleLastIndex = 0;
    private int visibleItemCount;

    private Context context;

    private NewsDbManager dbManager;
    private Intent intent;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_CONTENT:
                    int position = msg.arg1;
                    NewContentFragemnt newContentFragemnt = new NewContentFragemnt();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    Bundle bd = new Bundle();
                    intent = new Intent(getContext(), ContentActivity.class);
                    dbManager = new NewsDbManager(getActivity());
                    Data tempData = datas.get(position);
                    if(getTypeId() < 100){
                        parseContentDom = (ParseContentDom)msg.obj;
                        intent.putExtra("content", parseContentDom.getContent());
                        dbManager.updateContent(tempData.getNewArcid(), parseContentDom.getContent());
                    }else{
                        parseSpecialContentDom = (ParseSpecialContentDom)msg.obj;
                        intent.putExtra("content", parseSpecialContentDom.getContent());
                        dbManager.updateContent(tempData.getNewArcid(),parseSpecialContentDom.getContent());
                    }
                    intent.putExtra("arcid", tempData.getNewArcid());
                    intent.putExtra("typeid", getTypeId());
                    intent.putExtra("url", tempData.getNewUrl());
                    intent.putExtra("title", tempData.getNewTitle());
                    intent.putExtra("date",tempData.getNewDate());
                    intent.putExtra("favorite", tempData.getIsFavorite());
                    //更新本地数据库
                    dbManager.closeDB();
                    getActivity().startActivity(intent);
                    /*
                   // Log.v("update_news",parseContentDom.getContent());
                    newContentFragemnt.setArguments(bd);
                   // getActivity().getActionBar().setTitle(datas.get(position).getNewTitle());
                    //fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
                    fragmentTransaction.replace(R.id.fl_content, newContentFragemnt);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    */
                    break;
                case  ADD_LIST:
                    List<String> tempTitleList = new ArrayList<>();
                    List<String> tempUrlList = new ArrayList<>();
                    List<String> tempTimeList = new ArrayList<>();
                    int count = 0;
                    //Log.v("count", String.valueOf(parseListDom.getTitleList().size()));
                    if(getTypeId() < 100){
                        parseListDom = new ParseListDom((String)msg.obj);
                        tempTitleList = parseListDom.getTitleList();
                        tempUrlList = parseListDom.getUrlList();
                        tempTimeList = parseListDom.getTimeList();
                        count = 21;
                    }else{
                        parseSpecialListDom = new ParseSpecialListDom((String)msg.obj);
                        tempTitleList = parseSpecialListDom.getTitleList();
                        tempUrlList = parseSpecialListDom.getUrlList();
                        tempTimeList = parseSpecialListDom.getTimeList();
                        count = 60;
                    }
                    for(int i = 0; i<count; i++){
                        //Log.v("getUrl",parseListDom.getUrlList().get(i));
                        //此处content放的是页面地址
                        //Data data = new Data("("+parseListDom.getTimeList().get(i).substring(5) + ")" + parseListDom.getTitleList().get(i),parseListDom.getUrlList().get(i));
                        Data data = new Data();
                        data.setNewTitle(tempTitleList.get(i));
                        data.setNewContent(tempUrlList.get(i));
                        data.setNewUrl(tempUrlList.get(i));
                        data.setNewTypeid(getTypeId());
                        data.setNewArcid(Integer.valueOf(data.getNewContent().replaceAll(".*[^\\d](?=(\\d+))", "").replace(".html","")));
                        data.setNewDate(tempTimeList.get(i));
                        // Log.v("id", String.valueOf(data.getNewArcid()));
                        newsAdapter.addItem(data);
                    }
                    newsAdapter.notifyDataSetChanged();
                    THREAD_COUNT=0;
                    //Log.v("list_count", String.valueOf(newsAdapter.getCount()));
                    break;
                case REFRESH_LIST:
                    //parseListDom.getUrlList();
                    datas = new ArrayList<>();
                    tempTitleList = new ArrayList<>();
                    tempUrlList = new ArrayList<>();
                    tempTimeList = new ArrayList<>();
                    count = 0;
                    //Log.v("count", String.valueOf(parseListDom.getTitleList().size()));
                    if(getTypeId() < 100){
                        parseListDom = new ParseListDom((String)msg.obj);
                        tempTitleList = parseListDom.getTitleList();
                        tempUrlList = parseListDom.getUrlList();
                        tempTimeList = parseListDom.getTimeList();
                        count = 21;
                    }else{
                        parseSpecialListDom = new ParseSpecialListDom((String)msg.obj);
                        tempTitleList = parseSpecialListDom.getTitleList();
                        tempUrlList = parseSpecialListDom.getUrlList();
                        tempTimeList = parseSpecialListDom.getTimeList();
                        count = 60;
                    }
                    for(int i = 0; i<count; i++){
                        //Log.v("getUrl",parseListDom.getUrlList().get(i));
                        //此处content放的是页面地址
                        //Data data = new Data("("+parseListDom.getTimeList().get(i).substring(5) + ")" + parseListDom.getTitleList().get(i),parseListDom.getUrlList().get(i));
                        Data data = new Data();
                        data.setNewTitle(tempTitleList.get(i));
                        data.setNewContent(tempUrlList.get(i));
                        data.setNewUrl(tempUrlList.get(i));
                        data.setNewTypeid(getTypeId());
                        data.setNewArcid(Integer.valueOf(data.getNewContent().replaceAll(".*[^\\d](?=(\\d+))", "").replace(".html","")));
                        data.setNewDate(tempTimeList.get(i));
                        // Log.v("id", String.valueOf(data.getNewArcid()));
                        datas.add(data);
                    }
                    newsAdapter = new NewsAdapter(datas, getActivity());
                    newsList.setAdapter(newsAdapter);
                    //Log.v("typeid_refresh_in_handler", String.valueOf(getTypeId()));
                    dbManager.deleteContent(getTypeId());
                    //更新刷新时间
                    dbManager.setBoardRefreshDate(getTypeId(), getDate());
                    //将数据存进本地数据库
                    dbManager.add(datas);
                    dbManager.closeDB();
                    newsAdapter.notifyDataSetChanged();
                    THREAD_COUNT=0;
                    break;
                case SHOW_ERROR_NETWORK:
                    Toast.makeText(getActivity(), "网络错误！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }


        }
    };


    public NewsListFragment(FragmentManager fragmentManager, ArrayList<Data> datas){
        this.datas = datas;
        this.fragmentManager = fragmentManager;
    }

    public NewsListFragment(){

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list, container ,false);

        loadMoreView = inflater.inflate(R.layout.load_more, null);
        loadMoreTextView = (TextView) loadMoreView.findViewById(R.id.loadmore_text);
        newsList = (ListView) view.findViewById(R.id.list_news);
        newsList.addFooterView(loadMoreView);
        newsList.setOnScrollListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            newsList.setNestedScrollingEnabled(true);
        }

        ptrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.ptr_refresh);
        ptrClassicFrameLayout.setLastUpdateTimeRelateObject(this);
        ptrClassicFrameLayout.setResistance(1.7f);
        ptrClassicFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrClassicFrameLayout.setDurationToClose(200);
        ptrClassicFrameLayout.setDurationToCloseHeader(1000);
        ptrClassicFrameLayout.setPullToRefresh(false);
        ptrClassicFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrClassicFrameLayout.setLoadingMinTime(2000);
        //判断今天是否刷新决定是否自动刷新
        dbManager = new NewsDbManager(getActivity());
        //没有刷新并且列表不为空才会调用
        Log.v("is_list_empty", String.valueOf(dbManager.isListEmpty(getTypeId())));
        Log.v("getTypeId", String.valueOf(getTypeId()));
        //丢失现场则从Bundle恢复现场
        if(getTypeId() == 0){
            setTypeID(savedInstanceState.getInt("typeId"));
            datas = (ArrayList<Data>) savedInstanceState.getSerializable("datas");
        }
        if (!dbManager.isRefresh(getTypeId()) || dbManager.isListEmpty(getTypeId())){
            ptrClassicFrameLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrClassicFrameLayout.autoRefresh(true);
                }
            },100);
        }else{
            newsAdapter = new NewsAdapter(datas, getActivity());
            newsList.setAdapter(newsAdapter);
        }
        dbManager.closeDB();
        ptrClassicFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout ptrFrameLayout) {
                ptrClassicFrameLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        if (THREAD_COUNT == 0) {
                            THREAD_COUNT++;
                            refreshList();
                        }
                        ptrClassicFrameLayout.refreshComplete();
                    }
                });
            }
        });

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendMsgToGetContent(position);
            }
        });


        return view;
    }


    public void refreshList(){
        if(getTypeId() < 100){
            refreshListData();
        }else{
            refreshSpecialListData();
        }
    }

    public void refreshListData(){
        //dbManager = new NewsDbManager(getActivity());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(null != MyHttp.getActiveNetwork(getActivity())){
                    Log.v("typeid_refresh", String.valueOf(getTypeId()));
                    myHttp = new MyHttp("http://news.yzu.edu.cn/list.asp?TypeID="+getTypeId()+"&Page=1","GBK");
                    myHttp.startCon();
                    String tempHtmlText = myHttp.getResult();
                    Message message = new Message();
                    message.what = REFRESH_LIST;
                    message.obj = tempHtmlText;
                    message.arg1 = getTypeId();
                    handler.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.what = SHOW_ERROR_NETWORK;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public void refreshSpecialListData(){
        //dbManager = new NewsDbManager(getActivity());
        //fragmentManager = getFragmentManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(null != MyHttp.getActiveNetwork(getActivity())){
                    Log.v("typeid_refresh", String.valueOf(getTypeId()));
                    myHttp = new MyHttp("http://www.yzu.edu.cn/module/jslib/jquery/jpage/dataproxy.jsp?startrecord=1&endrecord=60&perpage=20","UTF-8");
                    myHttp.setPostVal("col=1&appid=1&webid=100&path=%2F&columnid=" + getTypeId() + "&sourceContentType=1&unitid=55987&webname=%E6%89%AC%E5%B7%9E%E5%A4%A7%E5%AD%A6&permissiontype=0");
                    myHttp.startSpecialCon();
                    String tempHtmlText = myHttp.getResult();
                    Log.v("specailHtml",tempHtmlText);
                    Message message = new Message();
                    message.what = REFRESH_LIST;
                    message.obj = tempHtmlText;
                    message.arg1 = getTypeId();
                    handler.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.what = SHOW_ERROR_NETWORK;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    public void sendMsgToGetContent(int position){
        //Log.v("sub_String_content", datas.get(position).getNewContent().substring(0, 4));
        //Log.v("news_arcid", String.valueOf(datas.get(position).getNewArcid()));
        dbManager = new NewsDbManager(getActivity());
        if(!dbManager.isContentEmpty(datas.get(position).getNewArcid())){
            //从网址中获取arcid
                int arcid = datas.get(position).getNewArcid();
                //Log.v("arcid", String.valueOf(arcid));
                //本地数据库存有文章
                //Log.v("is_content_empty","now is local datas");
                //int position = msg.arg1;
                NewContentFragemnt newContentFragemnt = new NewContentFragemnt();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                String content = dbManager.getContent(arcid);
                intent = new Intent(getContext(), ContentActivity.class);
                intent.putExtra("arcid", datas.get(position).getNewArcid());
                intent.putExtra("content",  content);
                intent.putExtra("url", datas.get(position).getNewUrl());
                intent.putExtra("title", datas.get(position).getNewTitle());
                intent.putExtra("typeid", getTypeId());;
                intent.putExtra("date", datas.get(position).getNewDate());
                intent.putExtra("favorite", datas.get(position).getIsFavorite());
                dbManager.closeDB();
                startActivity(intent);
        }else{
            if(null != MyHttp.getActiveNetwork(getActivity())){
                tempPosition = position;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = SHOW_CONTENT;
                            if(getTypeId() < 100){
                                myHttp = new MyHttp(datas.get(tempPosition).getNewUrl(), "GBK");
                                myHttp.startCon();
                                parseContentDom = new ParseContentDom(myHttp.getResult());

                                message.obj = parseContentDom;
                                message.arg1 = tempPosition;
                            }else{
                                myHttp = new MyHttp(datas.get(tempPosition).getNewUrl(), "UTF-8");
                                myHttp.startCon();
                                parseSpecialContentDom = new ParseSpecialContentDom(myHttp.getResult());
                                message.obj = parseSpecialContentDom;
                                message.arg1 = tempPosition;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
            }else {
                Message message = new Message();
                message.what = SHOW_ERROR_NETWORK;
                handler.sendMessage(message);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = newsAdapter.getCount() - 1;//数据集最后一项下标
        int lastIndex = itemsLastIndex + 1;//加上底部view
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex){
            //滑动停止状态
            //异步加载代码在这放
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.v("threadcount", String.valueOf(THREAD_COUNT));
                    if(THREAD_COUNT == 0) {
                        THREAD_COUNT++;
                        loadData();
                        newsList.setSelection(visibleLastIndex - visibleItemCount + 1);
                        // 这个方法的作用就是将第position个item显示在listView的最上面一项，假如有一个ListView控件，其一次只能显示10个item，但现在有20个数据项，设置好adapter以后，默认是第一个数据项显示在最上面，如果我现在调用setSelection(2),则第3个数据项会显示在最上面，调用setSelection(9),则第10个数据项会显示在最上面。但需要注意的是，如果我调用setSelection(19),第20个数据项不会显示在最上面，因为其可以显示10个数据项，最上面的一个最大只可能是11，也就是说如果setSelection传入参数大于10，都只会显示11；如果小于11，则传入参数是几最上面一项显示的就是几.
                        // listView滚动到最后一个条目的方法:listview.setSelection(n-1)(n为数据的个数)
                        //listview.setFooterDividersEnabled(false);这个方法是控制footer下边是否显示默认的分隔线，如果为true,显示分隔线，否则不显示
                        loadMoreTextView.setText("加载中...");
                    }
                }
            },1200);
            Log.v("LOADMORE", "loading");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        this.visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }


    //加载更多
    private void loadData(){
        int count = newsAdapter.getCount();
        int pageNum;
        Log.v("count",String.valueOf(count));
        //下一页页码，每页21or60
        if(getTypeId() < 100){
            pageNum = (count / 21) + 1;
            Log.v("pageNum", String.valueOf(pageNum));
            myHttp = new MyHttp("http://news.yzu.edu.cn/list.asp?TypeID="+getTypeId()+"&Page="+pageNum,"GBK");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("new Thread","new Thread");
                    if(null != MyHttp.getActiveNetwork(getActivity())){
                        myHttp.startCon();
                        Message message = new Message();
                        message.what = ADD_LIST;
                        message.obj = myHttp.getResult();
                        handler.sendMessage(message);
                    }else {
                        Message message = new Message();
                        message.what = SHOW_ERROR_NETWORK;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }else{
            pageNum = (count / 60);
            myHttp = new MyHttp("http://www.yzu.edu.cn/module/jslib/jquery/jpage/dataproxy.jsp?startrecord="+pageNum*60+"&endrecord="+(pageNum+1)*60+"&perpage=20","UTF-8");
            myHttp.setPostVal("col=1&appid=1&webid=100&path=%2F&columnid=" + getTypeId() + "&sourceContentType=1&unitid=55987&webname=%E6%89%AC%E5%B7%9E%E5%A4%A7%E5%AD%A6&permissiontype=0");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v("new Thread","new Thread");
                    if(null != MyHttp.getActiveNetwork(getActivity())){
                        myHttp.startSpecialCon();
                        Message message = new Message();
                        message.what = ADD_LIST;
                        message.obj = myHttp.getResult();
                        handler.sendMessage(message);
                    }else {
                        Message message = new Message();
                        message.what = SHOW_ERROR_NETWORK;
                        handler.sendMessage(message);
                    }
                }
            }).start();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v("onSaveInstanceState", String.valueOf(typeid));
        outState.putInt("typeId", this.typeid);
        outState.putSerializable("datas", datas);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //恢复现场
        Log.v("onActivitycreate","onActivitycreate");
        if(getTypeId() == 0 ){
            setTypeID(savedInstanceState.getInt("typeId"));
        }
        dbManager = new NewsDbManager(getActivity());
    }

    private String getDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    public void setTypeID(int id){
        this.typeid = id;
    }

    public int getTypeId(){
        return typeid;
    }

    public void setDatas(ArrayList<Data> datas){
        this.datas = datas;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }


}
