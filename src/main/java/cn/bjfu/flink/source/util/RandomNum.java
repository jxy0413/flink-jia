package cn.bjfu.flink.source.util;

import java.util.Random;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 3:42 下午
 */
public class RandomNum {
    public static final int getRandInt(int fromNum, int toNum){
        return fromNum + new Random().nextInt(toNum - fromNum + 1);
    }

    public static void main(String[] args) {
        System.out.println(getRandInt(1, 10));
    }
}
