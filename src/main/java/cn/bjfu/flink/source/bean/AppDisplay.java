package cn.bjfu.flink.source.bean;

import cn.bjfu.flink.source.config.AppConfig;
import cn.bjfu.flink.source.enums.DisplayType;
import cn.bjfu.flink.source.enums.ItemType;
import cn.bjfu.flink.source.enums.PageId;
import cn.bjfu.flink.source.util.ParamUtil;
import cn.bjfu.flink.source.util.RandomNum;
import cn.bjfu.flink.source.util.RandomOptionGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import scala.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cn.bjfu.flink.source.config.AppConfig.*;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 11:47 下午
 */
@Data
@AllArgsConstructor
public class AppDisplay {
    private ItemType itemType;

    private String item;

    private DisplayType displayType;

    private Integer order;

    private Integer pos_id;

    public static List<AppDisplay> buildList(AppPage appPage){

        List<AppDisplay> displayList = new ArrayList<>();
        Boolean isSkew = ParamUtil.checkBoolean(AppConfig.mock_skew);

        RandomOptionGroup isSkewRandom = RandomOptionGroup.builder().add(true, 80).add(false, 20).build();

        if(appPage.getPage_id() == PageId.home ||
           appPage.getPage_id() == PageId.discovery ||
           appPage.getPage_id() == PageId.category){
            int displayCount = RandomNum.getRandInt(1, AppConfig.max_activity_count);
            int pos_id = RandomNum.getRandInt(1, AppConfig.max_activity_count);
            for(int i = 0; i <= displayCount; i++){
                int actId = RandomNum.getRandInt(1, AppConfig.max_activity_count);
                AppDisplay appDisplay = new AppDisplay(ItemType.activity_id, actId + "", DisplayType.activity, i, pos_id);
                displayList.add(appDisplay);
            }
        }

        // 非促销活动曝光
        if (appPage.page_id == PageId.good_detail  //商品明细
                || appPage.page_id == PageId.home     //   首页
                || appPage.page_id == PageId.category     // 分类
                || appPage.page_id == PageId.activity     // 活动
                || appPage.page_id == PageId.good_spec     //  规格
                || appPage.page_id == PageId.good_list     // 商品列表
                || appPage.page_id == PageId.discovery) {    // 发现

            int displayCount = RandomNum.getRandInt(min_display_count, max_display_count);
            int activityCount = displayList.size();// 商品显示从 活动后面开始
            for (int i = 1 + activityCount; i <= displayCount + activityCount; i++) {
                // TODO 商品点击，添加倾斜逻辑
                int skuId = 0;
                if (appPage.page_id == PageId.good_detail && isSkew && isSkewRandom.getRandBoolValue()) {
                    skuId = max_sku_id / 2;
                } else {
                    skuId = RandomNum.getRandInt(1, max_sku_id);
                }

                int pos_id = RandomNum.getRandInt(1, max_pos_id);
                // 商品推广：查询结果：算法推荐 = 30：60：10
                RandomOptionGroup<DisplayType> dispTypeGroup = RandomOptionGroup.<DisplayType>builder()
                        .add(DisplayType.promotion, 30)
                        .add(DisplayType.query, 60)
                        .add(DisplayType.recommend, 10)
                        .build();
                DisplayType displayType = dispTypeGroup.getValue();

                AppDisplay appDisplay = new AppDisplay(ItemType.sku_id, skuId + "", displayType, i, pos_id);
                displayList.add(appDisplay);
            }
        }

        return displayList;
    }


}
