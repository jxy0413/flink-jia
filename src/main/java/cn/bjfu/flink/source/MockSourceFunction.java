package cn.bjfu.flink.source;

import cn.bjfu.flink.source.bean.*;
import cn.bjfu.flink.source.config.AppConfig;
import cn.bjfu.flink.source.enums.PageId;
import cn.bjfu.flink.source.util.ParamUtil;
import cn.bjfu.flink.source.util.RandomNum;
import cn.bjfu.flink.source.util.RandomOptionGroup;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:11 下午
 */
@Slf4j
public class MockSourceFunction implements ParallelSourceFunction<String> {

    private volatile Long ts;
    private volatile int mockCount;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        for(; mockCount < AppConfig.mock_count; mockCount++){
            List<AppMain> appMainList = doAppMock();
            for (AppMain appMain : appMainList) {
                ctx.collect(appMain.toString());
                Thread.sleep(AppConfig.log_sleep);
            }
        }
    }

    public List<AppMain> doAppMock(){
        List<AppMain> logList = new ArrayList<>();

        Date curDate = ParamUtil.checkDate(AppConfig.mock_date);
        ts = curDate.getTime();

        AppMain.AppMainBuilder appMainBuilder = AppMain.builder();

        //启动 数据
        AppCommon appCommon = AppCommon.build();
        appMainBuilder.common(appCommon);
        appMainBuilder.checkError();

        AppStart appStart = new AppStart.Builder().build();
        appMainBuilder.appStart(appStart);
        appMainBuilder.ts(ts);

        logList.add(appMainBuilder.build());

        String jsonFile = "[\n" +
                "  {\"path\":[\"home\",\"good_list\",\"good_detail\",\"cart\",\"trade\",\"payment\"],\"rate\":20 },\n" +
                "  {\"path\":[\"home\",\"search\",\"good_list\",\"good_detail\",\"login\",\"good_detail\",\"cart\",\"trade\",\"payment\"],\"rate\":50 },\n" +
                "  {\"path\":[\"home\",\"mine\",\"orders_unpaid\",\"trade\",\"payment\"],\"rate\":10 },\n" +
                "  {\"path\":[\"home\",\"mine\",\"orders_unpaid\",\"good_detail\",\"good_spec\",\"comment\",\"trade\",\"payment\"],\"rate\":5 },\n" +
                "  {\"path\":[\"home\",\"mine\",\"orders_unpaid\",\"good_detail\",\"good_spec\",\"comment\",\"home\"],\"rate\":5 },\n" +
                "  {\"path\":[\"home\",\"good_detail\"],\"rate\":70 },\n" +
                "  {\"path\":[\"home\"  ],\"rate\":10 }\n" +
                "]";

        List<Map> pathList = JSON.parseArray(jsonFile, Map.class);
        RandomOptionGroup.Builder<List> builder = RandomOptionGroup.builder();

        for(Map map : pathList){
            List path = (List)map.get("path");
            Integer rate = (Integer) map.get("rate");
            builder.add(path, rate);
        }

        List chosenPath = builder.build().getRandomOpt().getValue();

        PageId lastPageId = null;

        for(Object o : chosenPath){
            AppMain.AppMainBuilder pageBuilder = AppMain.builder().common(appCommon);

            String path = (String) o;

            int pageDuringTime = RandomNum.getRandInt(1000, AppConfig.page_during_max_ms);

            //添加页面
            PageId pageId = EnumUtils.getEnum(PageId.class, path);

            AppPage page = AppPage.build(pageId, lastPageId, pageDuringTime);
            if (pageId == null) {
                System.out.println();
            }
            pageBuilder.page(page);
            // 置入上一个页面
            lastPageId = page.getPage_id();

            // 页面中的动作
            List<AppAction> appActionList = AppAction.buildList(page, ts, pageDuringTime);
            if (appActionList.size() > 0) {
                pageBuilder.actions(appActionList);
            }
            // 曝光
            List<AppDisplay> displayList = AppDisplay.buildList(page);
            if (displayList.size() > 0) {
                pageBuilder.displays(displayList);
            }
            pageBuilder.ts(ts);
            pageBuilder.checkError();
            logList.add(pageBuilder.build());
        }
        return logList;
    }


    @Override
    public void cancel() {
        mockCount = AppConfig.mock_count;
    }

    public static void main(String[] args) {
        MockSourceFunction mockSourceFunction = new MockSourceFunction();
        mockSourceFunction.doAppMock();
    }
}
