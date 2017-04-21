package cn.memedai.orientdb.teleporter.sns.consumer;

import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import cn.memedai.orientdb.teleporter.sns.utils.ConfigUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class AbstractConsumer implements Runnable {

    private static final String UPDATE_PHONE_SQL = "update Phone set phone=? upsert return after where phone=?";

    private static final String SELECT_MEMBER_SQL = "select from Member where memberId = ?";

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected <RET> RET execute(ODatabaseDocumentTx tx, String sql, Object... args) {
        try {
            return tx.command(new OCommandSQL(sql)).execute(args);
        } catch (OConcurrentModificationException e) {
            return execute(tx, sql, args);
        } catch (Exception e) {
            log.error("{} @ {}", sql, e.getMessage());
            if (args != null && args.length > 0) {
                StringBuilder builder = new StringBuilder();
                for (Object arg : args) {
                    builder.append(arg).append(",");
                }
                sql += "|" + builder.toString().replaceAll(",$", "");
            }
            CacheUtils.COMMAND_SQL.add(sql);
        }
        return null;
    }

    protected String getPhoneRid(ODatabaseDocumentTx tx, String phone) {
        String phoneRid = CacheUtils.getPhoneRid(phone);
        if (StringUtils.isBlank(phoneRid)) {
            phoneRid = getRid(execute(tx, UPDATE_PHONE_SQL, phone, phone));
            CacheUtils.setPhoneRid(phone, phoneRid);
        }
        return phoneRid;
    }

    protected String getMemberRid(ODatabaseDocumentTx tx, String memberId) {
        if (StringUtils.isBlank(memberId)) {
            return null;
        }
        String memberRid = CacheUtils.getMemberRid(memberId);
        if (StringUtils.isBlank(memberRid)) {
            memberRid = getRid(execute(tx, SELECT_MEMBER_SQL, memberId));
        }
        return memberRid;
    }

    protected String getRid(Object value) {
        if (value == null) {
            return null;
        }
        List<ODocument> result = (List<ODocument>) value;
        if (result != null && !result.isEmpty()) {
            return result.get(0).getIdentity().toString();
        }
        return null;
    }

    protected ODatabaseDocumentTx getTx() {
        final ODatabaseDocumentTx tx = new ODatabaseDocumentTx(ConfigUtils.getProperty("toDbUrl"));
        tx.open(ConfigUtils.getProperty("toDbUserName"), ConfigUtils.getProperty("toDbPassword"));
        return tx;
    }

    protected String getValue(Object value) {
        return value == null ? "" : value.toString();
    }

}
