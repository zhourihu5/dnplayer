package com.dongnao.dnplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dongnao.live.LiveManager;
import com.dongnao.live.list.Data;
import com.dongnao.live.list.Items;
import com.dongnao.live.list.LiveList;
import com.dongnao.live.list.Pictures;
import com.dongnao.live.room.Info;
import com.dongnao.live.room.Room;
import com.dongnao.live.room.Videoinfo;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends RxAppCompatActivity implements TabLayout
        .BaseOnTabSelectedListener, LiveAdapter.OnItemClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private LiveAdapter liveAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // 配置recycleview
        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        liveAdapter = new LiveAdapter(this);
        liveAdapter.setItemClickListener(this);
        recyclerView.setAdapter(liveAdapter);

        //配置tab
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);
        //添加标签
        addTabs();
//        startActivity(new Intent(this,PlayActivity.class));
    }

    private void addTabs() {
        addTab("lol", "英雄联盟");
        addTab("acg", "二次元");
        addTab("food", "美食");
    }

    private void addTab(String tag, String title) {
        TabLayout.Tab tab = tabLayout.newTab();
        tab.setTag(tag);
        tab.setText(title);
        tabLayout.addTab(tab);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayout.removeOnTabSelectedListener(this);
    }

    /**
     * 切换标签回调
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //请求获取房间 todo 显示加载等待
        LiveManager.getInstance()
                .getLiveList(tab.getTag().toString())
                .compose(this.<LiveList>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<LiveList>() {

                    @Override
                    public void onNext(LiveList liveList) {
                        if(liveList==null){
                            liveList=new LiveList();
                        }
                        if(liveList.getData()==null){
                            Data data=new Data();
                            liveList.setData(data);
                        }
                        if(liveList.getData().getItems()==null){
                            liveList.getData().setItems(new ArrayList<>());
                        }
                        if(liveList.getData().getItems().size()<1){
                            Items items=null;
                            Pictures pictures=null;

                            items= new Items();
                            items.setId("file:/data/data/com.dongnao.dnplayer/cache/c1ac99874e61248eb9cf2ce406dce1d3.mp4");
                            items.setName(items.getId());
                            pictures= new Pictures();
                            pictures.setImg(items.getId());
                            items.setPictures(pictures);
                            liveList.getData().getItems().add(items);

                            items= new Items();
                            items.setId("https://file.roadshowing.com/video/roadshow/2020/09/video/huawei2020kfzdh.mp4");
                            items.setName(items.getId());
                            pictures= new Pictures();
                            pictures.setImg(items.getId());
                            items.setPictures(pictures);
                            liveList.getData().getItems().add(items);

                            items= new Items();
                            items.setId("http://192.188.0.116:8080/hls/mystream_src.m3u8");
                            items.setName(items.getId());
                            pictures= new Pictures();
                            pictures.setImg(items.getId());
                            items.setPictures(pictures);
                            liveList.getData().getItems().add(items);

                            items= new Items();
                            items.setId("rtmp://video.roadshowing.com/live/48035");
                            items.setName(items.getId());
                            pictures= new Pictures();
                            pictures.setImg(items.getId());
                            items.setPictures(pictures);
                            liveList.getData().getItems().add(items);


                        }
                        liveAdapter.setLiveList(liveList);
                        liveAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        onNext(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onItemClick(String id) {
        LiveManager.getInstance()
                .getLiveRoom(id)
                .compose(this.<Room>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSubscriber<Room>() {


                    @Override
                    public void onNext(Room room) {
                        if(room==null){
                            room=new Room();
                            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                            intent.putExtra("url", id);
                            startActivity(intent);
                            return;
                        }
                        if(room.getData()==null){
                            room.setData(new com.dongnao.live.room.Data());
                        }
                        if(room.getData().getInfo()==null){
                            room.getData().setInfo(new Info());
                        }
                        if(room.getData().getInfo().getVideoinfo()==null){
                            room.getData().getInfo().setVideoinfo(new Videoinfo());
                        }
                        if(room.getData().getInfo().getVideoinfo().getPlflag()==null){
                            room.getData().getInfo().getVideoinfo().setPlflag(id+"_");
                        }
                        if(room.getData().getInfo().getVideoinfo().getRoom_key()==null){
                            room.getData().getInfo().getVideoinfo().setRoom_key(id+"_");
                        }
                        if(room.getData().getInfo().getVideoinfo().getSign()==null){
                            room.getData().getInfo().getVideoinfo().setSign(id+"_");
                        }
                        if(room.getData().getInfo().getVideoinfo().getTs()==null){
                            room.getData().getInfo().getVideoinfo().setTs(id+"_");
                        }

                        Videoinfo info = room.getData().getInfo().getVideoinfo();
                        String[] plflags = info.getPlflag().split("_");
                        String room_key = info.getRoom_key();
                        String sign = info.getSign();
                        String ts = info.getTs();
                        Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                        String v = "3";
                        if (null != plflags && plflags.length > 0) {
                            v = plflags[plflags.length - 1];
                        }
                        intent.putExtra("url", "http://pl" + v + ".live" +
                                ".panda.tv/live_panda/" + room_key
                                + "_mid" +
                                ".flv?sign=" + sign +
                                "&time=" + ts);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                        onNext(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
