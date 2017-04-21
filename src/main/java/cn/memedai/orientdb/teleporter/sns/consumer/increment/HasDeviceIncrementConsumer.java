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
public class HasDeviceIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASDEVICE_SQL = "create edge HasDevice from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String SELECT_HASDEVICE_SQL = "select from (select expand(out_HasDevice) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx) {
        Set<String> memberRidAndDeviceRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_DEVICERID.entrySet()) {
            //ApplyInfo-HasDevice->Device
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyInfoRid(applyNo);
            createHasDevice(tx, fromRid, toRid);

            String memberRid = getMemberRid(tx, CacheUtils.CACHE_APPLYINFORID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndDeviceRidSet.add(memberRid + "|" + toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_DEVICERID.entrySet()) {
            //OrderInfo-HasDevice->Device
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderInfoRid(orderNo);
            createHasDevice(tx, fromRid, toRid);

            String memberRid = getMemberRid(tx, CacheUtils.CACHE_ORDERINFORID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndDeviceRidSet.add(memberRid + "|" + toRid);
            }
        }

        if (!memberRidAndDeviceRidSet.isEmpty()) {
            for (String memberRidAndDeviceRid : memberRidAndDeviceRidSet) {
                String[] strArr = memberRidAndDeviceRid.split("\\|");
                String memberRid = strArr[0];
                String deviceRid = strArr[1];
                //Member-HasDevice->Device
                createHasDevice(tx, memberRid, deviceRid);
            }
        }
    }

    private void createHasDevice(ODatabaseDocumentTx tx, String fromRid, String toRid) {
        if (StringUtils.isBlank(fromRid) || StringUtils.isBlank(toRid)) {
            return;
        }
        OResultSet ocrs = execute(tx, SELECT_HASDEVICE_SQL, fromRid, toRid);
        if (ocrs == null || ocrs.isEmpty()) {
            execute(tx, CREATE_HASDEVICE_SQL, fromRid, toRid);
        }
    }

}
