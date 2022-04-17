package cn.bjfu.flink.tuning;

import cn.bjfu.flink.source.MockSourceFunction;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import java.util.Random;

/**
 * @author jiaxiangyu
 * @date 2022/4/17 6:07 下午
 */
public class SkewDemo2 {

    public static void main(String[] args) throws Exception{

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.disableOperatorChaining();

        SingleOutputStreamOperator<JSONObject> jsonObs = env.addSource(new MockSourceFunction())
                .map(t -> JSONObject.parseObject(t));

        SingleOutputStreamOperator<Tuple2<String, Long>> filterDs = jsonObs.filter(t -> StringUtils.isEmpty(t.getString("start")))
                .map(t -> Tuple2.of(t.getJSONObject("common").getString("mid"), 1L))
                .returns(Types.TUPLE(Types.STRING, Types.LONG));

        //按照mid分组，统计出每10s，各mid出现的次数
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        boolean twoPhase = parameterTool.getBoolean("two-phase", true);
        int randomNum = parameterTool.getInt("random-num", 5);

        if(!twoPhase){
            filterDs
                    .keyBy(t -> t.f0)
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                    .reduce((v1, v2) -> Tuple2.of(v1.f0, v1.f1 + v2.f1))
                    .print();
        }else{
            Random random = new Random();
            //拼接随机数打散，第一次聚合
            SingleOutputStreamOperator<Tuple3<String, Long, Long>> reduceDs = filterDs.map(new MapFunction<Tuple2<String, Long>, Tuple2<String, Long>>() {
                        @Override
                        public Tuple2<String, Long> map(Tuple2<String, Long> value) throws Exception {
                            return Tuple2.of(value.f0 + "-" + random.nextInt(randomNum), 1l);
                        }
                    }).keyBy(t -> t.f0)
                    .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                    .reduce((t1, t2) -> Tuple2.of(t1.f0, t2.f1 + t1.f1)
                            //processWindow input、ouput、key的类型、窗口
                            , new ProcessWindowFunction<Tuple2<String, Long>, Tuple3<String, Long, Long>, String, TimeWindow>() {
                                @Override
                                public void process(String s, ProcessWindowFunction<Tuple2<String, Long>, Tuple3<String, Long, Long>, String, TimeWindow>.Context context, Iterable<Tuple2<String, Long>> iterable, Collector<Tuple3<String, Long, Long>> collect) throws Exception {
                                    long windowEnd = context.window().getEnd();
                                    Tuple2<String, Long> midAndCount = iterable.iterator().next();
                                    collect.collect(Tuple3.of(midAndCount.f0, midAndCount.f1, windowEnd));
                                }
                            });

            //按照原来的key和window进行二次聚合
            reduceDs.map(new MapFunction<Tuple3<String, Long, Long>, Tuple3<String, Long, Long>>() {
                @Override
                public Tuple3<String, Long, Long> map(Tuple3<String, Long, Long> value) throws Exception {
                    String originKey = value.f0.split("-")[0];
                    return Tuple3.of(originKey, value.f1, value.f2);
                }
            }).
                    keyBy(new KeySelector<Tuple3<String, Long, Long>, Tuple2<String, Long>>() {

                        @Override
                        public Tuple2<String, Long> getKey(Tuple3<String, Long, Long> key) throws Exception {
                            return Tuple2.of(key.f0, key.f2);
                        }
                    })
                            .reduce((t1, t2) -> Tuple3.of(t1.f0, t1.f1 + t2.f1, t1.f2))
                                    .print();
        }
        env.execute();
    }
}
