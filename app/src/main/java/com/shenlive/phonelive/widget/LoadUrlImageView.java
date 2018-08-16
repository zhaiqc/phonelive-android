package com.shenlive.phonelive.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.shenlive.phonelive.AppContext;
import com.shenlive.phonelive.utils.SimpleUtils;
import com.shenlive.phonelive.utils.StringUtils;

/**
 * 带图片加载的图片控件
 */
public class LoadUrlImageView extends ImageView {

    private int null_drawable = 0;
    public LoadUrlImageView(Context context) {
        super(context);
        init(context);
    }

    public int getNull_drawable() {
        return null_drawable;
    }

    public void setNull_drawable(int null_drawable) {
        this.null_drawable = null_drawable;
    }

    public LoadUrlImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    private void init(Context context) {


    }
    public LoadUrlImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void setImageLoadUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            setImageResource(null_drawable);
            return;
        }

        SimpleUtils.loadImageForView(AppContext.getInstance(),this,url,null_drawable);


    }
}
