package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OConcurrentResultSet;
import com.orientechnologies.orient.core.sql.query.OResultSet;

/**
 * Created by kisho on 2017/4/6.
 */
public class CacheIpConsumer extends AbstractConsumer {


    public void run() {
        long startTime = System.currentTimeMillis();
        OResultSet ocrs = execute(getTx(), "select from Ip");
        if (ocrs != null && !ocrs.isEmpty()) {
            for (int i = 0; i < ocrs.size(); i++) {
                ODocument doc = (ODocument) ocrs.get(i);
                CacheUtils.setIpRid((String) doc.field("ip"), doc.field("@rid").toString());
            }
        }
        log.debug("cache Ip use time : {}ms", (System.currentTimeMillis() - startTime));
    }

}
