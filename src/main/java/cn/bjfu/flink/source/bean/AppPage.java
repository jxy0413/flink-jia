package cn.bjfu.flink.source.bean;

import cn.bjfu.flink.source.config.AppConfig;
import cn.bjfu.flink.source.enums.DisplayType;
import cn.bjfu.flink.source.enums.ItemType;
import cn.bjfu.flink.source.enums.PageId;
import cn.bjfu.flink.source.util.RandomNum;
import cn.bjfu.flink.source.util.RandomNumString;
import cn.bjfu.flink.source.util.RandomOptionGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import scala.App;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 4:23 下午
 */
@Data
@AllArgsConstructor
public class AppPage {
    private PageId last_page_id;

    PageId page_id;

    ItemType item_type;

    String item;

    private Integer during_time;

    private String extend1;

    private String extend2;

    private DisplayType source_type;

    public static AppPage build(PageId pageId, PageId lastPageId, Integer duringTime){
        ItemType itemType = null;
        String item = null;
        String extend1 = null;
        String extend2 = null;
        DisplayType sourceType = null;

        RandomOptionGroup<DisplayType> sourceTypeGroup = RandomOptionGroup.<DisplayType>builder()
                .add(DisplayType.query, AppConfig.sourceTypeRate[0])
                .add(DisplayType.promotion, AppConfig.sourceTypeRate[1])
                .add(DisplayType.recommend, AppConfig.sourceTypeRate[2])
                .add(DisplayType.activity, AppConfig.sourceTypeRate[3])
                .build();

        sourceType = sourceTypeGroup.getValue();

        if(pageId == PageId.good_detail || pageId == PageId.good_spec || pageId == PageId.comment || pageId == PageId.comment_list){
            itemType = ItemType.sku_id;
            item = RandomNum.getRandInt(1, AppConfig.max_sku_id) + "";
        }else if(pageId == PageId.good_list){
            itemType = ItemType.keyword;
            item=  new RandomOptionGroup(AppConfig.searchKeywords).getRandStringValue();
        }else if(pageId == PageId.trade || pageId == PageId.payment || pageId == PageId.payment_done){
            itemType = ItemType.sku_ids;
            item = RandomNumString.getRandNumString(1, AppConfig.max_sku_id,RandomNum.getRandInt(1,3),",",false);
        }

        return new AppPage(lastPageId, pageId, itemType, item, duringTime, extend1, extend2, sourceType);
    }

}
