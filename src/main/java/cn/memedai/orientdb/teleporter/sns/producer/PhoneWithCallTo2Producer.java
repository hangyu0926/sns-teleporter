package cn.memedai.orientdb.teleporter.sns.producer;

import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2Producer extends AbstractDbProducer {

    protected String getSql() {
        String sql = "select ID,APPL_NO,PHONE_NO,CALL_CNT,CALL_LEN,CALL_IN_CNT,CALL_OUT_CNT,CREATE_TIME from network.ca_bur_operator_contact ";
        sql += "where CREATE_TIME>= '" + ConfigUtils.getDefaultLimitCreatedDatetime() + "'";
        return sql;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Calendar.getInstance().getTimeInMillis() - 3600 * 24 * 1000);
        System.out.println(sdf.format(date));

    }

}
