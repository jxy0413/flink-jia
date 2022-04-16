package cn.bjfu.flink.source.bean;

import cn.bjfu.flink.source.config.AppConfig;
import cn.bjfu.flink.source.util.ParamUtil;
import cn.bjfu.flink.source.util.RandomNum;
import cn.bjfu.flink.source.util.RandomOptionGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:21 下午
 */
@Data
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class AppCommon {
    private String mid;
    private String uid;
    private String vc;
    private String ch;
    private String os;
    private String ar;
    private String md;
    private String ba;
    private String is_new;

    //构建者设计模式
    public static AppCommon build(){
        String mid;
        String uid;
        String vc;
        String ch;
        String os;
        String ar;
        String md;
        String ba;
        String is_new;

        Boolean isSkew = ParamUtil.checkBoolean(AppConfig.mock_skew);
        RandomOptionGroup isSkewRandom = RandomOptionGroup.builder().add(true, 80).add(false, 20).build();

        //设备唯一标识
        //添加倾斜开关
        if(isSkew && isSkewRandom.getRandBoolValue()){
            mid = "mid_" + AppConfig.max_mid / 2 ;
        }else{
            mid = "mid_" + RandomNum.getRandInt(1, AppConfig.max_mid) + "";
        }

        ar = new RandomOptionGroup<String>(
                new RanOpt<String>("110000", 10),
                new RanOpt<String>("310000", 10),
                new RanOpt<String>("230000", 10),
                new RanOpt<String>("370000", 10),
                new RanOpt<String>("420000", 10),
                new RanOpt<String>("440000", 10),
                new RanOpt<String>("500000", 10),
                new RanOpt<String>("530000", 10)
        ).getRandStringValue();

        md = new RandomOptionGroup<String>(new RanOpt<String>("Xiaomi 9", 30),
                new RanOpt<String>("Xiaomi 10 Pro ", 30),
                new RanOpt<String>("Xiaomi Mix2 ", 30),
                new RanOpt<String>("iPhone X", 20),
                new RanOpt<String>("iPhone 8", 20),
                new RanOpt<String>("iPhone Xs", 20),
                new RanOpt<String>("iPhone Xs Max", 20),
                new RanOpt<String>("Huawei P30", 10),
                new RanOpt<String>("Huawei Mate 30", 10),
                new RanOpt<String>("Redmi k30", 10),
                new RanOpt<String>("Honor 20s", 5),
                new RanOpt<String>("vivo iqoo3", 20),
                new RanOpt<String>("Oneplus 7", 5),
                new RanOpt<String>("Sumsung Galaxy S20", 3)).getRandStringValue();

        ba = md.split(" ")[0];

        //渠道
        if(ba.equals("iPhone")){
            ch = "AppStore";
            os = "IOS" + new RandomOptionGroup<String>(
                    new RanOpt<String>("13.3.1", 30),
                    new RanOpt<String>("13.2.9", 10),
                    new RanOpt<String>("13.2.3", 10),
                    new RanOpt<String>("12.4.1", 5)
            ).getRandStringValue();
        }else{
            ch = new RandomOptionGroup<String>(new RanOpt<String>("xiaomi", 30),
                    new RanOpt<String>("wandoujia", 10),
                    new RanOpt<String>("web", 10),
                    new RanOpt<String>("huawei", 5),
                    new RanOpt<String>("oppo", 20),
                    new RanOpt<String>("vivo", 5),
                    new RanOpt<String>("360", 5)
            ).getRandStringValue();
            os = "Android " + new RandomOptionGroup<String>(
                    new RanOpt<String>("11.0", 70),
                    new RanOpt<String>("10.0", 20),
                    new RanOpt<String>("9.0", 5),
                    new RanOpt<String>("8.1", 5)
            ).getRandStringValue();
        }

        //程序版本号
        vc = "v" + new RandomOptionGroup<String>(
                new RanOpt<String>("2.1.134", 70),
                new RanOpt<String>("2.1.132", 20),
                new RanOpt<String>("2.1.111", 5),
                new RanOpt<String>("2.0.1", 5)
        ).getRandStringValue();

        uid = RandomNum.getRandInt(1, AppConfig.max_uid) + "";

        is_new = RandomNum.getRandInt(0, 1) + "";

        AppCommon appBase = new AppCommon(mid, uid, vc, ch, os, ar, md, ba,is_new);

        return appBase;
    }

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++){
            System.out.println(AppCommon.build());
        }
    }
}
