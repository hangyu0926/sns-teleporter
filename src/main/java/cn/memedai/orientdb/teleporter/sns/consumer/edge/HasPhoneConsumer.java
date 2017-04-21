package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasPhoneConsumer extends AbstractCreateEdgeConsumer {

    private static final String SQL_HASPHONE = "create edge HasPhone from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx) {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_MEMBER_PHONERIDS.entrySet()) {
            String memberId = entry.getKey();
            String memberRid = CacheUtils.getMemberRid(memberId);
            String phoneRids = entry.getValue();
            //Member-HasPhone->Phone
            String[] phoneRidArr = phoneRids.split("\\|");
            for (String phoneRid : phoneRidArr) {
                execute(tx, SQL_HASPHONE, memberRid, phoneRid);
            }
        }
    }

}
