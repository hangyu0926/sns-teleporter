package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo1Consumer extends AbstractBlockingQueueConsumer {

    private static final String CREATE_CALL_TO_SQL = "create edge CallTo from {0} to {1} set callCnt = ?,callLen=?,callInCnt=?,callOutCnt=?,reportTime=? retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String reportno = (String) dataMap.get("REPORTNO");
        String toPhone = (String) dataMap.get("PHONE_NUM");
        if (StringUtils.isBlank(reportno) || StringUtils.isBlank(toPhone)) {
            return;
        }
        String fromPhone = CacheUtils.CACHE_REPORTERNO_PHONE.get(reportno);
        if (StringUtils.isBlank(fromPhone)) {
            return;
        }

        String toPhoneRid = getPhoneRid(tx, toPhone);
        String fromPhoneRid = getPhoneRid(tx, fromPhone);
        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("REPORTTIME") == null ? null : dataMap.get("REPORTTIME"),
        };

        execute(tx, MessageFormat.format(CREATE_CALL_TO_SQL, fromPhoneRid, toPhoneRid), args);
    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
