package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneAndPhoneMarkConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_PHONEMARK = "update PhoneMark set mark=? upsert return after where mark=?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String phone = (String) dataMap.get("PHONE_NO");

        String phoneRid = getPhoneRid(tx, phone);
        if (StringUtils.isBlank(phoneRid)) {
            return;
        }

        String mark = (String) dataMap.get("PHONE_TYPE");
        //vertex : PhoneMark
        String markRid = CacheUtils.getPhoneMarkRid(mark);
        if (StringUtils.isBlank(markRid)) {
            markRid = getRid(execute(tx, SQL_PHONEMARK, mark,mark));
            CacheUtils.setPhoneMarkRid(mark, markRid);
        }

        //edge : HasPhoneMark
        execute(tx, "create edge HasPhoneMark from " + phoneRid + " to " + markRid);

    }

}
