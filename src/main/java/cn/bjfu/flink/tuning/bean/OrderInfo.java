package cn.bjfu.flink.tuning.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 9:58 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private Integer id;
    private Long user_id;
    private Double total_amount;
    private Long create_time;
}
