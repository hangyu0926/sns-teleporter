package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp2CtaProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT ORDER_ID,DEVICE_ID,IP,IP_CITY from network.cta_order_deviceinfo where ip is not null";
    }

}
