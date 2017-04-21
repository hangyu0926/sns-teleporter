package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasDeviceConsumer extends AbstractCreateEdgeConsumer {

    private static final String SQL_HASDEVICE = "create edge HasDevice from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx) {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_DEVICERID.entrySet()) {
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyInfoRid(applyNo);
            if (StringUtils.isNotBlank(fromRid)) {
                //ApplyInfo-HasDevice->Device
                execute(tx, SQL_HASDEVICE, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_DEVICERID.entrySet()) {
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderInfoRid(orderNo);
            if (StringUtils.isNotBlank(fromRid)) {
                //OrderInfo-HasDevice->Device
                execute(tx, SQL_HASDEVICE, fromRid, toRid);
            }
        }
    }

}
