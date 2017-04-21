package cn.memedai.orientdb.teleporter.sns.producer.increment;

import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbWithInProducer;

/**
 * Created by kisho on 2017/4/6.
 */
public class Device2CaIncrementProducer extends AbstractDbWithInProducer {

    protected String getSql() {
        return "SELECT APPL_NO,DEVICE_ID FROM network.ca_appl_member_device where DEVICE_ID is not null AND APPL_NO IN (" + IN_DATA_LIST + ")";
    }

}
