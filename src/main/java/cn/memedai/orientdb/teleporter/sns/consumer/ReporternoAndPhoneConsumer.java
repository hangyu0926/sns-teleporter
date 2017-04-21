package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class ReporternoAndPhoneConsumer extends AbstractBlockingQueueConsumer {

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, String> dataMap = (Map<String, String>) obj;
        String reportno = dataMap.get("REPORTNO");
        String phone = dataMap.get("cellphone");
        CacheUtils.setCacheReporternoPhone(reportno, phone);
    }


}
