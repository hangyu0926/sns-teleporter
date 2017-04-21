package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class OrderInfoConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_ORDERINFO = "update OrderInfo set orderNo=?,status=?,createdDatetime=? upsert return after where orderNo=?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String orderNo = (String) dataMap.get("order_no");
        //OrderInfo
        Object[] args = new Object[]{
                orderNo,
                dataMap.get("status"),
                dataMap.get("created_datetime"),
                orderNo
        };
        String orderInfoRid = getRid(execute(tx, SQL_ORDERINFO, args));
        CacheUtils.setOrderInfoRid(orderNo, orderInfoRid);

        //OrderInfo->storeId
        String storeId = (String) dataMap.get("store_id");
        if (StringUtils.isNotBlank(storeId)) {
            CacheUtils.setOrderInfoRidStoreId(orderInfoRid, storeId);
        }

        String memberId = dataMap.get("member_id").toString();
        String phone = (String) dataMap.get("mobile");
        if (StringUtils.isNotBlank(phone)) {
            String phoneRid = processMemberAndPhone(tx, memberId, phone);
            if (StringUtils.isNotBlank(phoneRid)) {
                //OrderInfo->Phone
                CacheUtils.setOrderInfoRidPhoneRid(orderInfoRid, phoneRid);
            }

        }

        //OrderInfo->memberId
        CacheUtils.setOrderInfoRidMemberId(orderInfoRid, memberId);
    }

}
