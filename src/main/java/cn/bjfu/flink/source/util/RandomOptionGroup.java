package cn.bjfu.flink.source.util;

import cn.bjfu.flink.source.bean.RanOpt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.flink.table.planner.expressions.In;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:33 下午
 */
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class RandomOptionGroup <T>{

    int totalWeight = 0;

    List<RanOpt> optList = new ArrayList();

//    public static <T> Builder<T> builder(){
//        return new RandomOptionGroup.Builder<T>();
//    }

    //在长度为100的集合中，随机取数
    public RanOpt<T> getRandomOpt(){
        int i = new Random().nextInt(totalWeight);
        return optList.get(i);
    }

    public static class Builder<T>{
        List<RanOpt> optList = new ArrayList();

        int totalWeight = 0;

        public Builder add(T value, int weight){
            RanOpt ranOpt = new RanOpt(value, weight);

            totalWeight += weight;
            for(int i = 0; i < weight; i++){
                optList.add(ranOpt);
            }
            return this;
        }

        public RandomOptionGroup<T> build(){
            return new RandomOptionGroup<>(totalWeight, optList);
        }
    }

    //这步比较巧妙，变成了长度为100的List
    public RandomOptionGroup(RanOpt<T> ... opts){
        for(RanOpt opt: opts){
            totalWeight += opt.getWeight();
            for(int i = 0; i < opt.getWeight(); i++){
                optList.add(opt);
            }
        }
    }

    public RandomOptionGroup(String ...values){
        for(String value : values){
            totalWeight += 1;
            optList.add(new RanOpt(value, 1));
        }
    }

    public T getValue(){
        int i = new Random().nextInt(totalWeight);
        return (T) optList.get(i).getValue();
    }

    public Boolean getRandBoolValue(){
        int i = new Random().nextInt(totalWeight);
        return (Boolean) optList.get(i).getValue();
    }

    public String getRandStringValue(){
        int i = new Random().nextInt(totalWeight);
        return (String) optList.get(i).getValue();
    }

    public Integer getRandIntValue(){
        int i = new Random().nextInt(totalWeight);
        return (Integer) optList.get(i).getValue();
    }


    public static void main(String[] args) {
        RanOpt[] opts = {new RanOpt("zhang3", 20), new RanOpt("li4", 30), new RanOpt("wang5", 50)};

        RandomOptionGroup randomOptionGroup = new RandomOptionGroup(opts);

        for (int i = 0; i < 10; i++) {
            System.out.println(randomOptionGroup.getRandomOpt().getValue());
        }
    }
}
