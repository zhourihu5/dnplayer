package com.dongnao.dnplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.dongnao.dnplayer.player.DNPlayer;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

/**
 * @author Lance
 * @date 2018/9/7
 */
public class PlayActivity extends RxAppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private DNPlayer dnPlayer;
    public String url;
    private SeekBar seekBar;

    private int progress;
    private boolean isTouch;
    private boolean isSeek;
    int videoW,videoH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager
                .LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        dnPlayer = new DNPlayer();
        dnPlayer.setSurfaceView(surfaceView);
        dnPlayer.setOnPrepareListener(new DNPlayer.OnPrepareListener() {
            /**
             * 视频信息获取完成 随时可以播放的时候回调
             * @param w
             * @param h
             */
            @Override
            public void onPrepared(int w, int h) {
                //获得时间
                int duration = dnPlayer.getDuration();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //直播： 时间就是0
                        if (duration != 0){
                            //显示进度条
                            seekBar.setVisibility(View.VISIBLE);
                        }
                        videoH=h;
                        videoW=w;
                        setVideoView(surfaceView, w, h);
                    }
                });

                dnPlayer.start();
            }
        });
        dnPlayer.setOnErrorListener(new DNPlayer.OnErrorListener() {
            @Override
            public void onError(int error) {

            }
        });
        dnPlayer.setOnProgressListener(new DNPlayer.OnProgressListener() {

            @Override
            public void onProgress(final int progress2) {
                if (!isTouch) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int duration = dnPlayer.getDuration();
                            //如果是直播
                            if (duration != 0) {
                                if (isSeek){
                                    isSeek = false;
                                    return;
                                }
                                //更新进度 计算比例
                                seekBar.setProgress(progress2 * 100 / duration);
                            }
                        }
                    });
                }
            }
        });
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        url = getIntent().getStringExtra("url");
//        dnPlayer.setDataSource("/sdcard/b.mp4");
//        url="file:/data/data/com.dongnao.dnplayer/cache/c1ac99874e61248eb9cf2ce406dce1d3.mp4";
//        url="https://file.roadshowing.com/video/roadshow/2020/09/video/huawei2020kfzdh.mp4";
//        url="https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8";
//        url="http://192.188.0.116:8080/hls/mystream_src.m3u8";
//        url="rtmp://video.roadshowing.com/live/48037";
        dnPlayer.setDataSource(url);
    }

    private void setVideoView(SurfaceView surfaceView, int w, int h) {
        //设置视频的宽高
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int dw=displayMetrics.widthPixels;
        int dh=displayMetrics.heightPixels;
        float  f=Math.min(1.0f*dw/ w,1.0f*dh/ h);
        layoutParams.width= (int) (f* w);
        layoutParams.height= (int) (f* h);
        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                    .LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_play);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        dnPlayer.setSurfaceView(surfaceView);
        dnPlayer.setDataSource(url);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(progress);
        setVideoView(surfaceView, videoW, videoH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dnPlayer.prepare();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dnPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dnPlayer.release();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTouch = true;
    }

    /**
     * 停止拖动的时候回调
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isSeek = true;
        isTouch = false;
        progress = dnPlayer.getDuration() * seekBar.getProgress() / 100;
        //进度调整
        dnPlayer.seek(progress);
    }
}
