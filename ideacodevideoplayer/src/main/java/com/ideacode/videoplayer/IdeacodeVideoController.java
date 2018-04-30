package com.ideacode.videoplayer;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 控制器抽象类
 *
 * Created by randysu on 2018/4/29.
 */

public abstract class IdeacodeVideoController extends FrameLayout implements View.OnTouchListener {

    private Context mContext;
    protected  IIdeacodeVideoPlayer mIdeacodeVideoPlayer;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;
    private static final int THRESHOLD = 80; // 滑动界面触发处理的最小距离，只要滑动距离大于等于80像素，就会触发事件响应
    private long mGestreDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

    public IdeacodeVideoController(@NonNull Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
    }

    public void setVideoPlayer(IIdeacodeVideoPlayer ideacodeVideoPlayer) {
        mIdeacodeVideoPlayer = ideacodeVideoPlayer;
    }

    /**
     * 设置播放视频的标题
     *
     * @param title
     */
    public abstract void setTitle(String title);

    /**
     * 设置视频默认图片
     *
     * 当你用Android Studio和IntelliJ的时候，如果给标注了这些注解的方法传递错误类型的参数，那么IDE就会实时标记出来。
     *
     * @param resId
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件
     *
     * @return
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长
     *
     * @param lenght
     */
    public abstract void setLenght(long lenght);

    /**
     * 当播放器播放状态发生变化是响应，在此方法中更新不同状态的UI
     *
     * @param playState
     */
    protected  abstract void onPlayStateChanged(int playState);

    /**
     * 当播放器播放模式发生变化时，在此方法中更新不同模式的控制器界面
     *
     * @param playMode
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态
     */
    protected abstract void reset();

    /**
     * 开启更新进度的计时器
     * 每隔1秒更新进度
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    IdeacodeVideoController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }



    /**
     * 取消更新进度的计数器
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }

        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // 只有全屏的时候才能拖动位置、亮度、声音
        if (!mIdeacodeVideoPlayer.isFullScreen()) {
            return false;
        }
        // 只有在播放、暂停、缓冲的时候才能拖动改变位置、亮度、声音
        if (mIdeacodeVideoPlayer.isIdle()
                || mIdeacodeVideoPlayer.isError()
                || mIdeacodeVideoPlayer.isPreparing()
                || mIdeacodeVideoPlayer.isPrepared()
                || mIdeacodeVideoPlayer.isCompleted()) {
            // TODO
            return false;
        }

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDetalX = Math.abs(deltaX);
                float absDetalY = Math.abs(deltaY);
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    // 只有在播放、暂停、缓冲的时候才能拖动改变位置、亮度、声音
                    if (absDetalX >= THRESHOLD) { // X轴数值改变调整视频播放位置
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestreDownPosition = mIdeacodeVideoPlayer.getCurrentPosition();
                    } else if (absDetalY >= THRESHOLD) {  // Y轴数值改变调整视频的音量或亮度
                        if (mDownX < getWidth() * 0.5f) {
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = VideoUtil.scanForActivity(mContext)
                                    .getWindow().getAttributes().screenBrightness;
                        } else {
                            // 右侧改变音量
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mIdeacodeVideoPlayer.getVolume();
                        }
                    }
                }

                // 改变播放位置
                if (mNeedChangePosition) {
                    long duration = mIdeacodeVideoPlayer.getDuration();
                    long toPosition = (long)(mGestreDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int)(100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }

                // 改变亮度
                if (mNeedChangeBrightness) {
                    deltaY = -deltaY;
                    float detaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + detaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = VideoUtil.scanForActivity(mContext)
                            .getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    VideoUtil.scanForActivity(mContext).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int)(100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }

                // 改变音量
                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = mIdeacodeVideoPlayer.getMaxVolume();
                    int deltaVolume = (int)(maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    mIdeacodeVideoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mNeedChangePosition) {
                    mIdeacodeVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume) {
                    hideChangeVolume();
                    return true;
                }
                break;
            default:
        }
        return false;
    }

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration
     * @param newPositionProgress
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或cancel时，隐藏控制器中间的播放位置变化视图
     * 在手势ACTION_UP   ACTION_CANCEL时调用
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动，显示控制音量变化视图
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolume
     */
    protected abstract void showChangeVolume(int newVolume);

    /**
     * 手势在右侧上下滑动改变音量后，手势up或cancel时，隐藏控制器中间的音量变化视图
     * 在手势ACTION_UP   ACTION_DOWN时调用
     *
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up、cancel时，隐藏控制器中间的亮度变化视图
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();

}
