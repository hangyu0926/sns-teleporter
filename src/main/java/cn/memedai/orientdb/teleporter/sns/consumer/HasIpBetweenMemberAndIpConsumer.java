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
public class HasIpBetweenMemberAndIpConsumer extends AbstractTxConsumer {

    private static final String SELECT_MEMBER_BY_IP_SQL1 = "match {as:member,class:Member}-HasApply->{as:apply,class:ApplyInfo}-HasIp->{as:ip,class:Ip,where:(ip=?)} return member";
    private static final String SELECT_MEMBER_BY_IP_SQL2 = "match {as:member,class:Member}-HasOrder->{as:order,class:OrderInfo}-HasIp->{as:ip,class:Ip,where:(ip=?)} return member";

    private static final String SELECT_HAS_IP_SQL = "select from (select expand(out_HasIp) from {0}) where in = {1}";

    private static final String CREATE_HASIP_SQL = "create edge HasIp from {0} to {1}";

    protected void process(ODatabaseDocumentTx tx) {
        Set<String> memberRidAndIpRidSet = new HashSet<String>();
        String selectIpSql = "select from Ip";
        OResultSet ors = execute(tx, selectIpSql);
        if (ors != null && !ors.isEmpty()) {
            for (Object o : ors) {
                ODocument doc = (ODocument) o;
                ORecordId oRecordId = doc.field("@rid");
                String ipRid = oRecordId.getIdentity().toString();
                String ip = doc.field("ip");
                OResultSet ors1 = execute(tx, SELECT_MEMBER_BY_IP_SQL1, ip);
                if (ors1 != null && !ors1.isEmpty()) {
                    for (Object o1 : ors1) {
                        ODocument doc1 = (ODocument) o1;
                        ODocument doc11 = doc1.field("member");
                        ORecordId memberRid = doc11.field("@rid");
                        memberRidAndIpRidSet.add(memberRid.getIdentity().toString() + "|" + ipRid);
                    }
                }

                OResultSet ors2 = execute(tx, SELECT_MEMBER_BY_IP_SQL2, ip);
                if (ors2 != null && !ors2.isEmpty()) {
                    for (Object o2 : ors2) {
                        ODocument doc2 = (ODocument) o2;
                        ODocument doc11 = doc2.field("member");
                        ORecordId memberRid = doc11.field("@rid");
                        memberRidAndIpRidSet.add(memberRid.getIdentity().toString() + "|" + ipRid);
                    }
                }
            }
        }
        log.info("memberRidAndIpRidSet.size() : " + memberRidAndIpRidSet.size());
        if (!memberRidAndIpRidSet.isEmpty()) {
            for (String memberRidAndIpRid : memberRidAndIpRidSet) {
                String[] strArr = memberRidAndIpRid.split("\\|");
                String memberRid = strArr[0];
                String ipRid = strArr[1];
                OResultSet ocrs = execute(tx, MessageFormat.format(SELECT_HAS_IP_SQL, memberRid, ipRid));
                if (ocrs == null || ocrs.isEmpty()) {
                    execute(tx, MessageFormat.format(CREATE_HASIP_SQL, memberRid, ipRid));
                }
            }
        }
    }

}
