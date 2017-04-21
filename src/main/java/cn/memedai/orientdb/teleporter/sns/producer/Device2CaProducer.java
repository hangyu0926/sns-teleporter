package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class Device2CaProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT APPL_NO,DEVICE_ID FROM network.ca_appl_member_device where DEVICE_ID is not null";
    }

}
