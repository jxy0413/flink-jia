package cn.bjfu.flink.source.enums;

import lombok.AllArgsConstructor;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 4:32 下午
 */
@AllArgsConstructor
public enum DisplayType {

    promotion("商品推品"),
    recommend("算法推荐商品"),
    query("查询结果商品"),
    activity("促销活动");

    private String desc;
}
