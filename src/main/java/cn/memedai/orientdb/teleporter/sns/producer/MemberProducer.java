package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class MemberProducer extends AbstractDbProducer {

    protected String getSql() {
        return "select a.MEMBER_ID,a.MOBILE_NO,b.`NAME`,b.ID_NO from memedai.MEMBER a LEFT JOIN memedai.ID_CARD b on a.MEMBER_ID = b.MEMBER_ID ";
    }

}
