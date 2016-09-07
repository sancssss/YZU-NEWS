package com.liuliugeek.sanc.news;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by 73732 on 2016/8/19.
 */
@SuppressLint("ValidFragment")
public class NewsListFragment extends Fragment implements AbsListView.OnScrollListener{
    private static final int SHOW_CONTENT = 0;
    private static  final int ADD_LIST = 1;
    private static final int SHOW_ERROR_NETWORK = 2;
    private static int THREAD_COUNT = 0;

    private int typeid = 3;

    private FragmentManager fragmentManager;
    private ArrayList<Data> datas;
    private ListView newsList;
    private  MyHttp myHttp;
    private ParseContentDom parseContentDom;
    private ParseListDom parseListDom;
    private int tempPosition;
    private NewsAdapter newsAdapter;

    private TextView loadMoreTextView;
    private View loadMoreView;

    private int visibleLastIndex = 0;
    private int visibleItemCount;

    private Context context;

    private NewsDbManager dbManager;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_CONTENT:
                    int position = msg.arg1;
                    NewContentFragemnt newContentFragemnt = new NewContentFragemnt();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Bundle bd = new Bundle();
                    parseContentDom = (ParseContentDom)msg.obj;
                    bd.putString("content","<h2>"+datas.get(position).getNewTitle() +"</h2><br>"+parseContentDom.getContent());
                    bd.putString("url",datas.get(position).getNewUrl());
                    bd.putString("title",datas.get(position).getNewTitle());
                    //从原网址中获取id
                    int arcid = Integer.parseInt(datas.get(position).getNewContent().replaceAll(".*[^\\d](?=(\\d+))", ""));
                    //更新本地数据库
                    dbManager = new NewsDbManager(getActivity());
                    dbManager.updateContent(arcid,parseContentDom.getContent());
                    dbManager.closeDB();
                    Log.v("update_news",parseContentDom.getContent());
                    newContentFragemnt.setArguments(bd);
                    TextView newsTitle = (TextView) getActivity().findViewById(R.id.txt_title);
                    //getActivity().getActionBar().setTitle(datas.get(position).getNewTitle());
                    // fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
                    fragmentTransaction.replace(R.id.fl_content, newContentFragemnt);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                case  ADD_LIST:
                    parseListDom = new ParseListDom((String)msg.obj);
                    for(int i = 0; i < 21;i++){
                        //Log.v("url",parseListDom.getUrlList().get(i));+
                        Data data = new Data();
                        data.setNewTitle(parseListDom.getTitleList().get(i));
                        data.setNewContent(parseListDom.getUrlList().get(i));
                        data.setNewUrl(parseListDom.getUrlList().get(i));
                        data.setNewTypeid(msg.arg2);
                        data.setNewArcid(Integer.valueOf(data.getNewContent().replaceAll(".*[^\\d](?=(\\d+))", "")));
                        data.setNewDate(parseListDom.getTimeList().get(i));
                        newsAdapter.addItem(data);
                    }
                    newsAdapter.notifyDataSetChanged();
                    THREAD_COUNT=0;
                    //Log.v("list_count", String.valueOf(newsAdapter.getCount()));
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list, container ,false);
        loadMoreView = inflater.inflate(R.layout.load_more, null);
        //loadMoreButton = (Button)loadMoreView.findViewById(R.id.loadmore_button);
        loadMoreTextView = (TextView) loadMoreView.findViewById(R.id.loadmore_text);
        newsList = (ListView) view.findViewById(R.id.list_news);
        newsList.addFooterView(loadMoreView);
        newsAdapter = new NewsAdapter(datas, getActivity());
        newsList.setAdapter(newsAdapter);
        newsList.setOnScrollListener(this);
        /*loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore(v);
            }
        });*/
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //隐藏菜单键
               // getActivity().findViewById(R.id.my_actionbar_left).setVisibility(View.INVISIBLE);
               // getActivity().findViewById(R.id.my_actionbar_right).setVisibility(View.INVISIBLE);

                //Log.v("position", String.valueOf(position));
                sendMsgToGetContent(position);
            }
        });


        return view;
    }

    public void sendMsgToGetContent(int position){
        //Log.v("sub_String_content", datas.get(position).getNewContent().substring(0, 4));
        //Log.v("news_arcid", String.valueOf(datas.get(position).getNewArcid()));
        dbManager = new NewsDbManager(getActivity());
        if(!dbManager.isContentEmpty(datas.get(position).getNewArcid())){
            //从网址中获取arcid
                int arcid = datas.get(position).getNewArcid();
                //Log.v("arcid", String.valueOf(arcid));
                dbManager = new NewsDbManager(this.getActivity());
                //本地数据库存有文章
                //Log.v("is_content_empty","now is local datas");
                //int position = msg.arg1;
                NewContentFragemnt newContentFragemnt = new NewContentFragemnt();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                String content = dbManager.getContent(arcid);

                Bundle bd = new Bundle();
                bd.putString("content","<h2>"+datas.get(position).getNewTitle() +"</h2><br>"+content);
                bd.putString("url",datas.get(position).getNewUrl());
                bd.putString("title",datas.get(position).getNewTitle());

                newContentFragemnt.setArguments(bd);
                TextView newsTitle = (TextView) getActivity().findViewById(R.id.txt_title);
                //newsTitle.setText(dbManager.getTitle(arcid));
                //getActivity().getActionBar().setTitle(dbManager.getTitle(arcid));
                dbManager.closeDB();
                // fragmentTransaction.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit);
                fragmentTransaction.replace(R.id.fl_content, newContentFragemnt);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
        }else{
            if(null != MyHttp.getActiveNetwork(getActivity())){
                tempPosition = position;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            myHttp = new MyHttp(datas.get(tempPosition).getNewContent(), "GBK");
                            myHttp.startCon();
                            parseContentDom = new ParseContentDom(myHttp.getResult());
                            Message message = new Message();
                            message.what = SHOW_CONTENT;
                            message.obj = parseContentDom;
                            message.arg1 = tempPosition;
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
            handler.post(new Runnable() {
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
            });
            Log.v("LOADMORE", "loading");
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        this.visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }


    //模拟添加数据
    private void loadData(){
        int count = newsAdapter.getCount();
        Log.v("count",String.valueOf(count));
        //下一页页码，每页21
        int pageNum = (count / 21) + 1;
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
    }

    public void setTypeID(int id){
        this.typeid = id;
    }

    public int getTypeId(){
        return typeid;
    }
}
