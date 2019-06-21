package com.eeka.mespad.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.eeka.mespad.PadApplication;
import com.eeka.mespad.R;
import com.eeka.mespad.utils.DateUtil;

import java.lang.ref.WeakReference;

/**
 * 视频播放页面
 * Created by Lenovo on 2017/7/24.
 */

public class VideoPlayerActivity extends BaseActivity {

    private VideoView mVideoView;
    private ImageView mIv_play;
    private RelativeLayout mLayout_control;
    private SeekBar mSeekBar;
    private TextView mTv_playTime, mTv_duration;

    private MyHandler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_videoplayer);

        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        mHandler = new MyHandler(this);
        mLayout_control = findViewById(R.id.layout_videoPlayer_control);
        mSeekBar = findViewById(R.id.seekBar_videoPlayer_progress);
        mSeekBar.setOnSeekBarChangeListener(new SeekChangedListener());
        mTv_playTime = findViewById(R.id.tv_videoPlayer_playTime);
        mTv_duration = findViewById(R.id.tv_videoPlayer_duration);

        mVideoView = findViewById(R.id.videoView);
        mVideoView.setOnTouchListener(new PlayerTouchListener());
        mIv_play = findViewById(R.id.iv_videoPlayer_play);
        mIv_play.setOnClickListener(this);

        showLoading();
        String videoUrl = getIntent().getStringExtra("videoUrl");
        HttpProxyCacheServer proxy = PadApplication.getProxy(mContext);
        String proxyUrl = proxy.getProxyUrl(videoUrl);
        mVideoView.setVideoPath(proxyUrl);//获取视频文件路径
        mVideoView.setOnPreparedListener(new PreparedListener());
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                dismissLoading();
                toggleControl(true);
                return false;
            }
        });
        mVideoView.setOnCompletionListener(new CompleteListener());
        mVideoView.start();//开始播放
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.iv_videoPlayer_play) {
            mHandler.removeMessages(MyHandler.WHAT_HIDE);
            if (mVideoView.isPlaying()) {
                mIv_play.setImageResource(R.drawable.play);
                mVideoView.pause();
            } else {
                mIv_play.setImageResource(R.drawable.pause);
                mVideoView.start();
                mHandler.sendEmptyMessageDelayed(MyHandler.WHAT_HIDE, 3000);
            }
        }
    }

    private class SeekChangedListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MyHandler.WHAT_HIDE);
            mHandler.sendEmptyMessageDelayed(MyHandler.WHAT_HIDE, 3000);
            int progress = seekBar.getProgress();
            int duration = mVideoView.getDuration();
            int curTime = (int) ((float) progress * duration / 100);
            mVideoView.seekTo(curTime);
        }
    }

    private static class MyHandler extends Handler {

        static final int WHAT_UPDATE = 0;
        static final int WHAT_HIDE = 1;

        WeakReference<VideoPlayerActivity> mWeakActivity;

        MyHandler(VideoPlayerActivity weakActivity) {
            mWeakActivity = new WeakReference<>(weakActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoPlayerActivity activity = mWeakActivity.get();
            if (activity != null) {
                if (msg.what == WHAT_HIDE) {
                    activity.toggleControl(false);
                } else if (msg.what == WHAT_UPDATE) {
                    int duration = activity.mVideoView.getDuration();
                    int curPosition = activity.mVideoView.getCurrentPosition();
                    activity.mTv_playTime.setText(DateUtil.millisToDate(curPosition, "mm:ss"));
                    int curTime = (int) ((float) curPosition / duration * 100);
                    activity.mSeekBar.setProgress(curTime);
                    sendEmptyMessageDelayed(WHAT_UPDATE, 1000);
                }
            }
        }
    }

    /**
     * 显示/隐藏控制控件
     *
     * @param flag 显示/隐藏
     */
    private void toggleControl(boolean flag) {
        if (flag) {
            mIv_play.setVisibility(View.VISIBLE);
            mLayout_control.setVisibility(View.VISIBLE);
        } else {
            mIv_play.setVisibility(View.GONE);
            mLayout_control.setVisibility(View.GONE);
        }
    }

    private class PlayerTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mHandler.removeMessages(MyHandler.WHAT_HIDE);
                toggleControl(mIv_play.getVisibility() == View.GONE);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (mIv_play.getVisibility() == View.VISIBLE) {
                    mHandler.sendEmptyMessageDelayed(MyHandler.WHAT_HIDE, 3000);
                }
            }
            return true;
        }
    }

    private class PreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            dismissLoading();
            mIv_play.setImageResource(R.drawable.pause);
            mHandler.sendEmptyMessageDelayed(MyHandler.WHAT_HIDE, 3000);
            int duration = mVideoView.getDuration();
            mTv_duration.setText(DateUtil.millisToDate(duration, "mm:ss"));
            mHandler.sendEmptyMessage(MyHandler.WHAT_UPDATE);
        }
    }

    private class CompleteListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mVideoView.seekTo(0);
            toggleControl(true);
            mIv_play.setImageResource(R.drawable.play);
        }
    }

    public static Intent getIntent(Context context, String videoUrl) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        return intent;
    }
}
