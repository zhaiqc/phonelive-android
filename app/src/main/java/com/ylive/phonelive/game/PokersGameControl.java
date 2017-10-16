package com.ylive.phonelive.game;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.ylive.phonelive.AppContext;
import com.ylive.phonelive.api.remote.ApiUtils;
import com.ylive.phonelive.api.remote.PhoneLiveApi;
import com.ylive.phonelive.bean.UserBean;
import com.ylive.phonelive.ui.other.ChatServer;
import com.ylive.phonelive.utils.StringUtils;
import com.ylive.phonelive.utils.UIHelper;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;

/**
 * Created by weipeng on 2017/3/7.
 */

public class PokersGameControl {

    private String mJinhuaToken, mGameId, mGameTime;

    private JinHuaPokersLayout mJinHuaPokersLayout;
    private HaiDaoPokersLayout mHaiDaoPokers;
    private LucklyPanLayout mLucklyPanLayout;
    private NiuZaiPokersLayout mNiuZaiPokersLayout;
    private GameInterface mGameInterface;

    public static final int POKERS_OPEN_VIEW = 1;//打开
    public static final int POKERS_START_GAME = 2;//开始发牌
    public static final int POKERS_CLOSE_GAME = 3;//关闭游戏
    public static final int POKERS_COUNT_DOWN = 4;//倒计时
    public static final int POKERS_BETTING_GAME = 5;//下注
    public static final int POKERS_RESULT_GAME = 6;//开奖
    public static final int POKERS_HAVE_HAND_GAME = 7;//进行中
    public int mBettingCoin = 10;
    public int mGameStatus = 0;


    private int betting1Total = 0, betting2Total = 0, betting3Total = 0, betting4Total = 0, betting1 = 0, betting2 = 0, betting3 = 0, betting4 = 0;

    private boolean isEmcee;

    public PokersGameControl(JinHuaPokersLayout jinHuaPokersLayout, LucklyPanLayout lucklyPanLayout, HaiDaoPokersLayout haiDaoPokers, NiuZaiPokersLayout niuZaiPokersLayout, Context context, boolean isemcee) {
        if (jinHuaPokersLayout != null) {
            mJinHuaPokersLayout = jinHuaPokersLayout;
            this.isEmcee = isemcee;
            setOnRechargeClick(context, 1);

            mJinHuaPokersLayout.setIsVisibleStartBtn(isEmcee);
            mJinHuaPokersLayout.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 1);
        }
        if (haiDaoPokers != null) {
            mHaiDaoPokers = haiDaoPokers;
            this.isEmcee = isemcee;
            setOnRechargeClick(context, 2);

            mHaiDaoPokers.setIsVisibleStartBtn(isEmcee);
            mHaiDaoPokers.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 2);
        }
        if (lucklyPanLayout != null) {
            mLucklyPanLayout = lucklyPanLayout;
            this.isEmcee = isemcee;
            setOnRechargeClick(context, 3);

            mLucklyPanLayout.setIsVisibleStartBtn(isEmcee);
            mLucklyPanLayout.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 3);
        }
        if (niuZaiPokersLayout != null) {
            mNiuZaiPokersLayout = niuZaiPokersLayout;
            this.isEmcee = isemcee;
            setOnRechargeClick(context, 4);
            mNiuZaiPokersLayout.setIsVisibleStartBtn(isEmcee);
            mNiuZaiPokersLayout.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 4);
        }
    }


    public void initGameView(Context context, int i) {
        mGameStatus = 0;
        mBettingCoin = 10;
        betting1Total = 0;
        betting2Total = 0;
        betting3Total = 0;
        betting1 = 0;
        betting2 = 0;
        betting3 = 0;
        mJinhuaToken = "0";
        mGameId = "0";
        mGameTime = "0";

        if (i == 1) {
            mJinHuaPokersLayout.initGameView();
            mJinHuaPokersLayout.setIsVisibleStartBtn(isEmcee);
            mJinHuaPokersLayout.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 1);
        } else if (i == 3) {
            mLucklyPanLayout.initGameView();
            mLucklyPanLayout.setIsVisibleStartBtn(isEmcee);
            mLucklyPanLayout.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 3);
        } else if (i == 2) {
            mHaiDaoPokers.initGameView();
            mHaiDaoPokers.setIsVisibleStartBtn(isEmcee);
            mHaiDaoPokers.setIsVisibleCloseBtn(isEmcee);
            setIsVisibleBettingView(false, 2);

        } else if (i == 4) {
            mNiuZaiPokersLayout.initGameView();
            mNiuZaiPokersLayout.setIsVisibleStartBtn(isEmcee);
            setIsVisibleBettingView(false, 4);
        }
        setOnRechargeClick(context, i);
    }

    //开始游戏(主播)
    public void startGame(String liveuid, String stream, String token, int i) {
        if (mGameStatus == POKERS_START_GAME) {
            return;
        }
        if (i == 1) {
            setIsVisibleStartWords(i);
            if (mGameInterface != null) {
                mJinHuaPokersLayout.setOnGameListen(mGameInterface);
            }
        } else if (i == 3) {
            if (mGameInterface != null) {
                mLucklyPanLayout.setOnGameListen(mGameInterface);
            }
        } else if (i == 2) {
            setIsVisibleStartWords(i);
            if (mGameInterface != null) {
                mHaiDaoPokers.setOnGameListen(mGameInterface);
            }
        } else if (i == 4) {
            setIsVisibleStartWords(i);
            if (mGameInterface != null) {
                mNiuZaiPokersLayout.setOnGameListen(mGameInterface);
            }
        }
        requestStartGame(liveuid, stream, token, i);

        mGameStatus = POKERS_START_GAME;
    }

    //是否显示下注栏
    public void setIsVisibleBettingView(boolean isvisible, int i) {
        if (i == 1) {
            mJinHuaPokersLayout.setIsVisibleBettingView(isvisible);
        } else if (i == 3) {
            mLucklyPanLayout.setIsVisibleBettingView(isvisible);
        } else if (i == 2) {
            mHaiDaoPokers.setIsVisibleBettingView(isvisible);
        } else if (i == 4) {
            mNiuZaiPokersLayout.setIsVisibleBettingView(isvisible);
        }

    }

    //是否显示开始提示
    public void setIsVisibleStartWords(int i) {
        if (i == 1) {
            mJinHuaPokersLayout.setIsVisibleStartWords();
        } else if (i == 3) {

        } else if (i == 2) {
            mHaiDaoPokers.setIsVisibleStartWords();
        } else if (i == 4) {
            mNiuZaiPokersLayout.setIsVisibleStartWords();
        }
    }

    //开始游戏
    public void startGame(int i) {

        if (mGameStatus == POKERS_START_GAME || mGameStatus == POKERS_HAVE_HAND_GAME) {

            if (mGameStatus == POKERS_HAVE_HAND_GAME) mGameStatus = POKERS_START_GAME;

            return;
        }
        if (i == 1) {
            if (mJinHuaPokersLayout.getVisibility() == View.GONE) {
                mJinHuaPokersLayout.setVisibility(View.VISIBLE);
            }
            mJinHuaPokersLayout.startGame();
            setIsVisibleStartWords(i);
        } else if (i == 3) {
            if (mLucklyPanLayout.getVisibility() == View.GONE) {
                mLucklyPanLayout.setVisibility(View.VISIBLE);
            }
            mLucklyPanLayout.startGame();
        } else if (i == 2) {
            if (mHaiDaoPokers.getVisibility() == View.GONE) {
                mHaiDaoPokers.setVisibility(View.VISIBLE);
            }
            mHaiDaoPokers.startGame();
            setIsVisibleStartWords(i);
        } else if (i == 4) {
            if (mNiuZaiPokersLayout.getVisibility() == View.GONE) {
                mNiuZaiPokersLayout.setVisibility(View.VISIBLE);
            }
            mNiuZaiPokersLayout.startGame();
            setIsVisibleStartWords(i);
        }
        mGameStatus = POKERS_START_GAME;

    }

    //开始倒计时
    public void startCountDown(int i) {

        if (mGameStatus == POKERS_COUNT_DOWN || mGameStatus == POKERS_HAVE_HAND_GAME) {

            if (mGameStatus == POKERS_HAVE_HAND_GAME) mGameStatus = POKERS_COUNT_DOWN;

            return;
        }
        if (i == 1) {
            mJinHuaPokersLayout.startCountDown(StringUtils.toInt(mGameTime));
        } else if (i == 3) {
            mLucklyPanLayout.startCountDown(StringUtils.toInt(mGameTime));
        } else if (i == 2) {
            mHaiDaoPokers.startCountDown(StringUtils.toInt(mGameTime));
        } else if (i == 4) {
            mNiuZaiPokersLayout.startCountDown(StringUtils.toInt(mGameTime));
        }
        mGameStatus = POKERS_COUNT_DOWN;
    }

    //游戏进行中
    public void setGameStatusOnHaveInHand(int i) {

        mGameStatus = POKERS_HAVE_HAND_GAME;
        if (i == 1) {
            if (mJinHuaPokersLayout != null) {
                mJinHuaPokersLayout.setGameStatusOnHaveInHand(mGameTime);
                mJinHuaPokersLayout.changeBettingCoin(1, 0, betting1Total);
                mJinHuaPokersLayout.changeBettingCoin(2, 0, betting2Total);
                mJinHuaPokersLayout.changeBettingCoin(3, 0, betting3Total);
                setIsVisibleStartWords(1);
            }

        }
        if (i == 3) {
            if (mLucklyPanLayout != null) {
                mLucklyPanLayout.setGameStatusOnHaveInHand(mGameTime);
                mLucklyPanLayout.changeBettingCoin(1, 0, betting1Total);
                mLucklyPanLayout.changeBettingCoin(2, 0, betting2Total);
                mLucklyPanLayout.changeBettingCoin(3, 0, betting3Total);
                mLucklyPanLayout.changeBettingCoin(4, 0, betting3Total);
                setIsVisibleStartWords(3);
            }
        }
        if (i == 2) {
            if (mHaiDaoPokers != null) {
                mHaiDaoPokers.setGameStatusOnHaveInHand(mGameTime);
                mHaiDaoPokers.changeBettingCoin(1, 0, betting1Total);
                mHaiDaoPokers.changeBettingCoin(2, 0, betting2Total);
                mHaiDaoPokers.changeBettingCoin(3, 0, betting3Total);
                setIsVisibleStartWords(2);
            }
        }
        if (i == 4) {
            if (mNiuZaiPokersLayout != null) {
                mNiuZaiPokersLayout.setGameStatusOnHaveInHand(mGameTime);
                mNiuZaiPokersLayout.changeBettingCoin(1, 0, betting1Total);
                mNiuZaiPokersLayout.changeBettingCoin(2, 0, betting2Total);
                mNiuZaiPokersLayout.changeBettingCoin(3, 0, betting3Total);
                setIsVisibleStartWords(4);
            }
        }


    }

    //揭晓游戏结果
    public void openGameResult(String res, Handler handler, int h) {

        if (mGameStatus == POKERS_RESULT_GAME) {
            return;
        }
        mGameStatus = POKERS_RESULT_GAME;
        try {
            JSONArray result = new JSONArray(res);
            if (h == 1 ) {
                for (int i = 0; i < result.length(); i++) {
                    JSONArray a = result.getJSONArray(i);
                    postShowResult(i, a, handler, h);
                }
            } else if (h == 3) {
                postShowResult(result.getInt(0), result, handler, h);
            } else if (h == 2||h==4) {
                for (int i = 0; i < result.length(); i++) {
                    JSONArray a = result.getJSONArray(i);
                    postShowResult(i, a, handler, h);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //设置游戏监听器
    public void setOnGameListen(GameInterface gameListen) {

        this.mGameInterface = gameListen;

        if (mGameInterface != null) {
            mJinHuaPokersLayout.setOnGameListen(mGameInterface);
        }
        if (mGameInterface != null) {
            mLucklyPanLayout.setOnGameListen(mGameInterface);
        }
        if (mGameInterface != null) {
            mHaiDaoPokers.setOnGameListen(mGameInterface);
        }
        if (mGameInterface != null) {
            mNiuZaiPokersLayout.setOnGameListen(mGameInterface);
        }
    }


    //延时执行显示结果
    private void postShowResult(final int index, final JSONArray data, final Handler handler, int i) {

        if (handler == null) return;
        if (i == 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mJinHuaPokersLayout.showResult(index, data.getString(0), data.getString(1), data.getString(2), data.getInt(6), data.getInt(3));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, index * 1000 * 2);
        } else if (i == 3) {
            mLucklyPanLayout.showResult(index);
        } else if (i == 2) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHaiDaoPokers.showResult(index, data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getInt(5), data.getInt(7));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, index * 1000 * 2);
        } else if (i == 4) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mNiuZaiPokersLayout.showResult(index, data.getString(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getInt(5), data.getInt(7));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, index * 1000 * 2);
        }

    }


    //下注修改数量
    public void changeBettingCoin(String muid, String uid, int index, int coin, int i) {
        if (i == 1) {
            if (index == 1) {
                if (uid.equals(muid)) {
                    betting1 += coin;
                }
                betting1Total += coin;
                mJinHuaPokersLayout.changeBettingCoin(index, betting1, betting1Total);
            } else if (index == 2) {

                if (uid.equals(muid)) {
                    betting2 += coin;
                }
                betting2Total += coin;
                mJinHuaPokersLayout.changeBettingCoin(index, betting2, betting2Total);
            } else {

                if (uid.equals(muid)) {
                    betting3 += coin;
                }
                betting3Total += coin;
                mJinHuaPokersLayout.changeBettingCoin(index, betting3, betting3Total);
            }
        } else if (i == 3) {
            if (index == 1) {
                if (uid.equals(muid)) {
                    betting1 += coin;
                }
                betting1Total += coin;
                mLucklyPanLayout.changeBettingCoin(index, betting1, betting1Total);
            } else if (index == 2) {

                if (uid.equals(muid)) {
                    betting2 += coin;
                }
                betting2Total += coin;
                mLucklyPanLayout.changeBettingCoin(index, betting2, betting2Total);
            } else if (index == 3) {
                if (uid.equals(muid)) {
                    betting3 += coin;
                }
                betting3Total += coin;
                mLucklyPanLayout.changeBettingCoin(index, betting3, betting3Total);
            } else {
                if (uid.equals(muid)) {
                    betting4 += coin;
                }
                betting4Total += coin;
                mLucklyPanLayout.changeBettingCoin(index, betting4, betting4Total);
            }
        } else if (i == 2) {
            if (index == 1) {
                if (uid.equals(muid)) {
                    betting1 += coin;
                }
                betting1Total += coin;
                mHaiDaoPokers.changeBettingCoin(index, betting1, betting1Total);
            } else if (index == 2) {

                if (uid.equals(muid)) {
                    betting2 += coin;
                }
                betting2Total += coin;
                mHaiDaoPokers.changeBettingCoin(index, betting2, betting2Total);
            } else {

                if (uid.equals(muid)) {
                    betting3 += coin;
                }
                betting3Total += coin;
                mHaiDaoPokers.changeBettingCoin(index, betting3, betting3Total);
            }
        } else if (i == 4) {
            if (index == 1) {
                if (uid.equals(muid)) {
                    betting1 += coin;
                }
                betting1Total += coin;
                mNiuZaiPokersLayout.changeBettingCoin(index, betting1, betting1Total);
            } else if (index == 2) {

                if (uid.equals(muid)) {
                    betting2 += coin;
                }
                betting2Total += coin;
                mNiuZaiPokersLayout.changeBettingCoin(index, betting2, betting2Total);
            } else {

                if (uid.equals(muid)) {
                    betting3 += coin;
                }
                betting3Total += coin;
                mNiuZaiPokersLayout.changeBettingCoin(index, betting3, betting3Total);
            }
        }


    }

    //开始游戏
    private void requestStartGame(String liveuid, String stream, String token, int i) {
        if (i == 1) {
            PhoneLiveApi.requestStartGame(liveuid, stream, token, new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {

                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        try {
                            mJinhuaToken = res.getJSONObject(0).getString("token");
                            mGameId = res.getJSONObject(0).getString("gameid");
                            mGameTime = res.getJSONObject(0).getString("time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mJinHuaPokersLayout.startGame();
                    }
                }
            });
        } else if (i == 3) {
            PhoneLiveApi.requestStartPanGame(liveuid, stream, token, new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {

                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        try {
                            mJinhuaToken = res.getJSONObject(0).getString("token");
                            mGameId = res.getJSONObject(0).getString("gameid");
                            mGameTime = res.getJSONObject(0).getString("time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mLucklyPanLayout.startGame();
                    }
                }
            });
        } else if (i == 2) {
            PhoneLiveApi.requestStartHaidaoGame(liveuid, stream, token, new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {

                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        try {
                            mJinhuaToken = res.getJSONObject(0).getString("token");
                            mGameId = res.getJSONObject(0).getString("gameid");
                            mGameTime = res.getJSONObject(0).getString("time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mHaiDaoPokers.startGame();
                    }
                }
            });
        } else if (i == 4) {
            PhoneLiveApi.requestStartNiuZaiGame(liveuid, stream, token, new StringCallback() {

                @Override
                public void onError(Call call, Exception e, int id) {

                }
                @Override
                public void onResponse(String response, int id) {

                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if (res != null) {
                        try {
                            mJinhuaToken = res.getJSONObject(0).getString("token");
                            mGameId = res.getJSONObject(0).getString("gameid");
                            mGameTime = res.getJSONObject(0).getString("time");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mNiuZaiPokersLayout.startGame();
                    }
                }
            });
        }
    }
    //下注
    public void requestBetting(final int index, final UserBean user, final ChatServer chatServer, final int i) {
        if (i == 1) {
            PhoneLiveApi.requestBetting(mGameId, mBettingCoin,
                    index, user.id, user.token, new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }
                        @Override
                        public void onResponse(String response, int id) {
                            JSONArray data = ApiUtils.checkIsSuccess(response);
                            if (data != null) {
                                chatServer.doSendBetting(String.valueOf(mBettingCoin), String.valueOf(index), user);
                                try {
                                    String coin = data.getJSONObject(0).getString("coin");
                                    setCoin(coin);
                                    UserBean u = AppContext.getInstance().getLoginUser();
                                    u.coin = coin;
                                    AppContext.getInstance().saveUserInfo(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else if (i == 3) {
            PhoneLiveApi.requestPanBetting(mGameId, mBettingCoin,
                    index, user.id, user.token, new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            JSONArray data = ApiUtils.checkIsSuccess(response);

                            if (data != null) {
                                chatServer.doSendPanBetting(String.valueOf(mBettingCoin), String.valueOf(index), user);
                                try {

                                    String coin = data.getJSONObject(0).getString("coin");

                                    setCoin(coin);
                                    UserBean u = AppContext.getInstance().getLoginUser();
                                    u.coin = coin;

                                    AppContext.getInstance().saveUserInfo(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else if (i == 2) {
            PhoneLiveApi.requestHaiDaoBetting(mGameId, mBettingCoin,
                    index, user.id, user.token, new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            JSONArray data = ApiUtils.checkIsSuccess(response);

                            if (data != null) {
                                chatServer.doSendHaiDaoBetting(String.valueOf(mBettingCoin), String.valueOf(index), user);
                                try {

                                    String coin = data.getJSONObject(0).getString("coin");

                                    setCoin(coin);
                                    UserBean u = AppContext.getInstance().getLoginUser();
                                    u.coin = coin;

                                    AppContext.getInstance().saveUserInfo(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else if (i == 4) {
            PhoneLiveApi.requestNiuZaiBetting(mGameId, mBettingCoin,
                    index, user.id, user.token, new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            JSONArray data = ApiUtils.checkIsSuccess(response);

                            if (data != null) {
                                chatServer.doSendNiuZaiBetting(String.valueOf(mBettingCoin), String.valueOf(index), user);
                                try {

                                    String coin = data.getJSONObject(0).getString("coin");

                                    setCoin(coin);
                                    UserBean u = AppContext.getInstance().getLoginUser();
                                    u.coin = coin;

                                    AppContext.getInstance().saveUserInfo(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    //关闭或者打开游戏
    public void closeGame(final ChatServer chatServer, final UserBean user, String stream, int i) {
        if (mGameStatus != 0) {
            Toast.makeText(AppContext.getInstance(), "请等待当前游戏结束", Toast.LENGTH_SHORT).show();
            return;
        }
        if (i == 1) {
            chatServer.doSendEndGame(user);
        } else if (i == 3) {
            chatServer.doSendEndPanGame(user);
        } else if (i == 2) {
            chatServer.doSendEndHaiDaoGame(user);
        } else if (i == 4) {
            chatServer.doSendEndNiuZaiGame(user);
        }
    }

    public void setCoin(String coin) {
        if (mJinHuaPokersLayout != null) {
            mJinHuaPokersLayout.setCoin(coin);
        }
        if (mHaiDaoPokers != null) {
            mHaiDaoPokers.setCoin(coin);
        }
        if (mLucklyPanLayout != null) {
            mLucklyPanLayout.setCoin(coin);
        }
    }

    public void setOnRechargeClick(final Context context, int i) {
        if (i == 1) {
            mJinHuaPokersLayout.setOnRechargeClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.showMyDiamonds(context);
                }
            });
        } else if (i == 3) {
            mLucklyPanLayout.setOnRechargeClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.showMyDiamonds(context);
                }
            });
        } else if (i == 2) {
            mHaiDaoPokers.setOnRechargeClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.showMyDiamonds(context);
                }
            });
        }
    }


    public int getGameStatus() {
        return mGameStatus;
    }
    public void setGameStatus(int i) {
        mGameStatus=i;
    }

    public String getJinhuaToken() {
        return mJinhuaToken;
    }

    public String getGameId() {
        return mGameId;
    }

    public void setGameId(String gameId) {
        mGameId = gameId;
    }

    public String getGameTime() {
        return mGameTime;
    }

    public void setGameTime(String gameTime) {
        mGameTime = gameTime;
    }

    public void setBettingCoin(int bettingCoin) {
        mBettingCoin = bettingCoin;
    }

    public int getBettingCoin() {
        return mBettingCoin;
    }


    public void setBetting2Total(int betting2Total) {
        this.betting2Total = betting2Total;
    }


    public void setBetting4Total(int betting4Total) {
        this.betting4Total = betting4Total;
    }


    public void setBetting4(int betting4) {
        this.betting4 = betting4;
    }

    public void setBetting3Total(int betting3Total) {
        this.betting3Total = betting3Total;
    }

    public void setBetting1Total(int betting1Total) {
        this.betting1Total = betting1Total;
    }

    public void setEmcee(boolean emcee) {
        isEmcee = emcee;
    }

    public static interface GameInterface {
        void onStartLicensing(int i);

        void onStartCountDown(int i);

        void onEndCountDown(int i);

        void onClickBetting(int index, int i);

        void onShowResultEnd(int i);

        void onClickStartGame(int i);

        void onClickCloseGame(int i);

        void onStartGameCommit(int i);

        void onSelectBettingNum(int coin, int i);

        void onInitGameView(int i);
    }
}
