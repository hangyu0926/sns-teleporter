package cn.memedai.orientdb.teleporter.sns.producer.increment;

import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbProducer;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;

/**
 * Created by kisho on 2017/4/6.
 */
public class ApplyInfoIncrementProducer extends AbstractDbProducer {

    protected String getSql() {
        String sql = "SELECT cellphone,member_id,apply_no,created_datetime,apply_status,store_id,order_no FROM network.apply_info ";
        sql += "where created_datetime >= '" + ConfigUtils.getDefaultLimitCreatedDatetime()
                + "' or modified_datetime >= '" + ConfigUtils.getDefaultLimitCreatedDatetime() + "'";
        return sql;
    }

}
