package cn.memedai.orientdb.teleporter.sns.consumer;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Created by kisho on 2017/4/6.
 */
public class CommandSqlConsumer extends AbstractBlockingQueueConsumer {

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        execute(tx, (String)obj);
    }

}
