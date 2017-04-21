package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class Ip2CaProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT APPL_NO,IP,IP_CITY  FROM network.ca_appl_member_device where IP is not null";
    }

}
