package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.model.CallTo;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import java.util.*;

/**
 * Created by kisho on 2017/4/6.
 */
public class DuplicatedPhoneCallTo2Consumer extends AbstractTxConsumer {

    private static final String SQL_CALLTO = "select expand(out_CallTo) from #phoneRid";

    protected void process(ODatabaseDocumentTx tx) {
        Collection<String> phoneRids1 = CacheUtils.CACHE_APPLYINFORID_PHONERID.values();
        Collection<String> phoneRids2 = CacheUtils.CACHE_ORDERINFORID_PHONERID.values();
        Set<String> phoneRids = new HashSet<String>();
        phoneRids.addAll(phoneRids1);
        phoneRids.addAll(phoneRids2);
        if (!phoneRids.isEmpty()) {
            for (String phoneRid : phoneRids) {
                OResultSet ocrs = execute(tx, SQL_CALLTO.replace("#phoneRid", phoneRid));

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
                        Collections.sort(callTos);
                        if (callTos.size() > 1) {
                            callTos.remove(0);
                            for (CallTo callTo : callTos) {
                                execute(tx, " delete edge " + callTo.getRid());
                            }
                        }
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
