package com.shenlive.phonelive.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 他人信息中心数据模型
 */
public class UserHomePageBean{


    /**
     * id : 122
     * user_nicename : ceshi333
     * avatar : http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg
     * avatar_thumb : http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg
     * sex : 0
     * signature : 1231312
     * consumption : 634617
     * votestotal : 1080174
     * province :
     * city : 泰安市
     * birthday : 2017-02-28
     * issuper : 0
     * level : 9
     * follows : 3
     * fans : 16
     * isattention : 1
     * isblack : 0
     * isblack2 : 0
     * contribute : [{"uid":"122","total":"628674","avatar":"http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg"},{"uid":"275","total":"272250","avatar":"http://live.yunbaozhibo.com/api/public/upload/avatar/275.png"},{"uid":"7521","total":"125478","avatar":"http://live.yunbaozhibo.com/api/public/upload/avatar/default.jpg"}]
     * liveinfo : {"uid":"122","avatar":"http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg","avatar_thumb":"http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg","user_nicename":"ceshi333","title":"未设置标题","islive":"1","city":"好像在火星","stream":"122_1487321391","pull":"rtmp://testlive.anbig.com/5showcam/122_1487321391"}
     * liverecord : [{"id":"227","uid":"122","nums":"0","starttime":"1487225397","endtime":"1487225404","title":"未设置标题","city":"好像在火星","datestarttime":"2017年02月16日 14:09","dateendtime":"2017年02月16日 14:10"},{"id":"226","uid":"122","nums":"0","starttime":"1487225098","endtime":"1487225114","title":"","city":"好像在火星","datestarttime":"2017年02月16日 14:04","dateendtime":"2017年02月16日 14:05"},{"id":"194","uid":"122","nums":"0","starttime":"1487140310","endtime":"1487140332","title":"","city":"好像在火星","datestarttime":"2017年02月15日 14:31","dateendtime":"2017年02月15日 14:32"},{"id":"193","uid":"122","nums":"0","starttime":"1487140194","endtime":"1487140212","title":"","city":"好像在火星","datestarttime":"2017年02月15日 14:29","dateendtime":"2017年02月15日 14:30"},{"id":"190","uid":"122","nums":"0","starttime":"1487139530","endtime":"1487139553","title":"","city":"好像在火星","datestarttime":"2017年02月15日 14:18","dateendtime":"2017年02月15日 14:19"},{"id":"189","uid":"122","nums":"0","starttime":"1487139431","endtime":"1487139449","title":"","city":"好像在火星","datestarttime":"2017年02月15日 14:17","dateendtime":"2017年02月15日 14:17"},{"id":"187","uid":"122","nums":"1","starttime":"1487139132","endtime":"1487139148","title":"","city":"好像在火星","datestarttime":"2017年02月15日 14:12","dateendtime":"2017年02月15日 14:12"},{"id":"176","uid":"122","nums":"1","starttime":"1487065394","endtime":"1487129339","title":"","city":"好像在火星","datestarttime":"2017年02月14日 17:43","dateendtime":"2017年02月15日 11:28"},{"id":"175","uid":"122","nums":"0","starttime":"1487064887","endtime":"1487064903","title":"","city":"好像在火星","datestarttime":"2017年02月14日 17:34","dateendtime":"2017年02月14日 17:35"},{"id":"174","uid":"122","nums":"1","starttime":"1487061626","endtime":"1487061675","title":"","city":"好像在火星","datestarttime":"2017年02月14日 16:40","dateendtime":"2017年02月14日 16:41"},{"id":"173","uid":"122","nums":"1","starttime":"1487061407","endtime":"1487061627","title":"","city":"好像在火星","datestarttime":"2017年02月14日 16:36","dateendtime":"2017年02月14日 16:40"},{"id":"170","uid":"122","nums":"1","starttime":"1487059771","endtime":"1487060125","title":"","city":"好像在火星","datestarttime":"2017年02月14日 16:09","dateendtime":"2017年02月14日 16:15"},{"id":"167","uid":"122","nums":"0","starttime":"1487059410","endtime":"1487059491","title":"","city":"好像在火星","datestarttime":"2017年02月14日 16:03","dateendtime":"2017年02月14日 16:04"},{"id":"165","uid":"122","nums":"1","starttime":"1487057348","endtime":"1487057604","title":"","city":"好像在火星","datestarttime":"2017年02月14日 15:29","dateendtime":"2017年02月14日 15:33"},{"id":"164","uid":"122","nums":"0","starttime":"1487056850","endtime":"1487057144","title":"","city":"好像在火星","datestarttime":"2017年02月14日 15:20","dateendtime":"2017年02月14日 15:25"},{"id":"163","uid":"122","nums":"0","starttime":"1487056829","endtime":"1487056847","title":"","city":"好像在火星","datestarttime":"2017年02月14日 15:20","dateendtime":"2017年02月14日 15:20"},{"id":"162","uid":"122","nums":"1","starttime":"1487056775","endtime":"1487056829","title":"","city":"好像在火星","datestarttime":"2017年02月14日 15:19","dateendtime":"2017年02月14日 15:20"},{"id":"161","uid":"122","nums":"1","starttime":"1487056560","endtime":"1487056775","title":"","city":"好像在火星","datestarttime":"2017年02月14日 15:16","dateendtime":"2017年02月14日 15:19"},{"id":"160","uid":"122","nums":"14","starttime":"1487051173","endtime":"1487056211","title":"","city":"好像在火星","datestarttime":"2017年02月14日 13:46","dateendtime":"2017年02月14日 15:10"},{"id":"159","uid":"122","nums":"1","starttime":"1487039726","endtime":"1487051115","title":"","city":"好像在火星","datestarttime":"2017年02月14日 10:35","dateendtime":"2017年02月14日 13:45"}]
     */

    @SerializedName("id")
    public String id;
    @SerializedName("user_nicename")
    public String user_nicename;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("avatar_thumb")
    public String avatar_thumb;
    @SerializedName("sex")
    public String sex;
    @SerializedName("signature")
    public String signature;
    @SerializedName("consumption")
    public String consumption;
    @SerializedName("votestotal")
    public String votestotal;
    @SerializedName("province")
    public String province;
    @SerializedName("city")
    public String city;
    @SerializedName("islive")
    public String islive;
    @SerializedName("birthday")
    public String birthday;
    @SerializedName("issuper")
    public String issuper;
    @SerializedName("level")
    public String level;
    @SerializedName("follows")
    public String follows;
    @SerializedName("fans")
    public String fans;
    @SerializedName("isattention")
    public String isattention;
    @SerializedName("isblack")
    public String isblack;
    @SerializedName("isblack2")
    public String isblack2;
    @SerializedName("liveinfo")
    public LiveJson liveinfo;
    @SerializedName("contribute")
    public List<ContributeBean> contribute;
    @SerializedName("liverecord")
    public List<LiveRecordBean> liverecord;



    public static class ContributeBean {
        /**
         * uid : 122
         * total : 628674
         * avatar : http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg
         */

        private String uid;
        private String total;
        private String avatar;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

}
