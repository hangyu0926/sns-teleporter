package cn.memedai.orientdb.teleporter.sns.producer.increment;

import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbProducer;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;

/**
 * Created by kisho on 2017/4/6.
 */
public class OrderInfoIncrementProducer extends AbstractDbProducer {

    protected String getSql() {
        String sql = "SELECT mobile,member_id,order_no,created_datetime,status,store_id from network.money_box_order ";
        sql += "where created_datetime >= '" + ConfigUtils.getDefaultLimitCreatedDatetime()
                + "' or modified_datetime >= '" + ConfigUtils.getDefaultLimitCreatedDatetime() + "'";
        return sql;
    }

}
