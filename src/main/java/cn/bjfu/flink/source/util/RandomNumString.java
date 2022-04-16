package cn.bjfu.flink.source.util;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 10:26 下午
 */
public class RandomNumString {
    public static final String getRandNumString(int fromNum, int toNum, int count, String delimiter, boolean canRepeat){
        String numString = "";
        if(canRepeat){
            List<Integer> numList = new ArrayList<>();
            while (numList.size() < count){
                numList.add(fromNum + new Random().nextInt(toNum - fromNum + 1));
            }
            numString = StringUtils.join(numList, delimiter);
        }else{
            Set<Integer> numSet = new HashSet();
            if(count <= (toNum - fromNum + 1) / 2){
                 while (numSet.size() < count){
                     numSet.add(fromNum + new Random().nextInt(toNum - fromNum + 1));
                 }
            }else{
                Set<Integer> exNumSet = new HashSet<>();
                while (exNumSet.size() < ((toNum - fromNum + 1) - count)) {
                    exNumSet.add(fromNum + new Random().nextInt(toNum - fromNum + 1));
                }
                for (int i = fromNum; i <= toNum; i++) {
                    if (!exNumSet.contains(i)) {
                        numSet.add(i);
                    }
                }
            }
            numString = StringUtils.join(numSet, delimiter);
        }

        return numString;
    }



    public static void main(String[] args) {
        System.out.println(getRandNumString(1, 3, 4, ",", false));
        String randNumString = getRandNumString(1, 100, 40, ",", false);
        System.out.println(randNumString);
    }
}
