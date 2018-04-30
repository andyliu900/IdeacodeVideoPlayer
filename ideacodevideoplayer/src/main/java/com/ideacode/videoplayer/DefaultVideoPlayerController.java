package com.ideacode.videoplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 默认的视频控制器
 *
 * Created by randysu on 2018/4/29.
 */

public class DefaultVideoPlayerController extends IdeacodeVideoController implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private ImageView mImage;
    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBatteryTime;
    private ImageView mBattery;
    private TextView mTime;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private ImageView mFullScreen;

    private TextView mLength;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;

    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;

    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mCompleted;
    private TextView mReplay;

    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;

    private String dataSourceUrl; // 播放连接

    private boolean hasRegisterBatteryReceiver; // 是否已经注册了电池广播

    public DefaultVideoPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.default_video_controller, this, true);

        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.thumb);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mBatteryTime = (LinearLayout) findViewById(R.id.battery_time);
        mBattery = (ImageView) findViewById(R.id.battery);
        mTime = (TextView) findViewById(R.id.time);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        mLength = (TextView) findViewById(R.id.length);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mChangePositon = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);

        mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);

        mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    public void setDataSource(String url) {
        dataSourceUrl = url;
        // 给播放器配置视频链接地址
        if (mIdeacodeVideoPlayer != null) {
            mIdeacodeVideoPlayer.setUp(url, null);
        }
    }

    @Override
    public void setVideoPlayer(IIdeacodeVideoPlayer ideacodeVideoPlayer) {
        super.setVideoPlayer(ideacodeVideoPlayer);
//        mIdeacodeVideoPlayer.setUp(dataSourceUrl, null);
    }

    @Override
    public void onClick(View view) {
        if (view == mCenterStart) {
            if (mIdeacodeVideoPlayer.isIdle()) {
                mIdeacodeVideoPlayer.start();
            }
        } else if (view == mBack) {
            if (mIdeacodeVideoPlayer.isFullScreen()) {
                mIdeacodeVideoPlayer.exitFullScreen();
            } else if (mIdeacodeVideoPlayer.isTinyWindow()) {
                mIdeacodeVideoPlayer.exitTinyWindow();
            }
        } else if (view == mRestartPause) {
            if (mIdeacodeVideoPlayer.isPlaying() || mIdeacodeVideoPlayer.isBufferingPlaying()) {
                mIdeacodeVideoPlayer.pause();
            } else if (mIdeacodeVideoPlayer.isPaused() || mIdeacodeVideoPlayer.isBuferingPaused()) {
                mIdeacodeVideoPlayer.restart();
            }
        } else if (view == mFullScreen) {
            if (mIdeacodeVideoPlayer.isNormal() || mIdeacodeVideoPlayer.isTinyWindow()) {
                mIdeacodeVideoPlayer.enterFullScreen();
            } else if (mIdeacodeVideoPlayer.isFullScreen()) {
                mIdeacodeVideoPlayer.exitFullScreen();
            }
        } else if (view == mRetry) {
            mIdeacodeVideoPlayer.restart();
        } else if (view == mReplay) {
            mRetry.performClick();
        } else if (view == this) { // 点击了整款播放器区域，显示top、bottom视图
            if (mIdeacodeVideoPlayer.isPlaying()
                    || mIdeacodeVideoPlayer.isPaused()
                    || mIdeacodeVideoPlayer.isBufferingPlaying()
                    || mIdeacodeVideoPlayer.isBuferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // 点击进度条
        if (mIdeacodeVideoPlayer.isBuferingPaused() || mIdeacodeVideoPlayer.isPaused()) {
            mIdeacodeVideoPlayer.restart();
        }
        if (mIdeacodeVideoPlayer.getSignalType() == IdeacodeVideoPlayer.SIGNAL_TYPE_RES) {
            long position = (long)(mIdeacodeVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
            mIdeacodeVideoPlayer.seekTo(position);
            startDismissTopBottomTimer();
        } else {
            seekBar.setProgress(100);
        }

    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setImage(int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setLenght(long lenght) {
        mLength.setText(VideoUtil.formatTime(lenght));
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case IdeacodeVideoPlayer.STATE_IDLE:
                break;
            case IdeacodeVideoPlayer.STATE_PREPARING:
                mImage.setVisibility(View.GONE);
                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在准备...");
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                mCenterStart.setVisibility(View.GONE);
                mLength.setVisibility(View.GONE);
                break;
            case IdeacodeVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                break;
            case IdeacodeVideoPlayer.STATE_PLAYING:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case IdeacodeVideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case IdeacodeVideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case IdeacodeVideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            case IdeacodeVideoPlayer.STATE_ERROR:
                cancelDismissTopBottomTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                break;
            case IdeacodeVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case IdeacodeVideoPlayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                mFullScreen.setVisibility(View.VISIBLE);
                mBatteryTime.setVisibility(View.GONE);
                if (hasRegisterBatteryReceiver) {
                    mContext.unregisterReceiver(mBatterReceiver);
                    hasRegisterBatteryReceiver = false;
                }
                break;
            case IdeacodeVideoPlayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                mBatteryTime.setVisibility(View.VISIBLE);
                if (!hasRegisterBatteryReceiver) {
                    mContext.registerReceiver(mBatterReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    hasRegisterBatteryReceiver = true;
                }
                break;
            case IdeacodeVideoPlayer.MODE_TINY_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);

        mBottom.setVisibility(View.GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mLength.setVisibility(View.VISIBLE);

        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }


    @Override
    protected void updateProgress() {
        long position = mIdeacodeVideoPlayer.getCurrentPosition();
        long duration = mIdeacodeVideoPlayer.getDuration();
        int bufferPercentage = mIdeacodeVideoPlayer.getBufferPrecentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int)(100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(VideoUtil.formatTime(position));
        mDuration.setText(VideoUtil.formatTime(duration));
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(View.VISIBLE);
        long newPosition = (long)(duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(VideoUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(VideoUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeVolume(int newVolume) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolume);
    }

    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    private BroadcastReceiver mBatterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                mBattery.setImageResource(R.drawable.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                mBattery.setImageResource(R.drawable.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (int)(((float)level / scale)*100);
                if (percentage <= 10) {
                    mBattery.setImageResource(R.drawable.battery_10);
                } else if (percentage <= 20) {
                    mBattery.setImageResource(R.drawable.battery_20);
                } else if (percentage <= 50) {
                    mBattery.setImageResource(R.drawable.battery_50);
                } else if (percentage <= 80) {
                    mBattery.setImageResource(R.drawable.battery_80);
                } else if (percentage <= 100) {
                    mBattery.setImageResource(R.drawable.battery_100);
                }
            }
        }
    };

    /**
     * 设置top、bottom显示隐藏
     *
     * @param visible
     */
    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mIdeacodeVideoPlayer.isPaused() && !mIdeacodeVideoPlayer.isBuferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    /**
     * 开启top、bottom自动小时的timer
     * 5秒后隐藏top、bottom
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(5000, 5000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
            mDismissTopBottomCountDownTimer.start();
        }
    }

    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }
}
