package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Created by kisho on 2017/4/6.
 */
public class ReexecuteErrorSqlConsumer extends AbstractTxConsumer {

    protected void process(ODatabaseDocumentTx tx) {
        if (!CacheUtils.COMMAND_SQL.isEmpty()) {
            for (String sql0 : CacheUtils.COMMAND_SQL) {
                String[] strArr = sql0.split("\\|");
                String sql = strArr[0];
                Object[] args = null;
                if (strArr.length == 2) {
                    args = strArr[1].split(",");
                }
                execute(tx, sql, args);
                log.info("Re execute sql success : " + sql);
            }
        }
    }

}
