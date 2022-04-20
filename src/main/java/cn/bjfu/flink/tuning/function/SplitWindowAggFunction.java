package cn.bjfu.flink.tuning.function;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiaxiangyu
 * @date 2022/4/20 11:33 上午
 */
public class SplitWindowAggFunction extends KeyedProcessFunction<Integer, Tuple3<String, String, Long>, Tuple3<String, String, Long>> {
    ValueState<List<Tuple3<String, String, Long>>> splitListState;
    List<Tuple3<String, String, Long>> splitList;
    int splitNum;
    Tuple3<String, String, Long> windowAggResult;

    @Override
    public void open(Configuration parameters) throws Exception {
        splitListState = getRuntimeContext()
                .getState(
                        new ValueStateDescriptor<List<Tuple3<String, String, Long>>>(
                                "splitListState",
                                Types.LIST(Types.TUPLE(Types.STRING, Types.STRING, Types.LONG)),
                                new ArrayList<>()
                        )
                );
        //窗口：60分钟的窗口 1秒滑动
        splitNum = 3;
        windowAggResult = new Tuple3<>();
        super.open(parameters);
    }

    @Override
    public void processElement(Tuple3<String, String, Long> value, KeyedProcessFunction<Integer, Tuple3<String, String, Long>, Tuple3<String, String, Long>>.Context context, Collector<Tuple3<String, String, Long>> collector) throws Exception {
        //每个时间分片的结果来说，先清空统计值
        windowAggResult.f2 = 0L;
        //将新的时间分片结果添加到List,删除第一个时间分片
        splitList = splitListState.value();
        splitList.add(value);
        splitListState.update(splitList);

        if(splitList.size() >= splitNum) {
            if (splitList.size() == (splitNum + 1)) {
                splitList.remove(0);
                splitListState.update(splitList);
            }
            for(int i = 0; i < splitList.size(); i++){
                //累计时间分片
                windowAggResult.f2 = splitList.get(i).f2;
                if(i == 0){
                    windowAggResult.f0 = splitList.get(0).f0;
                }
                if(i == (splitList.size() - 1)){
                    windowAggResult.f1 = splitList.get(i).f1;
                }
            }
            collector.collect(windowAggResult);
        }

    }
}
