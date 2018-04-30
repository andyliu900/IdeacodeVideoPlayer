package com.ideacode.videoplayer;

import android.content.Context;
import android.view.TextureView;

/**
 * 重写TextureView，适配视频的宽高和旋转
 * TextureView概念参考   https://blog.csdn.net/AND_YOU_with_ME/article/details/72900106
 *
 * Created by randysu on 2018/4/26.
 */

public class IdeacodeTextureView extends TextureView {

    private int videoHeight;
    private int videoWidth;

    public IdeacodeTextureView(Context context) {
        super(context);
    }

    public void adaptVideoSize(int videoWidth, int videoHeight) {
        if (this.videoWidth != videoWidth && this.videoHeight != videoHeight) {
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            requestLayout();
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float viewRotation = getRotation();

        // 如果判断成立，说明显示的TextureView和本身的位置有90度的旋转，所以需要交换宽高参数
        if (viewRotation == 90f || viewRotation == 270f) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }

        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);

        if (videoWidth > 0 && videoHeight > 0) {
            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // 指定大小的视图，将实际的大小赋值给宽高
                width = widthSpecSize;
                height = heightSpecSize;

                // 为了兼容性，我们根据纵横比调整大小
                // 视频宽度*视图高度   比   视图宽度*视频高度   小，说明要以视图的高度为基准调整视图的宽度
                if (videoWidth * height < width * videoHeight) {
                    width = height * videoWidth / videoHeight;
                } else { // 否则的话，以视图的
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // 指定了宽度，高度按比例调整
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    //  在约束内无法匹配宽高比
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // 指定了高度，宽度也要按比例调整
                height = heightSpecSize;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    //  在约束内无法匹配宽高比
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            } else {
                // 宽高都没有指定，显示视频的实际的宽高
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // 太高，减少宽度和高度
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // 太宽，减少宽度和高度
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {
            // 没有大小，只是采用给定的规格尺寸
        }
        setMeasuredDimension(width, height);
    }

    public interface OnSizeMeasuredCallback {
        void onSizeMeasuredCallBack(int width, int height);
    }
}
