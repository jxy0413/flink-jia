package cn.bjfu.flink.tuning;

import cn.bjfu.flink.source.MockSourceFunction;
import cn.bjfu.flink.tuning.function.UvRichFilterFunction;
import cn.bjfu.flink.tuning.map.NewMidRichMapFunc;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 7:33 下午
 */
public class UidDemo {
    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStateBackend(new HashMapStateBackend());

        SingleOutputStreamOperator<JSONObject> jsonObjDs = env.addSource(new MockSourceFunction())
                .uid("mock-source")
                .name("mock-source")
                .map(t -> JSONObject.parseObject(t))
                .uid("parsejon-map")
                .name("parsejon-map");

        //修正
        SingleOutputStreamOperator<JSONObject> jsonFlagDs = jsonObjDs
                .keyBy(t -> t.getJSONObject("common").getString("mid"))
                .map(new NewMidRichMapFunc())
                .uid("fixNewMid-map")
                .name("fixNewMid-map");


        //过滤出 页面数据
        SingleOutputStreamOperator<JSONObject> filterDs = jsonFlagDs.filter(t -> StringUtils.isEmpty(t.getString("start")))
                .uid("page-filter").name("page-filter");

        SingleOutputStreamOperator<JSONObject> uvDs = filterDs
                .keyBy(t -> t.getJSONObject("common").getString("mid"))
                .filter(new UvRichFilterFunction())
                .uid("firstMid-filter").name("firstMid-filter");

        uvDs.map(t -> Tuple3.of("uv", t.getJSONObject("common").getString("mid"), 1L))
                .uid("uvAndone-map").name("uvAndone-map")
                .returns(Types.TUPLE(Types.STRING, Types.STRING, Types.LONG))
                .keyBy(t -> t.f0)
                .reduce((t1, t2) -> Tuple3.of("uv", t2.f1, t1.f2 + t2.f2))
                .uid("uv-reduce").name("uv-reduce")
                .print()
                .uid("uv-print")
                .name("uv-print");

        env.execute();

    }
}
