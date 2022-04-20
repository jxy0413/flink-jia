package cn.bjfu.flink.tuning;

import cn.bjfu.flink.source.MockSourceFunction;
import cn.bjfu.flink.tuning.function.SplitTumpleWindowPAWF;
import cn.bjfu.flink.tuning.function.UvRichFilterFunction;
import cn.bjfu.flink.tuning.map.NewMidRichMapFunc;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @author jiaxiangyu
 * @date 2022/4/18 3:21 下午
 */
public class SlideWindowDemo {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.disableOperatorChaining();

        env.setStateBackend(new EmbeddedRocksDBStateBackend());

        SingleOutputStreamOperator<JSONObject> mapSourceDs = env.addSource(new MockSourceFunction())
                .map(t -> JSONObject.parseObject(t));

        //按照mid分组，新老用户修正
        SingleOutputStreamOperator<JSONObject> jsonNewFlagDs = mapSourceDs.keyBy(t -> t.getJSONObject("common").getString("mid"))
                .map(new NewMidRichMapFunc());

        //过滤出 页面数据
        SingleOutputStreamOperator<JSONObject> jsonObjDs = jsonNewFlagDs.filter(t -> StringUtils.isEmpty(t.getString("start")));

        //按照mid分组，过滤出不是第一次访问的数据
        SingleOutputStreamOperator<JSONObject> uvDs = jsonObjDs.keyBy(t -> t.getJSONObject("common").getString("mid"))
                .filter(new UvRichFilterFunction());

        SingleOutputStreamOperator<Long> mapDs = uvDs.map(t -> 1l);

        //统计最近一小时的uv,1秒更新一次
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        boolean isSlidingSplit = parameterTool.getBoolean("sliding-split", false);

        if(!isSlidingSplit){
            mapDs.windowAll(SlidingProcessingTimeWindows.of(Time.hours(1), Time.seconds(1)))
                    .reduce((v1, v2) -> v1 + v2, new SplitTumpleWindowPAWF())
                    .print();
        }else{

        }

        env.execute();
    }
}
