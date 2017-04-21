package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
public class HasApplyConsumer extends AbstractCreateEdgeConsumer {

    private static final String SQL_HASAPPLY = "create edge HasApply from {0} to {1} retry " + ConfigUtils.getInt("retryTimesForCreateEdge");

    protected void process(ODatabaseDocumentTx tx) {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_MEMBERID.entrySet()) {
            String memberId = entry.getValue();
            String fromRid = CacheUtils.getMemberRid(memberId);
            String toRid = entry.getKey();
            if (StringUtils.isNotBlank(fromRid)) {
                //Member-HasApply->ApplyInfo
                execute(tx, SQL_HASAPPLY, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_PHONERID.entrySet()) {
            String fromRid = entry.getValue();
            String toRid = entry.getKey();
            //Phone-HasApply->ApplyInfo
            execute(tx, SQL_HASAPPLY, fromRid, toRid);
        }
    }

}
