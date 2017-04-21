package cn.memedai.orientdb.teleporter.sns.producer.increment;

import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbWithInProducer;

/**
 * Created by kisho on 2017/4/6.
 */
public class Ip2CaIncrementProducer extends AbstractDbWithInProducer {

    protected String getSql() {
        return "SELECT APPL_NO,IP,IP_CITY  FROM network.ca_appl_member_device where IP is not null and APPL_NO in (" + IN_DATA_LIST + ")";
    }

}
