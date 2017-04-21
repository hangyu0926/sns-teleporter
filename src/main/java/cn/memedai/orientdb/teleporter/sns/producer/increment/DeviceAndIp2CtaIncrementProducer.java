package cn.memedai.orientdb.teleporter.sns.producer.increment;

import cn.memedai.orientdb.teleporter.sns.producer.AbstractDbWithInProducer;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp2CtaIncrementProducer extends AbstractDbWithInProducer {

    protected String getSql() {
        return "SELECT ORDER_ID,DEVICE_ID,IP,IP_CITY from network.cta_order_deviceinfo where ip is not null AND ORDER_ID IN (" + IN_DATA_LIST + ")";
    }

}
