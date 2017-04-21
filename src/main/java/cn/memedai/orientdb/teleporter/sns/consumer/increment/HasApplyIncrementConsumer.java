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
public class HasApplyIncrementConsumer extends AbstractCreateEdgeConsumer {

    private static final String CREATE_HASAPPLY_SQL = "create edge HasApply from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    private static final String SELECT_HASAPPLY_SQL = "select from (select expand(out_HasApply) from {0}) where in = {1}";

    protected void process(ODatabaseDocumentTx tx) {

        //Member-HasApply->ApplyInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_MEMBERID.entrySet()) {
            String memberId = entry.getValue();
            String fromRid = CacheUtils.getMemberRid(memberId);
            String toRid = entry.getKey();
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASAPPLY_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASAPPLY_SQL, fromRid, toRid);
                }
            }
        }

        //Phone-HasApply->ApplyInfo
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_PHONERID.entrySet()) {
            String fromRid = entry.getValue();
            String toRid = entry.getKey();
            if (StringUtils.isNotBlank(fromRid)) {
                OResultSet ocrs = execute(tx, SELECT_HASAPPLY_SQL, fromRid, toRid);
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, CREATE_HASAPPLY_SQL, fromRid, toRid);
                }
            }
        }
    }

}
