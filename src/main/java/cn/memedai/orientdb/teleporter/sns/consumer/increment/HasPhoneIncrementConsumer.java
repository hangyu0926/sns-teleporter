package cn.memedai.orientdb.teleporter.sns.consumer.increment;

import cn.memedai.orientdb.teleporter.sns.consumer.edge.AbstractCreateEdgeConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasPhoneIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASPHONE_SQL = "create edge HasPhone from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");
    private static final String SELECT_HASPHONE_SQL = "select from (select expand(out_HasPhone) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx) {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_MEMBER_PHONERIDS.entrySet()) {
            String memberId = entry.getKey();
            String fromRid = CacheUtils.getMemberRid(memberId);
            String phoneRids = entry.getValue();
            //Member-HasPhone->Phone
            String[] phoneRidArr = phoneRids.split("\\|");
            for (String toRid : phoneRidArr) {
                if (StringUtils.isNotBlank(fromRid)) {
                    OResultSet ocrs = execute(tx, SELECT_HASPHONE_SQL, fromRid, toRid);
                    if (ocrs == null || ocrs.isEmpty()) {
                        execute(tx, CREATE_HASPHONE_SQL, fromRid, toRid);
                    }
                }
            }
        }
    }

}
