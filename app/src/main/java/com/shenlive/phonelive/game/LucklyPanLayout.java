package com.shenlive.phonelive.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.shenlive.phonelive.bean.PrizeVo;
import com.shenlive.phonelive.utils.SimpleUtils;
import com.shenlive.phonelive.utils.StringUtils;
import com.shenlive.phonelive.utils.TDevice;
import com.shenlive.phonlive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.Gravity.CENTER;

/**
 * Created by weipeng on 2017/3/6.
 */

public class LucklyPanLayout extends RelativeLayout implements LuckPan.OnLuckPanAnimatorEndListener {

    private Context mContext;
    private ImageView mIvEnd;
    private TextView mTvCountDown;
    private TextView mTvBetting1, mTvBetting2, mTvBetting3, mTvBetting4;
    private TextView mTvTotalBetting1, mTvTotalBetting2, mTvTotalBetting3, mTvTotalBetting4;
    private ImageView mIvSelectBettingBtn[];
    private TextView mTvCoin, mIvReady;
    private LuckPan mLuckPan;
    private ImageView mIvStart, mLight, mWinImg;
    private List<Bitmap> bitmaps = new ArrayList<>();
    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        boolean blingbling = true;

        @Override
        public void run() {
            //do something
            //每隔1s循环执行run方法
            if (blingbling) {
                mLight.setBackgroundResource(R.drawable.deng2);
                blingbling = false;
            } else {
                mLight.setBackgroundResource(R.drawable.deng1);
                blingbling = true;
            }
            mHandler.postDelayed(this, 500);
        }
    };

    private PokersGameControl.GameInterface mGameInterface;
    private RelativeLayout[] mPokersGroup;

    private Map<Integer, Integer> res = new HashMap<>();
    private int[] images = new int[]{R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3, R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3};

    private int btiSelectNumImg[] = new int[]{
            R.drawable.icon_game_betting_10,
            R.drawable.icon_game_betting_100,
            R.drawable.icon_game_betting_1000,
            R.drawable.icon_game_betting_10000,
    };

    private int[] btiSelectNumImgFocus = new int[]{
            R.drawable.icon_game_betting_10_foucs,
            R.drawable.icon_game_betting_100_foucs,
            R.drawable.icon_game_betting_1000_foucs,
            R.drawable.icon_game_betting_10000_foucs,
    };

    private View mView;

    private Button mBtnGameClose;
    private ImageView mBtnGameStart;

    public LucklyPanLayout(Context context) {
        super(context);

        init(context);
    }

    public LucklyPanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LucklyPanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void initGameView() {

        removeView(mView);
        mView = View.inflate(mContext, R.layout.view_game_luckpan, null);

        addView(mView);
        initView(mView);

        if (mGameInterface != null) {
            mGameInterface.onInitGameView(3);
        }

    }

    private void init(Context ct) {
        mContext = ct;

        mView = View.inflate(ct, R.layout.view_game_luckpan, null);

        initView(mView);

        addView(mView);
    }

    private void initView(View view) {
        mTvCountDown = (TextView) view.findViewById(R.id.tv_game_count_down);
        for (int i = 0; i < 20; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), images[i]);
            bitmaps.add(bitmap);
        }
        mLuckPan = (LuckPan) view.findViewById(R.id.luck_pan);
        mLight = (ImageView) view.findViewById(R.id.luckpan_light);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) TDevice.getScreenWidth(), (int) TDevice.getScreenWidth());
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mLuckPan.setLayoutParams(layoutParams);
        mLight.setLayoutParams(layoutParams);
        mHandler.postDelayed(r, 500);//延时100毫秒
        List<PrizeVo> prizeVoList = new ArrayList<>();
        PrizeVo prizeVo;
        for (int i = 0; i < 20; i++) {
            prizeVo = new PrizeVo();
            prizeVo.id = i + "";
            prizeVo.rate = i + "";
            prizeVo.title = "×" + i;
            prizeVo.img = bitmaps.get(i);
            prizeVoList.add(prizeVo);
        }

        // 给转盘设置奖项集合
        mLuckPan.setPrizeVoList(prizeVoList);
        // 设置转盘交替的深色
        mLuckPan.setDarkColor(Color.rgb(82, 182, 197));
        // 设置转盘交替的浅色
        mLuckPan.setShallowColor(Color.rgb(186, 226, 232));
        // 给转盘设置动画结束的监听
        mLuckPan.setOnLuckPanAnimatorEndListener(this);
        // 设置转动圈数的范围
        mLuckPan.setCircleNumRange(9, 15);
        // 设置平均转动一圈用时
        mLuckPan.setOneCircleMillisRange(400, 600);


        mTvBetting1 = (TextView) view.findViewById(R.id.tv_game_betting_1);
        mTvBetting2 = (TextView) view.findViewById(R.id.tv_game_betting_2);
        mTvBetting3 = (TextView) view.findViewById(R.id.tv_game_betting_3);
        mTvBetting4 = (TextView) view.findViewById(R.id.tv_game_betting_4);

        mTvTotalBetting1 = (TextView) view.findViewById(R.id.tv_game_totalbetting_1);
        mTvTotalBetting2 = (TextView) view.findViewById(R.id.tv_game_totalbetting_2);
        mTvTotalBetting3 = (TextView) view.findViewById(R.id.tv_game_totalbetting_3);
        mTvTotalBetting4 = (TextView) view.findViewById(R.id.tv_game_totalbetting_4);

//        mIvGameWinning = (ImageView) view.findViewById(R.id.iv_game_winning);
        mTvCoin = (TextView) view.findViewById(R.id.tv_game_coin);

        view.findViewById(R.id.rl_game_betting_1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(1, 3);
                }
            }
        });

        view.findViewById(R.id.rl_game_betting_2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(2, 3);
                }
            }
        });

        view.findViewById(R.id.rl_game_betting_3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(3, 3);
                }
            }
        });
        view.findViewById(R.id.rl_game_betting_4).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(4, 3);
                }
            }
        });

        //开始直播
        mBtnGameStart = (ImageView) view.findViewById(R.id.btn_game_start);
        mBtnGameClose = (Button) view.findViewById(R.id.btn_game_close);

        mBtnGameStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickStartGame(3);
                }
                mBtnGameStart.setEnabled(false);
            }
        });


        mBtnGameClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickCloseGame(3);
                }
            }
        });

        mIvSelectBettingBtn = new ImageView[4];
        mIvSelectBettingBtn[0] = (ImageView) view.findViewById(R.id.iv_game_betting_1);
        mIvSelectBettingBtn[1] = (ImageView) view.findViewById(R.id.iv_game_betting_2);
        mIvSelectBettingBtn[2] = (ImageView) view.findViewById(R.id.iv_game_betting_3);
        mIvSelectBettingBtn[3] = (ImageView) view.findViewById(R.id.iv_game_betting_4);

        for (int i = 0; i < mIvSelectBettingBtn.length; i++) {

            mIvSelectBettingBtn[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (mGameInterface != null) {
                        if (view.getId() == mIvSelectBettingBtn[0].getId())
                            mGameInterface.onSelectBettingNum(10, 3);
                        if (view.getId() == mIvSelectBettingBtn[1].getId())
                            mGameInterface.onSelectBettingNum(100, 3);
                        if (view.getId() == mIvSelectBettingBtn[2].getId())
                            mGameInterface.onSelectBettingNum(1000, 3);
                        if (view.getId() == mIvSelectBettingBtn[3].getId())
                            mGameInterface.onSelectBettingNum(10000, 3);
                    }

                    for (int i = 0; i < mIvSelectBettingBtn.length; i++) {

                        if (view.getId() == mIvSelectBettingBtn[i].getId()) {
                            mIvSelectBettingBtn[i].setImageResource(btiSelectNumImgFocus[i]);
                        } else {
                            mIvSelectBettingBtn[i].setImageResource(btiSelectNumImg[i]);
                        }
                    }
                }
            });
        }

    }

    //是否显示开始游戏按钮
    public void setIsVisibleStartBtn(boolean isVisible) {
        findViewById(R.id.btn_game_start).setEnabled(isVisible);
    }

    public void setIsVisibleCloseBtn(boolean isVisible) {
        findViewById(R.id.btn_game_close).setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setIsVisibleBettingView(boolean isVisible) {
        findViewById(R.id.rl_game_betting).setVisibility(isVisible ? VISIBLE : GONE);

    }

    public void setIsVisibleStartWords() {
        findViewById(R.id.tv_game_start).animate().scaleX(0.4f).scaleY(0.4f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.tv_game_start).setVisibility(GONE);
            }
        }).start();
    }


    //游戏进行中
    public void setGameStatusOnHaveInHand(String downCount) {

        startLicensing();
        startCountDown(StringUtils.toInt(downCount));

    }

    //下注修改数量
    public void changeBettingCoin(int index, int ownCoin, int totalCoin) {

        if (index == 1) {
            mTvTotalBetting1.setText(SimpleUtils.formatNumbers(totalCoin));
            mTvBetting1.setText(SimpleUtils.formatNumbers(ownCoin));
        } else if (index == 2) {
            mTvTotalBetting2.setText(SimpleUtils.formatNumbers(totalCoin));
            mTvBetting2.setText(SimpleUtils.formatNumbers(ownCoin));
        } else if (index == 3) {
            mTvTotalBetting3.setText(SimpleUtils.formatNumbers(totalCoin));
            mTvBetting3.setText(SimpleUtils.formatNumbers(ownCoin));
        } else {
            mTvTotalBetting4.setText(SimpleUtils.formatNumbers(totalCoin));
            mTvBetting4.setText(SimpleUtils.formatNumbers(ownCoin));
        }

    }


    public void setOnGameListen(PokersGameControl.GameInterface gameListen) {

        this.mGameInterface = gameListen;
    }

    //开始游戏
    public void startGame() {

        if (mGameInterface != null) {
            mGameInterface.onStartGameCommit(3);
        }
        startLicensing();
    }


    //开始发牌
    private void startLicensing() {


        if (mGameInterface != null) {
            mGameInterface.onStartLicensing(3);
        }
        if (mGameInterface != null) {
            mGameInterface.onStartCountDown(3);
        }


    }

    //揭晓结果
    public void readyGame(int i) {
        mIvReady = new TextView(mContext);
        if (i == 0) {
            mIvReady.setText(R.string.game_start_suporrt);
        }
        if (i == 1) {
            mIvReady.setText(R.string.game_stop);
        }
        mIvReady.setTextColor(getResources().getColor(R.color.global));
        mIvReady.setBackgroundResource(R.drawable.dt);
        mIvReady.setGravity(CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TDevice.dpToPixel(30));
        params.addRule(CENTER_HORIZONTAL);
        params.setMargins((int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(50), (int) TDevice.dpToPixel(35), 0);
        mIvReady.setLayoutParams(params);
        addView(mIvReady);
        final ScaleAnimation animation = new ScaleAnimation(0.0f, 1f, 0.0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);//设置动画持续时间
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        mIvReady.startAnimation(animation);

        mIvReady.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScaleAnimation animation1 = new ScaleAnimation(1f, 0f, 1f, 0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation1.setDuration(1000);//设置动画持续时间
                animation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                mIvReady.startAnimation(animation1);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        removeView(mIvReady);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }, 2000);
    }

    public void setCoin(String coin) {
        mTvCoin.setText(coin);
    }

    public void setOnRechargeClick(OnClickListener click) {
        mView.findViewById(R.id.tv_game_recharge).setOnClickListener(click);
    }

    //揭晓结果
    public void resultGame() {

        mIvEnd = new ImageView(mContext);
        mIvEnd.setImageResource(R.drawable.icon_start_game);
        LayoutParams params = new LayoutParams((int) TDevice.dpToPixel(200), (int) TDevice.dpToPixel(100));
        params.addRule(CENTER_IN_PARENT);
        mIvEnd.setLayoutParams(params);
        addView(mIvEnd);
    }

    //开始倒计时
    public void startCountDown(int time) {

        mTvCountDown.setVisibility(VISIBLE);
        mLuckPan.start(0, time * 1000);
        new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long l) {
                mTvCountDown.setText(String.valueOf((int) (l / 1000)));
            }

            @Override
            public void onFinish() {
                readyGame(1);
                if (mGameInterface != null) {
                    mGameInterface.onEndCountDown(3);
                }
            }

        }.start();
    }

    //显示结果
    public void showResult(final int index) {
        mLuckPan.start(index - 1, 6000);
        final int winImg[] = new int[]{R.drawable.card_0, R.drawable.card_1, R.drawable.card_2, R.drawable.card_3};
        mTvCountDown.setVisibility(GONE);
        final ScaleAnimation animation = new ScaleAnimation(0.0f, 1f, 0.0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);//设置动画持续时间
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWinImg = new ImageView(mContext);
                mWinImg.setImageResource(winImg[index - 1]);
                mWinImg.setBackgroundResource(R.drawable.sel);
                LayoutParams params = new LayoutParams((int) TDevice.dpToPixel(200), (int) TDevice.dpToPixel(100));
                params.addRule(ALIGN_PARENT_TOP);
                params.addRule(CENTER_HORIZONTAL);
                mWinImg.setLayoutParams(params);
                addView(mWinImg);
                mWinImg.startAnimation(animation);

                mWinImg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ScaleAnimation animation1 = new ScaleAnimation(1f, 0f, 1f, 0f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        animation1.setDuration(1000);//设置动画持续时间
                        animation1.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                        mWinImg.startAnimation(animation1);
                        animation1.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                removeView(mWinImg);
                                if (mGameInterface != null) {
                                    mGameInterface.onShowResultEnd(3);
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                }, 2000);
            }
        }, 6000);


    }


    @Override
    public void onLuckPanAnimatorEnd(PrizeVo choicePrizeVo) {
        mBtnGameStart.setEnabled(true);
    }
}
