package cn.memedai.orientdb.teleporter.sns.consumer.vertex;

import cn.memedai.orientdb.teleporter.sns.consumer.AbstractBlockingQueueConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class MemberConsumer extends AbstractBlockingQueueConsumer {

    private static final String SQL_MEMBER = "update Member set memberId=?,name=?,idNo=?,isBlack=? upsert return after where memberId=?";

    protected void process(ODatabaseDocumentTx tx, Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String memberId = dataMap.get("MEMBER_ID").toString();

        //vertex: Member
        Object[] args = new Object[]{
                memberId,
                getValue(dataMap.get("NAME")),
                getValue(dataMap.get("ID_NO")),
                false,
                memberId
        };
        String memberRid = getRid(execute(tx, SQL_MEMBER, args));
        if (memberRid != null) {
            CacheUtils.setMemberRid(memberId, memberRid);
        }

        String phone = (String) dataMap.get("MOBILE_NO");
        processMemberAndPhone(tx, memberId, phone);
    }


}
