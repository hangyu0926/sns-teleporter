package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasOrderConsumer extends AbstractCreateEdgeConsumer {

    private static final String SQL_HASORDER = "create edge HasOrder from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx) {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_MEMBERID.entrySet()) {
            String toRid = entry.getKey();
            String memberId = entry.getValue();
            //Member-HasOrder->OrderInfo
            String fromRid = CacheUtils.getMemberRid(memberId);
            if (StringUtils.isNotBlank(fromRid)) {
                execute(tx, SQL_HASORDER, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_PHONERID.entrySet()) {
            String toRid = entry.getKey();
            String fromRid = entry.getValue();
            //Phone-HasApply->OrderInfo
            execute(tx, SQL_HASORDER, fromRid, toRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_APPLYINFORID.entrySet()) {
            String fromRid = entry.getValue();
            String orderNo = entry.getKey();
            String toRid = CacheUtils.getOrderInfoRid(orderNo);
            if (StringUtils.isNotBlank(toRid)) {
                //ApplyInfo-HasOrder->OrderInfo
                execute(tx, SQL_HASORDER, fromRid, toRid);
            }
        }
    }

}
