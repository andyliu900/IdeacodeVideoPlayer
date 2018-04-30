package com.ideacode.myvideoplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.ideacode.videoplayer.DefaultVideoPlayerController;
import com.ideacode.videoplayer.IdeacodeVideoPlayer;
import com.ideacode.videoplayer.LogUtil;
import com.ideacode.videoplayer.VideoPlayerManager;

public class MainActivity extends AppCompatActivity {

    private IdeacodeVideoPlayer liveVideoPlayer;

    private IdeacodeVideoPlayer mp4VideoPlayer;

    // 直播信号
    private String hksUrl = "http://acm.gg/jade.m3u8";

    // 网络视频文件信号
    private String mp4Url = "http://jzvd.nathen.cn/1b61da23555d4ce28c805ea303711aa5/7a33ac2af276441bb4b9838f32d8d710-5287d2089db37e62345123a1be272f8b.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  直播信号
        liveVideoPlayer = (IdeacodeVideoPlayer)findViewById(R.id.live_videoplayer);
        liveVideoPlayer.setPlayerType(IdeacodeVideoPlayer.TYPE_IJK);
        liveVideoPlayer.setSigalType(IdeacodeVideoPlayer.SIGNAL_TYPE_LIVE);

        DefaultVideoPlayerController controller = new DefaultVideoPlayerController(this);
        controller.setTitle("高清翡翠台");
        controller.setLenght(0);
        Glide.with(this)
                .load("http://e.hiphotos.baidu.com/zhidao/pic/item/3bf33a87e950352af29733445b43fbf2b3118b83.jpg")
                .placeholder(R.mipmap.img_default)
                .crossFade()
                .into(controller.imageView());

        liveVideoPlayer.setController(controller);
        liveVideoPlayer.setUp(hksUrl, null);


        // 网络视频信号
        mp4VideoPlayer = (IdeacodeVideoPlayer)findViewById(R.id.mp4_videoplayer);
        mp4VideoPlayer.setPlayerType(IdeacodeVideoPlayer.TYPE_IJK);
        mp4VideoPlayer.setSigalType(IdeacodeVideoPlayer.SIGNAL_TYPE_RES);

        DefaultVideoPlayerController mp4Controller = new DefaultVideoPlayerController(this);
        mp4Controller.setTitle("mp4信号");
        mp4Controller.setLenght(52000);
        Glide.with(this)
                .load("http://www.3dmgame.com/uploads/allimg/150205/276_150205114148_1.jpg")
                .placeholder(R.mipmap.img_default)
                .crossFade()
                .into(mp4Controller.imageView());
        mp4VideoPlayer.setController(mp4Controller);
        mp4VideoPlayer.setUp(mp4Url, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        VideoPlayerManager.getsInstance().releaseVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getsInstance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
