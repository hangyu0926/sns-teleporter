package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.DbUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Created by kisho on 2017/4/6.
 */
public class DeleteEdgeConsumer extends AbstractConsumer {

    private String[] edges;

    public void run() {
        long startTime = System.currentTimeMillis();
        ODatabaseDocumentTx tx = null;
        try {
            if (edges == null || edges.length == 0) {
                return;
            }
            tx = getTx();
            for (String edge : edges) {
                long s = System.currentTimeMillis();
                String sql = "delete edge " + edge;
                log.debug("start to execute delete sql : " + sql);
                execute(tx, sql);
                log.debug("{}@{}ms", sql, (System.currentTimeMillis() - s));
            }
        } finally {
            log.debug("final use time : {}ms", (System.currentTimeMillis() - startTime));
            DbUtils.close(tx);
        }
    }

    public void setEdges(String[] edges) {
        this.edges = edges;
    }

}
