package cn.memedai.orientdb.teleporter.sns.consumer.increment;

import cn.memedai.orientdb.teleporter.sns.consumer.edge.AbstractCreateEdgeConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasOrderIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASORDER_SQL = "create edge HasOrder from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String SELECT_HASORDER_SQL = "select from (select expand(out_HasOrder) from {0}) where in = {1}";


    protected void process(ODatabaseDocumentTx tx) {

        //Member-HasOrder->OrderInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_MEMBERID.entrySet()) {
            String toRid = entry.getKey();
            String memberId = entry.getValue();
            String fromRid = CacheUtils.getMemberRid(memberId);
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASORDER_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASORDER_SQL, fromRid, toRid);
                }
            }
        }

        //Phone-HasApply->OrderInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_PHONERID.entrySet()) {
            String toRid = entry.getKey();
            String fromRid = entry.getValue();
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASORDER_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASORDER_SQL, fromRid, toRid);
                }
            }
        }

        //ApplyInfo-HasOrder->OrderInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_APPLYINFORID.entrySet()) {
            String fromRid = entry.getValue();
            String orderNo = entry.getKey();
            String toRid = CacheUtils.getOrderInfoRid(orderNo);
            if (StringUtils.isNotBlank(toRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASORDER_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASORDER_SQL, fromRid, toRid);
                }
            }
        }
    }

}
