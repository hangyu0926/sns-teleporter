package cn.memedai.orientdb.teleporter.sns.producer;

/**
 * Created by kisho on 2017/4/6.
 */
public class ReporternoAndPhoneProducer extends AbstractDbProducer {

    protected String getSql() {
        return "select b.REPORTNO,a.cellphone from network.apply_info a join mis_watson.D_JXL_PERSON b on a.apply_no=b.APPL_NO";
    }

}
