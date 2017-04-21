package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class StoreInfoProducer extends AbstractDbProducer {

    protected String getSql() {
        return "select a.STOREID,a.MERCHANTID,a.STORENAME,a.PROVINCE,a.CITY,b.MERCHANT_TEMPLATE_TYPE from MERCHANTAUDIT.ma_store_baseinfo a inner join MERCHANTAUDIT.ma_merchant_baseinfo b on a.merchantid = b.merchantid";
    }

}
