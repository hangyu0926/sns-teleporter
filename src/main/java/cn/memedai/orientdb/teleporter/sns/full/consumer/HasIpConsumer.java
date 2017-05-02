/*
 * -------------------------------------------------------------------------------------
 * Mi-Me Confidential
 *
 * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 * All rights reserved.
 *
 * No part of this file may be reproduced or transmitted in any form or by any means,
 * electronic, mechanical, photocopying, recording, or otherwise, without prior
 * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 * -------------------------------------------------------------------------------------
 */
package cn.memedai.orientdb.teleporter.sns.full.consumer;

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
@Service
public class HasIpConsumer extends SnsAbstractTxConsumer {

    private String createApplyHasIp = "create edge ApplyHasIp from {0} to {1} retry 100";
    private String createOrderHasIp = "create edge OrderHasIp from {0} to {1} retry 100";
    private String createMemberHasIp = "create edge MemberHasIp from {0} to {1} retry 100";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        Set<String> memberRidAndIpRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_IPRID.entrySet()) {
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyRid(applyNo);
            if (StringUtils.isNotBlank(fromRid)) {
                execute(createApplyHasIp, fromRid, toRid);
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_APPLYRID_MEMBERID.get(fromRid));
                if (StringUtils.isNotBlank(memberRid)) {
                    memberRidAndIpRidSet.add(memberRid + "|" + toRid);
                }
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_IPRID.entrySet()) {
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isNotBlank(fromRid)) {
                execute(createOrderHasIp, fromRid, toRid);
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_ORDERRID_MEMBERID.get(fromRid));
                if (StringUtils.isNotBlank(memberRid)) {
                    memberRidAndIpRidSet.add(memberRid + "|" + toRid);
                }
            }
        }

        if (!memberRidAndIpRidSet.isEmpty()) {
            for (String memberRidAndIpRid : memberRidAndIpRidSet) {
                String[] strArr = memberRidAndIpRid.split("\\|");
                String memberRid = strArr[0];
                String IpRid = strArr[1];
                //Member-MemberHasIp->Ip
                execute(createMemberHasIp, memberRid, IpRid);
            }
        }
    }

}
