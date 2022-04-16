package cn.bjfu.flink.source.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jiaxiangyu
 * @date 2022/4/15 2:25 下午
 */
public class ParamUtil {

    public static final Boolean checkBoolean(String bool){
        if(bool.equals("1") || bool.equals("true")){
            return true;
        }else if(bool.equals("0") || bool.equals("false")){
            return false;
        }else {
            throw new RuntimeException("是非型参数请填写：1或者0， true或者false");
        }
    }

    public static final Date checkDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = null;
        try {
            String timeStr = sdf.format(new Date());
            String datetimeStr = dateString + " " + timeStr;
            parse = sdfFor.parse(datetimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public static void main(String[] args) {
        System.out.println(checkDate("2021-11-11"));
    }
}
