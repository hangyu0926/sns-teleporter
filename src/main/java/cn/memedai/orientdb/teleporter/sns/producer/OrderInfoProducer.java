package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class OrderInfoProducer extends AbstractDbProducer {

    protected String getSql() {
        return "SELECT mobile,member_id,order_no,created_datetime,status,store_id from network.money_box_order";
    }

}
