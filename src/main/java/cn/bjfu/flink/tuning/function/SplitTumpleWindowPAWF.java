package cn.bjfu.flink.tuning.function;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;

/**
 * @author jiaxiangyu
 * @date 2022/4/18 4:10 下午
 */
public class SplitTumpleWindowPAWF extends ProcessAllWindowFunction<Long, Tuple3<String, String, Long>, TimeWindow> {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void process(ProcessAllWindowFunction<Long, Tuple3<String, String, Long>, TimeWindow>.Context context, Iterable<Long> iterable, Collector<Tuple3<String, String, Long>> collector) throws Exception {
        Long count = iterable.iterator().next();
        String windowStart = sdf.format(context.window().getStart());
        String windowEnd = sdf.format(context.window().getEnd());
        collector.collect(Tuple3.of(windowStart, windowEnd, count));
    }
}
