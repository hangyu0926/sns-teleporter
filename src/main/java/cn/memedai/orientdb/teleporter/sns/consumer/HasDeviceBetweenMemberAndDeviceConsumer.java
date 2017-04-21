package cn.memedai.orientdb.teleporter.sns.consumer;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kisho on 2017/4/6.
 */
public class HasDeviceBetweenMemberAndDeviceConsumer extends AbstractTxConsumer {

    private static final String SELECT_MEMBER_BY_DEVICE_SQL1 = "match {as:member,class:Member}-HasApply->{as:apply,class:ApplyInfo}-HasDevice->{as:device,class:Device,where:(deviceId=?)} return member";
    private static final String SELECT_MEMBER_BY_DEVICE_SQL2 = "match {as:member,class:Member}-HasOrder->{as:order,class:OrderInfo}-HasDevice->{as:device,class:Device,where:(deviceId=?)} return member";

    private static final String SELECT_HAS_DEVICE_SQL = "select from (select expand(out_HasDevice) from {0}) where in = {1}";

    private static final String CREATE_HASDEVICE_SQL = "create edge HasDevice from {0} to {1}";

    protected void process(ODatabaseDocumentTx tx) {
        Set<String> memberRidAndDeviceRidSet = new HashSet<String>();
        String selectDeviceSql = "select from Device";
        long startTime = System.currentTimeMillis();
        OResultSet ors = execute(tx, selectDeviceSql);
        if (ors != null && !ors.isEmpty()) {
            log.debug("{}@{}ms", selectDeviceSql, (System.currentTimeMillis() - startTime));
            for (Object o : ors) {
                ODocument doc = (ODocument) o;
                ORecordId oRecordId = doc.field("@rid");
                String deviceRid = oRecordId.getIdentity().toString();
                String deviceId = doc.field("deviceId");
                OResultSet ors1 = execute(tx, SELECT_MEMBER_BY_DEVICE_SQL1, deviceId);
                if (ors1 != null && !ors1.isEmpty()) {
                    for (Object o1 : ors1) {
                        ODocument doc1 = (ODocument) o1;
                        ODocument doc11 = doc1.field("member");
                        ORecordId memberRid = doc11.field("@rid");
                        memberRidAndDeviceRidSet.add(memberRid.getIdentity().toString() + "|" + deviceRid);
                    }
                }

                OResultSet ors2 = execute(tx, SELECT_MEMBER_BY_DEVICE_SQL2, deviceId);
                if (ors2 != null && !ors2.isEmpty()) {
                    for (Object o2 : ors2) {
                        ODocument doc2 = (ODocument) o2;
                        ODocument doc11 = doc2.field("member");
                        ORecordId memberRid = doc11.field("@rid");
                        memberRidAndDeviceRidSet.add(memberRid.getIdentity().toString() + "|" + deviceRid);
                    }
                }
            }
        }

        log.info("memberRidAndDeviceRidSet.size() : " + memberRidAndDeviceRidSet.size());
        if (!memberRidAndDeviceRidSet.isEmpty()) {
            for (String memberRidAndIpRid : memberRidAndDeviceRidSet) {
                String[] strArr = memberRidAndIpRid.split("\\|");
                String memberRid = strArr[0];
                String deviceRid = strArr[1];
                OResultSet ocrs = execute(tx, MessageFormat.format(SELECT_HAS_DEVICE_SQL, memberRid, deviceRid));
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, MessageFormat.format(CREATE_HASDEVICE_SQL, memberRid, deviceRid));
                }
            }
        }
    }

}
