package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneAndPhoneMarkProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT PHONE_NO, PHONE_TYPE  FROM network.ca_sys_phone_tag_merge";
    }

}
