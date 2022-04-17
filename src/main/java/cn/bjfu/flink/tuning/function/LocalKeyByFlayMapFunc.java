package cn.bjfu.flink.tuning.function;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiaxiangyu
 * @date 2022/4/17 11:19 上午
 */
public class LocalKeyByFlayMapFunc extends RichFlatMapFunction<Tuple2<String, Long>, Tuple2<String, Long>> implements CheckpointedFunction {
    //CheckPoint时 为了保证Exeacly Once
    private ListState<Tuple2<String, Long>> listState;

    //本地Buffer 存放local端缓存的mid的 count 信息
    private HashMap<String, Long> localBuffer;

    //缓存数据量的大小，缓存多少再向下游发送
    private int batchSize;

    //计数器，获取当前批次的
    private AtomicInteger currentSize;

    public LocalKeyByFlayMapFunc(int batchSize){
        this.batchSize = batchSize;
    }

    @Override
    public void flatMap(Tuple2<String, Long> value, Collector<Tuple2<String, Long>> out) throws Exception {
        //1、将新来的数据添加到 buffer中，本地聚合
        Long count = localBuffer.getOrDefault(value.f0, 0L);
        localBuffer.put(value.f0, count);

        //如果达到指定的批次，则将buffer中的数据发送到下游
        if(currentSize.incrementAndGet() >= batchSize){
            for(Map.Entry<String,Long> midAcount : localBuffer.entrySet()){
                out.collect(Tuple2.of(midAcount.getKey(), midAcount.getValue()));
            }
            localBuffer.clear();
            currentSize.set(0);
        }
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        listState.clear();
        for(Map.Entry<String, Long> entry : localBuffer.entrySet()){
            listState.add(Tuple2.of(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        //从状态中恢复数据
        listState = context.getOperatorStateStore().getListState(
                new ListStateDescriptor<Tuple2<String, Long>>(
                        "localBufferState",
                        Types.TUPLE(Types.STRING, Types.LONG)
                )
        );
        localBuffer = new HashMap<>();
        if(context.isRestored()){
            // 从状态中恢复数据到 buffer 中
            for(Tuple2<String, Long> midAndCount : listState.get()){
                Long count = localBuffer.getOrDefault(midAndCount.f0, 0L);
                localBuffer.put(midAndCount.f0, midAndCount.f1 + count);
            }
        }else{
            currentSize = new AtomicInteger(0);
        }
    }
}
