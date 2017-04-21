package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class ApplyInfoProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT cellphone,member_id,apply_no,created_datetime,apply_status,store_id,order_no FROM network.apply_info";
    }

}
