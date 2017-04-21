package cn.memedai.orientdb.teleporter.sns.consumer.increment;

import cn.memedai.orientdb.teleporter.sns.consumer.edge.AbstractCreateEdgeConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasIpIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASIP_SQL = "create edge HasIp from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String SELECT_HASIP_SQL = "select from (select expand(out_HasIp) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx) {
        Set<String> memberRidAndIpRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_IPRID.entrySet()) {
            //ApplyInfo-HasIp->Ip
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyInfoRid(applyNo);
            createHasIp(tx, fromRid, toRid);

            String memberRid = getMemberRid(tx, CacheUtils.CACHE_APPLYINFORID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndIpRidSet.add(memberRid + "|" + toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_IPRID.entrySet()) {
            //OrderInfo-HasIp->Ip
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderInfoRid(orderNo);
            createHasIp(tx, fromRid, toRid);

            String memberRid = getMemberRid(tx, CacheUtils.CACHE_ORDERINFORID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndIpRidSet.add(memberRid + "|" + toRid);
            }
        }
    }

    private void createHasIp(ODatabaseDocumentTx tx, String fromRid, String toRid) {
        if (StringUtils.isBlank(fromRid) || StringUtils.isBlank(toRid)) {
            return;
        }
        OResultSet ocrs = execute(tx, SELECT_HASIP_SQL, fromRid, toRid);
        if (ocrs == null || ocrs.isEmpty()) {
            execute(tx, CREATE_HASIP_SQL, fromRid, toRid);
        }
    }

}
