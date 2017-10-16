package com.ylive.phonelive.game;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.ylive.phonelive.R;
import com.ylive.phonelive.ui.other.DrawableRes;
import com.ylive.phonelive.utils.SimpleUtils;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.TDevice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by weipeng on 2017/3/6.
 */

public class NiuZaiPokersLayout extends RelativeLayout {

    private Context mContext;
    private RelativeLayout mLlGameDq, mLlGameDc, mLlGameXq;
    private LinearLayout mLlGameContent;
    private RelativeLayout mRlGameTop, mRlRootView;
    private ImageView mIvEnd, mIvGameWinning;
    private TextView mTvCountDown;
    private TextView mTvBetting1, mTvBetting2, mTvBetting3;
    private TextView mTvTotalBetting1, mTvTotalBetting2, mTvTotalBetting3;
    private ImageView mIvSelectBettingBtn[];
    private TextView mTvCoin, mIvReady;


    private PokersGameControl.GameInterface mGameInterface;
    private RelativeLayout[] mPokersGroup;

    private Map<Integer, Integer> res = new HashMap<>();

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

    private int winningIndex;
    private Button mBtnGameStart,mBtnGameClose;

    public NiuZaiPokersLayout(Context context) {
        super(context);

        init(context);
    }

    public NiuZaiPokersLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NiuZaiPokersLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void initGameView() {

        removeView(mView);
        mView = View.inflate(mContext, R.layout.view_game_niuzai_pokers, null);

        addView(mView);
        initView(mView);

        if (mGameInterface != null) {
            mGameInterface.onInitGameView(4);
        }

    }

    private void init(Context ct) {

        mContext = ct;
        mView = View.inflate(ct, R.layout.view_game_niuzai_pokers, null);


        res.put(1, R.drawable.xing1);
        res.put(0, R.drawable.xingsan);
        res.put(2, R.drawable.xing2);
        res.put(3, R.drawable.xing3);
        res.put(4, R.drawable.xing4);
        res.put(5, R.drawable.xing5);
        res.put(6, R.drawable.xing6);
        res.put(7, R.drawable.xing7);
        res.put(8, R.drawable.xing8);
        res.put(9, R.drawable.xing9);
        res.put(10, R.drawable.xing10);
        res.put(11, R.drawable.xing11);
        res.put(12, R.drawable.xing12);
        res.put(13, R.drawable.xing13);
        res.put(14, R.drawable.xingman);

        initView(mView);

        addView(mView);

    }

    private void initView(View view) {

        mTvCountDown = (TextView) view.findViewById(R.id.tv_game_count_down);
        mLlGameDq = (RelativeLayout) view.findViewById(R.id.ll_game_dq);
        mLlGameDc = (RelativeLayout) view.findViewById(R.id.ll_game_center);
        mLlGameXq = (RelativeLayout) view.findViewById(R.id.ll_game_xq);
        mLlGameContent = (LinearLayout) view.findViewById(R.id.ll_game_content);
        mRlGameTop = (RelativeLayout) view.findViewById(R.id.rl_game_top);

        mTvBetting1 = (TextView) view.findViewById(R.id.tv_game_pokers_dq_2);
        mTvBetting2 = (TextView) view.findViewById(R.id.tv_game_pokers_dc_2);
        mTvBetting3 = (TextView) view.findViewById(R.id.tv_game_pokers_xq_2);

        mTvTotalBetting1 = (TextView) view.findViewById(R.id.tv_game_pokers_dq_1);
        mTvTotalBetting2 = (TextView) view.findViewById(R.id.tv_game_pokers_dc_1);
        mTvTotalBetting3 = (TextView) view.findViewById(R.id.tv_game_pokers_xq_1);

        mRlRootView = (RelativeLayout) view.findViewById(R.id.rl_game_root);
        mIvGameWinning = (ImageView) view.findViewById(R.id.iv_game_winning);
        mTvCoin = (TextView) view.findViewById(R.id.tv_game_coin);

        view.findViewById(R.id.rl_game_betting_1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(1, 4);
                }
            }
        });

        view.findViewById(R.id.rl_game_betting_2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(2, 4);
                }
            }
        });

        view.findViewById(R.id.rl_game_betting_3).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickBetting(3, 4);
                }
            }
        });

        //开始直播
        mBtnGameStart = (Button) view.findViewById(R.id.btn_game_start);

        mBtnGameClose = (Button) view.findViewById(R.id.btn_game_close);


        mBtnGameStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mBtnGameStart.setVisibility(GONE);

                if (mGameInterface != null) {
                    mGameInterface.onClickStartGame(4);
                }
            }
        });

        mBtnGameClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGameInterface != null) {
                    mGameInterface.onClickCloseGame(4);
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
                            mGameInterface.onSelectBettingNum(10, 4);
                        if (view.getId() == mIvSelectBettingBtn[1].getId())
                            mGameInterface.onSelectBettingNum(100, 4);
                        if (view.getId() == mIvSelectBettingBtn[2].getId())
                            mGameInterface.onSelectBettingNum(1000, 4);
                        if (view.getId() == mIvSelectBettingBtn[3].getId())
                            mGameInterface.onSelectBettingNum(10000, 4);
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
        findViewById(R.id.btn_game_start).setVisibility(isVisible ? VISIBLE : GONE);
    }
    //是否显示结束游戏按钮
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
            mLlGameDq.animate().scaleX(0.7f).scaleY(0.7f).translationY(100f).setDuration(0).start();
            mLlGameDc.animate().scaleX(0.7f).scaleY(0.7f).translationY(100f).setDuration(0).start();
            mLlGameXq.animate().scaleX(0.7f).scaleY(0.7f).translationY(100f).setDuration(0).start();

            mLlGameDq.getChildAt(0).setVisibility(VISIBLE);
            mLlGameDc.getChildAt(0).setVisibility(VISIBLE);
            mLlGameXq.getChildAt(0).setVisibility(VISIBLE);

            mLlGameDq.getChildAt(1).setVisibility(GONE);
            mLlGameDc.getChildAt(1).setVisibility(GONE);
            mLlGameXq.getChildAt(1).setVisibility(GONE);


            mLlGameContent.getChildAt(1).setVisibility(VISIBLE);
            mLlGameContent.getChildAt(3).setVisibility(VISIBLE);
            mLlGameContent.getChildAt(5).setVisibility(VISIBLE);

            mRlGameTop.setVisibility(VISIBLE);
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
        } else {
            mTvTotalBetting3.setText(SimpleUtils.formatNumbers(totalCoin));
            mTvBetting3.setText(SimpleUtils.formatNumbers(ownCoin));
        }

    }


    public void setOnGameListen(PokersGameControl.GameInterface gameListen) {

        this.mGameInterface = gameListen;
    }

    //开始游戏
    public void startGame() {

        if (mGameInterface != null) {
            mGameInterface.onStartGameCommit(1);
        }
        int ty = (int) TDevice.dpToPixel(35);
        mLlGameDq.animate().scaleX(0.7f).scaleY(0.7f).translationY(ty).setDuration(1000).start();
        mLlGameDc.animate().scaleX(0.7f).scaleY(0.7f).translationY(ty).setDuration(1000).start();
        mLlGameXq.animate().scaleX(0.7f).scaleY(0.7f).translationY(ty)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {


                        mLlGameDq.getChildAt(0).setVisibility(VISIBLE);
                        mLlGameDc.getChildAt(0).setVisibility(VISIBLE);
                        mLlGameXq.getChildAt(0).setVisibility(VISIBLE);

                        mLlGameDq.getChildAt(1).setVisibility(GONE);
                        mLlGameDc.getChildAt(1).setVisibility(GONE);
                        mLlGameXq.getChildAt(1).setVisibility(GONE);


                        mLlGameContent.getChildAt(1).setVisibility(VISIBLE);
                        mLlGameContent.getChildAt(3).setVisibility(VISIBLE);
                        mLlGameContent.getChildAt(5).setVisibility(VISIBLE);

                        mRlGameTop.setVisibility(VISIBLE);


                        startLicensing();
                    }
                }).setDuration(1000).start();
    }


    //开始发牌
    private void startLicensing() {

        if (mGameInterface != null) {
            mGameInterface.onStartLicensing(4);
        }


        mPokersGroup = new RelativeLayout[3];
        mPokersGroup[0] = (RelativeLayout) mRlGameTop.getChildAt(0);
        mPokersGroup[1] = (RelativeLayout) mRlGameTop.getChildAt(1);
        mPokersGroup[2] = (RelativeLayout) mRlGameTop.getChildAt(2);


        int margin = (int) TDevice.dpToPixel(12);
        for (int s = 0; s < 3; s++) {

            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_slice_in_right);
            animation.setDuration(150);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            mPokersGroup[s].setLayoutAnimation(new LayoutAnimationController(animation));
            int i = 0;

            while (i < 5) {
                ImageView pokers = new ImageView(mContext);
                LayoutParams params = new LayoutParams((int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(40));
                params.setMargins(i * margin, 10, 0, 0);

                pokers.setImageResource(R.drawable.poker_back_popbull);
                pokers.setLayoutParams(params);

                mPokersGroup[s].addView(pokers);

                i++;
            }
        }
        readyGame(0);
        if (mGameInterface != null) {
            mGameInterface.onStartCountDown(4);
        }
    }

    //开始支持
    public void readyGame(int i) {
        mIvReady = new TextView(mContext);
        if (i == 0) {
            mIvReady.setText(R.string.game_start_suporrt);
        } else if (i == 1) {
            mIvReady.setText(R.string.game_stop);
        }
        mIvReady.setTextColor(getResources().getColor(R.color.global));
        mIvReady.setBackgroundResource(R.drawable.dt);
        mIvReady.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) TDevice.dpToPixel(30));
        params.addRule(CENTER_HORIZONTAL);
        params.setMargins((int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(50), (int) TDevice.dpToPixel(35), 0);
        mIvReady.setLayoutParams(params);
        addView(mIvReady);
//        mIvReady.animate()
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

        new CountDownTimer(time * 1000, 1000) {

            @Override
            public void onTick(long l) {
                mTvCountDown.setText(String.valueOf((int) (l / 1000)));
            }

            @Override
            public void onFinish() {
                readyGame(1);
                if (mGameInterface != null) {
                    mGameInterface.onEndCountDown(4);
                }
            }

        }.start();
    }

    //显示结果
    public void showResult(final int index, String p1, String p2, String p3, String p4, String p5, int str, int isWinning) {

        if (isWinning == 1) {
            winningIndex = index;
        }

        if (index == 2) {
            mIvGameWinning.setVisibility(VISIBLE);

            int winImg[] = new int[]{R.drawable.bg_game_winning_left, R.drawable.bg_game_winning_center, R.drawable.bg_game_winning_right};

            mIvGameWinning.setImageResource(winImg[winningIndex]);
        }

        mTvCountDown.setVisibility(GONE);
        String p1Img[][] = new String[5][2];

        p1Img[0] = p1.split("-");
        p1Img[1] = p2.split("-");
        p1Img[2] = p3.split("-");
        p1Img[3] = p4.split("-");
        p1Img[4] = p5.split("-");

        if (mPokersGroup.length != 0)
            for (int s = 0; s < mPokersGroup[index].getChildCount(); s++) {
                int idx = (StringUtils.toInt(p1Img[s][0]) - 1) * 14 + StringUtils.toInt(p1Img[s][1]) - 1;
                ((ImageView) mPokersGroup[index].getChildAt(s)).setImageResource(DrawableRes.PokersImg[idx]);
            }


        ImageView name = new ImageView(mContext);

        LayoutParams params = new LayoutParams((int) TDevice.dpToPixel(80), (int) TDevice.dpToPixel(60));

        name.setImageResource(res.get(str));


        if (index == 0) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins((int) TDevice.dpToPixel(15), 0, 0, 0);
        } else if (index == 1) {
            params.addRule(CENTER_HORIZONTAL);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, (int) TDevice.dpToPixel(15), 0);
        }
        mRlGameTop.addView(name, params);

        ObjectAnimator animator = ObjectAnimator.ofFloat(name, "scaleX", 1f, 0.5f, 1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(name, "scaleY", 1f, 0.5f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator, animator2);
        animatorSet.setDuration(500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mGameInterface != null && index == 2) {
                    mGameInterface.onShowResultEnd(4);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();


    }


}
