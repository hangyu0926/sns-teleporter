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
public class HasStoreIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASSTORE_SQL = "create edge HasStore from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String SELECT_HASSTORE_SQL = "select from (select expand(out_HasStore) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx) {
        //ApplyInfo-HasStore->StoreInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            String toRid = CacheUtils.getStoreInfoRid(storeId);
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASSTORE_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASSTORE_SQL, fromRid, toRid);
                }
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            //OrderInfo-HasStore->StoreInfo
            String toRid = CacheUtils.getStoreInfoRid(storeId);
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASSTORE_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASSTORE_SQL, fromRid, toRid);
                }
            }
        }
    }

}
