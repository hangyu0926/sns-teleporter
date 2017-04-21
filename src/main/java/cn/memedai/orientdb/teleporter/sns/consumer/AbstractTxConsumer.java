package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractTxConsumer extends AbstractConsumer {

    private int sqlCount;

    private int printCount = 10000;

    private long startTime;

    protected <RET> RET execute(ODatabaseDocumentTx tx, String sql, Object... args) {
        RET ret = super.execute(tx, sql, args);
        if (++sqlCount % printCount == 0) {
            log.debug("executed count {} use time {}ms", sqlCount, (System.currentTimeMillis() - startTime));
        }
        return ret;
    }

    public void run() {
        long s = System.currentTimeMillis();
        final ODatabaseDocumentTx tx = new ODatabaseDocumentTx(ConfigUtils.getProperty("toDbUrl"));
        tx.open(ConfigUtils.getProperty("toDbUserName"), ConfigUtils.getProperty("toDbPassword"));
        log.info("Start to process data from cache...");
        startTime = System.currentTimeMillis();
        try {
            process(tx);
        } finally {
            log.info("Total use time : " + (System.currentTimeMillis() - s) + "ms");
            DbUtils.close(tx);
        }
    }

    protected abstract void process(ODatabaseDocumentTx tx);

}
