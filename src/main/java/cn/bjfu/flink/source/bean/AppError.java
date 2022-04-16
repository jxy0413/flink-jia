package cn.bjfu.flink.source.bean;

import cn.bjfu.flink.source.util.RandomNum;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 11:10 下午
 */
@Data
@AllArgsConstructor
public class AppError {
    private Integer error_code;
    private String msg;

    public static AppError build(){
        int errorCode = RandomNum.getRandInt(1001, 4001);
        String msg=" Exception in thread \\  java.net.SocketTimeoutException\\n \\tat cn.bjfu.flink.mock.log.bean.AppError.main(AppError.java:xxxxxx)";
        return new AppError(errorCode, msg);
    }

    public static void main(String[] args) {
        System.out.println(AppError.build());
    }
}
