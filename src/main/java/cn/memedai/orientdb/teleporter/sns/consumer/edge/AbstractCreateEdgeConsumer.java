package cn.memedai.orientdb.teleporter.sns.consumer.edge;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractTxConsumer;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import java.text.MessageFormat;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractCreateEdgeConsumer extends AbstractTxConsumer {

    @Override
    protected <RET> RET execute(ODatabaseDocumentTx tx, String sql, Object... args) {
        sql = MessageFormat.format(sql, args);
        return super.execute(tx, sql);
    }

}
