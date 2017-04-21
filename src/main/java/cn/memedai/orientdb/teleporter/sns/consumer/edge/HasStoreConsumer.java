package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasStoreConsumer extends AbstractCreateEdgeConsumer {

    private static final String SQL_HASSTORE = "create edge HasStore from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx) {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            //ApplyInfo-HasStore->StoreInfo
            String toRid = CacheUtils.getStoreInfoRid(storeId);
            if (StringUtils.isBlank(toRid)) {
                log.info("storeId : " + storeId + " does not exist!");
            } else {
                execute(tx, SQL_HASSTORE, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            //OrderInfo-HasStore->StoreInfo
            String toRid = CacheUtils.getStoreInfoRid(storeId);
            if (StringUtils.isBlank(toRid)) {
                log.info("storeId : " + storeId + " does not exist!");
            } else {
                execute(tx, SQL_HASSTORE, fromRid, toRid);
            }
        }
    }

}
