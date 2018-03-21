package com.shenlive.phonelive.game;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.shenlive.phonelive.bean.PrizeVo;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonlive.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽奖转盘
 * Created by bakumon on 16-11-12.
 */

public class LuckPan extends View {

    public int minCircleNum = 9; // 圈数
    public int maxCircleNum = 15;

    public long minOneCircleMillis = 400; // 平均一圈用时
    public long maxOneCircleMillis = 600;

    private List<PrizeVo> mPrizeVoList;
    private RectF mRectF;

    private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Context mContext;

    public LuckPan(Context context) {
        this(context, null);

    }

    public LuckPan(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckPan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        dPaint.setColor(Color.rgb(82, 182, 197));
        sPaint.setColor(Color.rgb(186, 226, 232));
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(TDevice.dpToPixel(16));
        mRectF = new RectF();
        mPrizeVoList = new ArrayList<>();
        PrizeVo prizeVo = new PrizeVo();
        prizeVo.id = "";
        prizeVo.rate = "";
        prizeVo.title = "";
        for (int i = 0; i < 20; i++) {
            mPrizeVoList.add(prizeVo);
        }
    }

    /**
     * 设置奖项实体集合
     *
     * @param prizeVoList 奖项实体集合
     */
    public void setPrizeVoList(List<PrizeVo> prizeVoList) {
        mPrizeVoList = prizeVoList;
        invalidate();
    }

    /**
     * 设置转盘交替的深色
     *
     * @param darkColor 深色 默认：Color.rgb(82, 182, 197)
     */
    public void setDarkColor(int darkColor) {
        dPaint.setColor(darkColor);
    }

    /**
     * 设置转盘交替的浅色
     *
     * @param shallowColor 浅色 默认：Color.rgb(186, 226, 232)
     */
    public void setShallowColor(int shallowColor) {
        sPaint.setColor(shallowColor);
    }

    /**
     * 设置转动圈数的范围
     *
     * @param minCircleNum 最小转动圈数
     * @param maxCircleNum 最大转动圈数
     */
    public void setCircleNumRange(int minCircleNum, int maxCircleNum) {
        if (minCircleNum > maxCircleNum) {
            return;
        }
        this.minCircleNum = minCircleNum;
        this.maxCircleNum = maxCircleNum;
    }

    /**
     * 设置平均转动一圈用时
     *
     * @param minOneCircleMillis 最小转动圈数
     * @param maxOneCircleMillis 最大转动圈数
     */
    public void setOneCircleMillisRange(int minOneCircleMillis, int maxOneCircleMillis) {
        if (minOneCircleMillis > maxOneCircleMillis)
            this.minOneCircleMillis = minOneCircleMillis;
        this.maxOneCircleMillis = maxOneCircleMillis;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //wrap_content value
        int mHeight = (int) TDevice.dpToPixel(TDevice.getScreenWidth());
        int mWidth = (int) TDevice.dpToPixel(TDevice.getScreenWidth());
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() ;
        int height = getHeight() ;
        int MinValue = Math.min(width, height);

        int radius = MinValue / 2;
        mRectF.set(getPaddingLeft(), getPaddingTop(), MinValue, MinValue);
        float sweepAngle = (float) (360.0 / mPrizeVoList.size());
        float angle = -90 - sweepAngle / 2;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zp_dp1);
        canvas.drawBitmap(bitmap, null, mRectF, null);

        for (PrizeVo prizeVo : mPrizeVoList) {
            int imgWidth = radius / 4;
            float angle1 = (float) Math.toRadians(360/mPrizeVoList.size() + angle-360/mPrizeVoList.size()/2);

            float x = (float) (width/2 + radius / 1.4 * Math.cos(angle1));
            float y = (float) (height/2 + radius / 1.4 * Math.sin(angle1));

            // 确定绘制图片的位置
            RectF rect = new RectF(x - (float)(imgWidth * 2 / 4), y - imgWidth * 2 / 4, x + imgWidth
                    * 2 / 4, y + imgWidth * 2 / 4);
            if (prizeVo.img!=null)
            canvas.drawBitmap(prizeVo.img,null, rect,null);
            angle += sweepAngle;
        }
    }

    private float startAngle; // 每次开始转的起始角度

    private OnLuckPanAnimatorEndListener mOnLuckPanAnimatorEndListener;

    public void setOnLuckPanAnimatorEndListener(OnLuckPanAnimatorEndListener listener) {
        this.mOnLuckPanAnimatorEndListener = listener;
    }

    public interface OnLuckPanAnimatorEndListener {
        void onLuckPanAnimatorEnd(PrizeVo choicePrizeVo);
    }

    /**
     * 开始转动 抽奖
     *
     * @param id 要停到对应奖项的 PrizeVo 实体的 id
     */
    public void start(final int id,int time) {
        int choiceIndex1 = 0;
        // 获取选择的id对应 在转盘中的位置 从0开始
        for (int i = 0; i < mPrizeVoList.size(); i++) {
            if (id == Integer.parseInt(mPrizeVoList.get(i).id)) {
                choiceIndex1 = i;
            }
        }
        final int choiceIndex = choiceIndex1;
        // minOneCircleMillis < 平均一圈用时 < maxOneCircleMillis
        long oneCircleTime = (long) (minOneCircleMillis + Math.random() * (maxOneCircleMillis - minOneCircleMillis + 1));
        // minCircleNum < 圈数 < maxCircleNum
        int ringNumber = (int) (minCircleNum + Math.random() * (maxCircleNum - minCircleNum + 1));

        float allAngle = 360 * ringNumber + (360f / mPrizeVoList.size()) * -choiceIndex;

        ObjectAnimator animator = ObjectAnimator.ofFloat(LuckPan.this, "rotation", -startAngle, allAngle);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(time);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startAngle = (360f / mPrizeVoList.size()) * choiceIndex;
                if (mOnLuckPanAnimatorEndListener != null) {
                    mOnLuckPanAnimatorEndListener.onLuckPanAnimatorEnd(mPrizeVoList.get(choiceIndex));
                }

            }
        });
        animator.start();

    }
}
