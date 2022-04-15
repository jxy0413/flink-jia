package cn.bjfu.flink.source;

import cn.bjfu.flink.source.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:11 下午
 */
@Slf4j
public class MockSourceFunction implements ParallelSourceFunction<String> {

    private volatile Long ts;
    private volatile int mockCount;

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        for(; mockCount < AppConfig.mock_count; mockCount++){

        }
    }

    @Override
    public void cancel() {

    }
}
