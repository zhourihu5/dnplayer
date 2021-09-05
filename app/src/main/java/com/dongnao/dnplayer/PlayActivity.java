package com.dongnao.dnplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
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
             */
            @Override
            public void onPrepared() {
                //获得时间
                int duration = dnPlayer.getDuration();
                //直播： 时间就是0
                if (duration != 0){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           //显示进度条
                           seekBar.setVisibility(View.VISIBLE);
                       }
                   });
                }
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
        url="https://live-dtocnc-cdn.ysp.cctv.cn/08545806BA4B052A8DDB5751428952ADDB4A0B1B3280C9105CBA3933D2CA874161D24E8FA104432D4AA117460EACE018B020C46F6BA579F0C0BEFE5A0B5AAB2D6F1D58CC2B298B50F7847B93187C5AD06D968A5A36126ECFA07275235AAED6F8B9756EA515BAF67D8612A7CC73FB26BE80A180F33EE7D325465A5AFD3BF67B2C/2000210101.m3u8";
        dnPlayer.setDataSource(url);
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
