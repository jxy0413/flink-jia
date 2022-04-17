package cn.bjfu.flink.tuning;

import cn.bjfu.flink.tuning.bean.OrderInfo;
import cn.bjfu.flink.tuning.bean.UserInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.datagen.DataGeneratorSource;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.flink.streaming.api.functions.source.datagen.RandomGenerator;
import org.apache.flink.streaming.api.functions.source.datagen.SequenceGenerator;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 9:56 下午
 */
public class DataStreamDataGenDemo {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<OrderInfo> returnDs = env.addSource(new DataGeneratorSource<OrderInfo>(
                new RandomGenerator<OrderInfo>() {
                    @Override
                    public OrderInfo next() {
                        return new OrderInfo(
                                random.nextInt(1, 100000),
                                random.nextLong(1, 1000000),
                                random.nextUniform(1, 1000),
                                System.currentTimeMillis()
                        );
                    }
                }
        )).returns(Types.POJO(OrderInfo.class));

        SingleOutputStreamOperator<UserInfo> userInfoDs = env.addSource(new DataGeneratorSource<UserInfo>(
                new SequenceGenerator<UserInfo>(1, 100000) {
                    RandomDataGenerator random = new RandomDataGenerator();

                    @Override
                    public UserInfo next() {
                        return new UserInfo(
                                valuesToEmit.peek().intValue(),
                                valuesToEmit.poll().longValue(),
                                random.nextInt(1, 100),
                                random.nextInt(0, 1));
                    }
                }
        )).returns(Types.POJO(UserInfo.class));

        returnDs.print();
        userInfoDs.print();

        env.execute();
    }

}
