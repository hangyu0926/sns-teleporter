package cn.memedai.orientdb.teleporter.sns.increment.consumer;

import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kisho on 2017/4/7.
 */
@Service("hasIpIncrementConsumer")
public class HasIpIncrementConsumer extends SnsAbstractTxConsumer {

    private String createApplyHasIp = "create edge ApplyHasIp from {0} to {1} retry 100";
    private String createOrderHasIp = "create edge OrderHasIp from {0} to {1} retry 100";
    private String createMemberHasIp = "create edge MemberHasIp from {0} to {1} retry 100";

    private String selectApplyHasIp = "select from (select expand(out_ApplyHasIp) from {0}) where in = {1}";
    private String selectOrderHasIp = "select from (select expand(out_OrderHasIp) from {0}) where in = {1}";
    private String selectMemberHasIp = "select from (select expand(out_MemberHasIp) from {0}) where in = {1}";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        Set<String> memberRidAndIpRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_IPRID.entrySet()) {
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyRid(applyNo);
            createEdge(createApplyHasIp, selectApplyHasIp, fromRid, toRid);

            String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_APPLYRID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndIpRidSet.add(memberRid + "|" + toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_IPRID.entrySet()) {
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderRid(orderNo);
            createEdge(createOrderHasIp, selectOrderHasIp, fromRid, toRid);

            String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_ORDERRID_MEMBERID.get(fromRid));
            if (StringUtils.isNotBlank(memberRid)) {
                memberRidAndIpRidSet.add(memberRid + "|" + toRid);
            }
        }

        if (!memberRidAndIpRidSet.isEmpty()) {
            for (String memberRidAndIpRid : memberRidAndIpRidSet) {
                String[] strArr = memberRidAndIpRid.split("\\|");
                String memberRid = strArr[0];
                String IpRid = strArr[1];
                //Member-MemberHasIp->Ip
                createEdge(createMemberHasIp, selectMemberHasIp, memberRid, IpRid);
            }
        }
    }


}
