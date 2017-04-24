package cn.memedai.orientdb.teleporter.sns.main;

import cn.memedai.orientdb.teleporter.sns.consumer.DuplicatedPhoneCallTo2Consumer;
import cn.memedai.orientdb.teleporter.sns.consumer.vertex.PhoneWithCallTo2Consumer;
import cn.memedai.orientdb.teleporter.sns.producer.PhoneWithCallTo2Producer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 */
public class TeleporterTest extends Teleporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeleporterTest.class);

    private static final String SQL_PHONE = "update Phone set phone=? upsert return after where phone=?";

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
//        LOGGER.debug("I'm testing!");
//        final ODatabaseDocumentTx tx = new ODatabaseDocumentTx(ConfigUtils.getProperty("toDbUrl"));
//
//        tx.open(ConfigUtils.getProperty("toDbUserName"), ConfigUtils.getProperty("toDbPassword"));
//        System.out.println(tx + "");
//        System.out.println(getPhoneRid(tx,"13816749902"));

        submit(PhoneWithCallTo2Producer.class, PhoneWithCallTo2Consumer.class);
        blockUntilPreviousFinish();
        submit(DuplicatedPhoneCallTo2Consumer.class);
        blockUntilPreviousFinish();
    }

    static String getPhoneRid(ODatabaseDocumentTx tx, String phone) {
        String phoneRid = CacheUtils.getPhoneRid(phone);
        if (StringUtils.isBlank(phoneRid)) {
            phoneRid = getRid(execute(tx, SQL_PHONE, phone, phone));
            CacheUtils.setPhoneRid(phone, phoneRid);
        }
        return phoneRid;
    }

    static <RET> RET execute(ODatabaseDocumentTx tx, String sql, Object... args) {
        try {
            return tx.command(new OCommandSQL(sql)).execute(args);
        } catch (OConcurrentModificationException e) {
            return execute(tx, sql, args);
        } catch (Exception e) {
            LOGGER.error("{} @ {}", sql, e.getMessage());
            CacheUtils.COMMAND_SQL.add(sql);
        }
        return null;
    }

    protected String getValue(Object value) {
        return value == null ? "" : value.toString();
    }

    static String getRid(Object value) {
        if (value == null) {
            return null;
        }
        List<ODocument> result = (List<ODocument>) value;
        if (result != null && !result.isEmpty()) {
            return result.get(0).getIdentity().toString();
        }
        return null;
    }

    static ODatabaseDocumentTx getTx() {
        final ODatabaseDocumentTx tx = new ODatabaseDocumentTx(ConfigUtils.getProperty("toDbUrl"));
        tx.open(ConfigUtils.getProperty("toDbUserName"), ConfigUtils.getProperty("toDbPassword"));
        return tx;
    }

}


