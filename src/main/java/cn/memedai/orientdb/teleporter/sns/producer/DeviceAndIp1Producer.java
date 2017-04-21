package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeviceAndIp1Producer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT APPL_NO,DEVICE_ID,IP,IP_CITY FROM memedai.A_APPL where device_id is not null or ip is not null";
    }

}
