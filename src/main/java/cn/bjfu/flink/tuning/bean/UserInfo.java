package cn.bjfu.flink.tuning.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiaxiangyu
 * @date 2022/4/16 10:10 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private Integer id;
    private Long user_id;
    private Integer age;
    private Integer sex;
}
