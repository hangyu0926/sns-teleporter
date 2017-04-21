package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class Device2CaConsumer extends AbstractBlockingQueueConsumer {

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");

        String deviceId = (String) dataMap.get("DEVICE_ID");
        processApplyAndDevice(tx, applyNo, deviceId);
    }

}
