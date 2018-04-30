package com.ideacode.videoplayer;

import java.util.Map;

/**
 * 播放器抽象接口
 *
 * Created by randysu on 2018/4/26.
 */

public interface IIdeacodeVideoPlayer {

    /**
     * 设置视频URL， 以及headers
     *
     * @param url
     * @param header
     */
    void setUp(String url, Map<String, String> header);

    /**
     * 开始播放
     */
    void start();

    /**
     * 指定的位置开始播放
     *
     * @param position
     */
    void start(long position);

    /**
     * 重新播放，播放器被暂停、播放错误、播放完成后，需要重新播放
     */
    void restart();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * seek到指定的位置继续播放
     *
     * @param pos
     */
    void seekTo(long pos);

    /**
     * 设置音量
     *
     * @param volume
     */
    void setVolume(int volume);

    /**
     * 设置播放速度，目前只有IjkPlayer有效果，原生MediaPlayer暂不支持
     *
     * @param speed
     */
    void setSpeed(float speed);

    /**
     * 开始播放时，是否从上一次的位置继续播放
     *
     * @param continueFromLastPosition
     */
    void continueFromLastPosition(boolean continueFromLastPosition);


    /************************
     * 这九个方法时播放器在当前的播放状态
     ***********************/
    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBuferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();

    /************************
     * 播放器的运行模式
     ************************/
    boolean isFullScreen();
    boolean isTinyWindow();
    boolean isNormal();

    /**
     * 获取最大音量
     *
     * @return
     */
    int getMaxVolume();

    /**
     * 获取当前音量
     *
     * @return
     */
    int getVolume();

    /**
     * 获取视频的总时长
     *
     * @return
     */
    long getDuration();

    /**
     * 获取当前播放的位置
     *
     * @return
     */
    long getCurrentPosition();

    /**
     * 获取视频缓冲百分比
     *
     * @return
     */
    int getBufferPrecentage();

    /**
     * 获取播放速度
     *
     * @return
     */
    float getSpeed(float speed);

    /**
     * 获取网络加载速度
     *
     * @return
     */
    long getTcpSpeed();

    /**
     * 进入全屏模式
     *
     * @return
     */
    void enterFullScreen();

    /**
     * 推出全屏模式
     *
     * @return
     */
    boolean exitFullScreen();

    /**
     * 进入小窗口模式
     *
     * @return
     */
    void enterTinyWindow();

    /**
     * 推出小窗口模式
     *
     * @return
     */
    boolean exitTinyWindow();

    /**
     * 此方法只释放播放器
     * 不管全屏、小窗口、还是normal状态下控制器的UI都不恢复初始状态
     * 这样以便在当前播放器状态下可以方便的切换不同的清晰度的视频地址
     *
     * @return
     */
    void releasePlayer();

    /**
     * 释放IVideoPlayer，释放后，内部的播放器被释放掉，同时如果在全屏、小窗口模式下都会退出
     * 并且控制器的UI页应该恢复到初始状态
     * @return
     */
    void release();
}
