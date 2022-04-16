package cn.bjfu.flink.source.config;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:14 下午
 * 配置类
 */
public class AppConfig {

    public final static Integer mock_count = Integer.MAX_VALUE;

    //是否产生数据倾斜 1、是、0否; 目前是 mid、skuid(商品明细) 产生倾斜
    public final static String mock_skew = "1";

    //设备最大值
    public final static Integer max_mid = 1000000;

    public final static Integer max_uid = 20000000;

    //商品详情来源的占比：用户查询、商品推广、智能推荐、促销活动
    public final static Integer[] sourceTypeRate = new Integer[]{40, 25, 15, 20};

    //搜索关键词
    public static String [] searchKeywords =  new String[]{"图书", "小米", "iphone11", "电视", "口红", "ps5", "苹果手机", "小米盒子"};

    //商品最大值
    public final static Integer max_sku_id = 100000;

    public final static Integer max_activity_count = 2;

    public final static Integer max_pos_id = 5;

    //最大曝光数
    public static Integer max_display_count = 10;

    //最小曝光数
    public static Integer min_display_count = 4;

    //添加收藏率 百分比
    public static Integer if_favor_rate = 30;

    //取消收藏率 百分比
    public static Integer if_favor_cancel_rate = 10;

    //添加购物车率 百分比
    public static Integer if_cart_rate = 10;

    //增加购物车商品数量率 百分比
    public static Integer if_cart_add_num_rate = 10;

    //减少购物车商品数量率 百分比
    public static Integer if_cart_minus_num_rate = 10;

    //删除购物车率 百分比
    public static Integer if_cart_rm_rate = 10;

    //增加收货地址率 百分比
    public static Integer if_add_address = 15;

    //领取优惠券率 百分比
    public static Integer if_get_coupon = 25;

    //购物券最大id
    public static Integer max_coupon_id = 30;

    //错误概率 百分比
    public static Integer error_rate = 3;

    //页面最大访问时间
    public static Integer page_during_max_ms = 20000;

    //每条日志发送延迟 ms
    public static Integer log_sleep = 100;

    public static String mock_date = "2021-11-11";

}
