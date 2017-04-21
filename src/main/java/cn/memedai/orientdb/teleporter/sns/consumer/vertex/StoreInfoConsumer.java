package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class StoreInfoConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_TEMPLATE = "update StoreInfo set storeId=?,merchantId=?,storeName=?,province=?,city=?,industryType=? upsert return after where storeId = ?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String storeId = getValue(dataMap.get("storeid"));
        Object[] args = new Object[]{
                storeId,
                getValue(dataMap.get("merchantid")),
                getValue(dataMap.get("storename")),
                getValue(dataMap.get("province")),
                getValue(dataMap.get("city")),
                getValue(dataMap.get("merchant_template_type")),
                storeId};
        String storeInfoRid = getRid(execute(tx, SQL_TEMPLATE, args));
        if (storeInfoRid != null) {
            CacheUtils.setStoreInfoRid(storeId, storeInfoRid);
        }
    }

}
