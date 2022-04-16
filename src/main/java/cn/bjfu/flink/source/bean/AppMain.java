package cn.bjfu.flink.source.bean;

import cn.bjfu.flink.source.config.AppConfig;
import cn.bjfu.flink.source.util.RandomOptionGroup;
import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:20 下午
 */
@Data
@Builder
public class AppMain {
    private Long ts;

    private AppCommon common;

    private AppPage page;

    private AppError err;

    private AppNotice appNotice;

    private AppStart appStart;

    private List<AppDisplay> displays;

    private List<AppAction> actions;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static class AppMainBuilder {

        public void checkError() {
            Integer errorRate = AppConfig.error_rate;
            Boolean ifError = RandomOptionGroup.builder().add(true, errorRate).add(false, 100 - errorRate).build().getRandBoolValue();
            if (ifError) {
                AppError appError = AppError.build();
                this.err = appError;
            }

        }

    }
}
