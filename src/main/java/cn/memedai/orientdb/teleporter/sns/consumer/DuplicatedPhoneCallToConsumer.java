package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.model.CallTo;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class DuplicatedPhoneCallToConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_CALLTO = "select expand(out_CallTo) from Phone where phone = ?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, String> dataMap = (Map<String, String>) obj;
        String phone = dataMap.get("cellphone");
        OResultSet ocrs = execute(tx, SQL_CALLTO, phone);

        if (ocrs != null && !ocrs.isEmpty()) {
            Map<String, List<CallTo>> allCallTos = new HashMap<String, List<CallTo>>(ocrs.size());
            for (int i = 0; i < ocrs.size(); i++) {
                ODocument doc = (ODocument) ocrs.get(i);
                CallTo callTo = createCallTo(doc);
                String key = callTo.getOut() + callTo.getIn();
                List<CallTo> value = allCallTos.get(key);
                if (value == null) {
                    value = new ArrayList<CallTo>();
                }
                value.add(callTo);
                allCallTos.put(key, value);
            }
            for (Map.Entry<String, List<CallTo>> entry : allCallTos.entrySet()) {
                List<CallTo> callTos = entry.getValue();
                if (callTos.size() > 1) {
                    callTos.remove(0);
                    for (CallTo callTo : callTos) {
                        execute(tx, " delete edge " + callTo.getRid());
                    }
                }
            }
        }
    }

    private CallTo createCallTo(ODocument doc) {
        CallTo callTo = new CallTo();
        ORecordId oRecordId = doc.field("@rid");
        callTo.setRid(oRecordId.getIdentity().toString());
        callTo.setReportTime("" + doc.field("reportTime"));
        ODocument inDoc = doc.field("in");
        ORecordId inRid = inDoc.field("@rid");
        callTo.setIn(inRid.getIdentity().toString());
        ODocument outDoc = doc.field("out");
        ORecordId outRid = outDoc.field("@rid");
        callTo.setOut(outRid.getIdentity().toString());
        return callTo;
    }

}
