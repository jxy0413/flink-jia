package cn.bjfu.flink.tuning;

import cn.bjfu.flink.source.MockSourceFunction;
import cn.bjfu.flink.tuning.function.TestFilter;
import cn.bjfu.flink.tuning.function.UvRichFilterFunction;
import cn.bjfu.flink.tuning.map.NewMidRichMapFunc;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.runtime.state.hashmap.HashMapStateBackend;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 1:20 下午
 */
public class UvDemo {
    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStateBackend(new HashMapStateBackend());

        SingleOutputStreamOperator<JSONObject> jsonDs = env.addSource(new MockSourceFunction())
                .map(t -> JSONObject.parseObject(t));

        SingleOutputStreamOperator<JSONObject> mapDs = jsonDs.keyBy(t -> t.getJSONObject("common").getString("mid")).map(new NewMidRichMapFunc());

        SingleOutputStreamOperator<JSONObject> pageDs = mapDs.filter(t -> StringUtils.isEmpty(t.getString("start")));

        SingleOutputStreamOperator<JSONObject> filterDs = pageDs.keyBy(jsonObj -> jsonObj.getJSONObject("common").getString("mid"))
                .filter(new UvRichFilterFunction());


        SingleOutputStreamOperator<Tuple3<String, String, Long>> returnDs = filterDs.map(r -> Tuple3.of("uv", r.getJSONObject("common").getString("mid"), 1L))
                .returns(Types.TUPLE(Types.STRING, Types.STRING, Types.LONG));

        returnDs.keyBy(t -> t.f0)
                        .reduce((t1,t2) -> Tuple3.of("uv", t2.f1, t1.f2 + t2.f2))
                                .print();

        env.execute();
    }
}
