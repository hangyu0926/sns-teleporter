package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class Ip2CaConsumer extends AbstractBlockingQueueConsumer {

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");

        String ip = (String) dataMap.get("IP");
        String ipCity = (String) dataMap.get("IP_CITY");
        processApplyAndIp(tx, applyNo, ip, ipCity);
    }

}
