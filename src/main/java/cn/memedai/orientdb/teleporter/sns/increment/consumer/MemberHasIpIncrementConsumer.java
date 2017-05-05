package cn.memedai.orientdb.teleporter.sns.increment.consumer;

import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsCommonAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class MemberHasIpIncrementConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createMemberHasIp}")
    private String createMemberHasIp;

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        Set<String> memberRidAndIpRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_IPRID.entrySet()) {
            String applyNo = entry.getKey();
            String applyRid = CacheUtils.getApplyRid(applyNo);
            if (StringUtils.isBlank(applyRid)) {
                continue;
            }
            String ipRid = entry.getValue();
            String memberId = CacheUtils.CACHE_APPLYRID_MEMBERID.get(applyRid);
            if (StringUtils.isBlank(memberId)) {
                continue;
            }
            memberRidAndIpRidSet.add(memberId + "|" + ipRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_IPRID.entrySet()) {
            String orderNo = entry.getKey();
            String ipRid = entry.getValue();
            String orderRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isBlank(orderRid)) {
                continue;
            }
            String memberId = CacheUtils.CACHE_ORDERRID_MEMBERID.get(orderRid);
            if (StringUtils.isBlank(memberId)) {
                continue;
            }
            memberRidAndIpRidSet.add(memberId + "|" + ipRid);
        }

        if (!memberRidAndIpRidSet.isEmpty()) {
            for (String memberRidAndIpRid : memberRidAndIpRidSet) {
                String[] strArr = memberRidAndIpRid.split("\\|");
                String memberId = strArr[0];
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), memberId);
                if (StringUtils.isBlank(memberRid)) {
                    continue;
                }
                String ipRid = strArr[1];
                createEdge(createMemberHasIp, "MemberHasIp", memberRid, ipRid);
            }
        }
    }


}
