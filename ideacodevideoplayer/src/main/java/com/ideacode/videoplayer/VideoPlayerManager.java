package com.ideacode.videoplayer;

/**
 * 视频播放器管理器
 *
 * Created by randysu on 2018/4/27.
 */

public class VideoPlayerManager {

    private IdeacodeVideoPlayer mVideoPlayer;

    private VideoPlayerManager() {

    }

    private static VideoPlayerManager sInstance;

    public static synchronized VideoPlayerManager getsInstance() {
        if (sInstance == null) {
            sInstance = new VideoPlayerManager();
        }
        return sInstance;
    }

    public IdeacodeVideoPlayer getCurrentVideoPlayer() {
        return mVideoPlayer;
    }

    public void setCurrentVideoPlayer(IdeacodeVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    /**
     * 暂停
     */
    public void suspendVideoPlayer() {
        if (mVideoPlayer != null
                && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    public void resumeVideoPlayer() {
        if (mVideoPlayer != null
                && (mVideoPlayer.isPaused() || mVideoPlayer.isBuferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }

}
