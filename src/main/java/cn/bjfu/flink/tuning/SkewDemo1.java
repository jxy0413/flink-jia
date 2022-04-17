package cn.bjfu.flink.tuning;

import cn.bjfu.flink.source.MockSourceFunction;
import cn.bjfu.flink.tuning.function.LocalKeyByFlayMapFunc;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 11:15 下午
 */
public class SkewDemo1 {

    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.disableOperatorChaining();
        env.setParallelism(5);

        env.setStateBackend(new EmbeddedRocksDBStateBackend());

        SingleOutputStreamOperator<JSONObject> jsonObjs = env.addSource(new MockSourceFunction())
                .map(t -> JSONObject.parseObject(t));

        SingleOutputStreamOperator<Tuple2<String, Long>> pageMidDs = jsonObjs.filter(t -> StringUtils.isEmpty(t.getString("start")))
                .map(r -> Tuple2.of(r.getJSONObject("common").getString("mid"), 1L))
                .returns(Types.TUPLE(Types.STRING, Types.LONG));

        //按照mid分组，统计出每个mid出现的次数
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        boolean isLocalKeyby = parameterTool.getBoolean("local-keyby", false);

        if(!isLocalKeyby){
            pageMidDs.keyBy(t -> t.f0)
                    .reduce((v1,v2) -> Tuple2.of(v1.f0, v2.f1 + v1.f1))
                    .print();
        }else{
            pageMidDs.flatMap(new LocalKeyByFlayMapFunc(20000))
                    .keyBy(t -> t.f0)
                    .reduce((v1, v2) -> Tuple2.of(v1.f0, v2.f1 + v1.f1))
                    .print();
        }
        env.execute();
    }
}
