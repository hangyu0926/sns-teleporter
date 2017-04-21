package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class DuplicatedPhoneCallToProducer extends AbstractDbProducer {

    protected String getSql() {
        return "select cellphone from network.apply_info group by cellphone HAVING count(1)>1";
    }

}
