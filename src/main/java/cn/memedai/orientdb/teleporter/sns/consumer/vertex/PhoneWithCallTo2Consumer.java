package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Map;


/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2Consumer extends AbstractBlockingQueueConsumer {

    private static final String CREATE_CALL_TO_SQL = "create edge CallTo from {0} to {1} set callCnt = ?,callLen=?,callInCnt=?,callOutCnt=?,reportTime=? retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String UPDATE_CALL_TO_SQL = "update edge {0} set callCnt = ?,callLen=?,callInCnt=?,callOutCnt=?,reportTime=?";

    private static final String SELECT_CALL_TO_SQL = "select from (select expand(out_CallTo) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");
        String toPhone = (String) dataMap.get("PHONE_NO");
        if (StringUtils.isBlank(applyNo) || StringUtils.isBlank(toPhone)) {
            return;
        }

        String applyInfoRid = CacheUtils.getApplyInfoRid(applyNo);
        if (StringUtils.isBlank(applyInfoRid)) {
            return;
        }

        String toPhoneRid = getPhoneRid(tx, toPhone);
        String fromPhoneRid = CacheUtils.getApplyInfoRidPhoneRid(applyInfoRid);
        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("CREATE_TIME") == null ? null : dataMap.get("CREATE_TIME"),
        };

//        OResultSet ocrs = execute(tx, MessageFormat.format(SELECT_CALL_TO_SQL, fromPhoneRid, toPhoneRid));
//        if (ocrs != null && !ocrs.isEmpty()) {
//            //更新
//            ODocument doc = (ODocument) ocrs.get(0);
//            ORecordId oRecordId = doc.field("@rid");
//            execute(tx, MessageFormat.format(UPDATE_CALL_TO_SQL, oRecordId.getIdentity().toString()), args);
//        } else {
            //新增
            execute(tx, MessageFormat.format(CREATE_CALL_TO_SQL, fromPhoneRid, toPhoneRid), args);
//        }

    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
