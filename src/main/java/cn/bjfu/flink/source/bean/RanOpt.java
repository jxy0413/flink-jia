package cn.bjfu.flink.source.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:35 下午
 */
@Data
@AllArgsConstructor
public class RanOpt<T> {
    T value;
    private int weight;
}
