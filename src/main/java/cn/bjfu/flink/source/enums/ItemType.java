package cn.bjfu.flink.source.enums;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 4:30 下午
 */
public enum ItemType {
    sku_id("商品skuId"),
    keyword("搜索关键词"),
    sku_ids("多个商品skuId"),
    activity_id("活动id"),
    coupon_id("购物券id");



    String desc;

    ItemType(String desc){
        this.desc=desc;
    }
}
