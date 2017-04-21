package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class ApplyInfoConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_APPLYINFO = "update ApplyInfo set applyNo=?,status=?,createdDatetime=? upsert return after where applyNo=?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("apply_no");
        //ApplyInfo
        Object[] args = new Object[]{
                applyNo,
                dataMap.get("apply_status"),
                dataMap.get("created_datetime"),
                applyNo
        };
        String applyInfoRid = getRid(execute(tx, SQL_APPLYINFO, args));
        CacheUtils.setApplyInfoRid(applyNo, applyInfoRid);

        //orderNo->ApplyInfo
        String orderNo = (String) dataMap.get("order_no");
        if (StringUtils.isNotBlank(orderNo)) {
            CacheUtils.setOrderNoApplyInfoRid(orderNo, applyInfoRid);
        }

        //ApplyInfo->storeId
        String storeId = (String) dataMap.get("store_id");
        if (StringUtils.isNotBlank(storeId)) {
            CacheUtils.setApplyInfoRidStoreId(applyInfoRid, storeId);
        }

        String memberId = dataMap.get("member_id").toString();
        String phone = (String) dataMap.get("cellphone");
        if (StringUtils.isNotBlank(phone)) {
            String phoneRid = processMemberAndPhone(tx, memberId, phone);
            if (StringUtils.isNotBlank(phoneRid)) {
                //ApplyInfo->Phone
                CacheUtils.setApplyInfoRidPhoneRid(applyInfoRid, phoneRid);
            }

        }

        //ApplyInfo->memberId
        CacheUtils.setApplyInfoRidMemberId(applyInfoRid, memberId);
    }

}
