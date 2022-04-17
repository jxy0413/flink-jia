package cn.bjfu.flink.tuning.function;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.functions.RichFilterFunction;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 2:38 下午
 */
public class TestFilter extends RichFilterFunction<JSONObject> {
    @Override
    public boolean filter(JSONObject jsonObject) throws Exception {
        return false;
    }
}
