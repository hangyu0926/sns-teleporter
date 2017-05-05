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
public class MemberHasDeviceIncrementConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createMemberHasDevice}")
    private String createMemberHasDevice;

    @Resource
    private SnsService snsService;

    protected void process() {
        Set<String> memberIdAndDeviceRidSet = new HashSet<>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_DEVICERID.entrySet()) {
            String applyNo = entry.getKey();
            String applyRid = CacheUtils.getApplyRid(applyNo);
            if (StringUtils.isBlank(applyRid)) {
                continue;
            }
            String deviceRid = entry.getValue();
            String memberId = CacheUtils.CACHE_APPLYRID_MEMBERID.get(applyRid);
            if (StringUtils.isBlank(memberId)) {
                continue;
            }
            memberIdAndDeviceRidSet.add(memberId + "|" + deviceRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_DEVICERID.entrySet()) {
            String orderNo = entry.getKey();
            String deviceRid = entry.getValue();
            String orderRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isBlank(orderRid)) {
                continue;
            }
            String memberId = CacheUtils.CACHE_ORDERRID_MEMBERID.get(orderRid);
            if (StringUtils.isBlank(memberId)) {
                continue;
            }
            memberIdAndDeviceRidSet.add(memberId + "|" + deviceRid);
        }

        if (!memberIdAndDeviceRidSet.isEmpty()) {
            for (String memberRidAndDeviceRid : memberIdAndDeviceRidSet) {
                String[] strArr = memberRidAndDeviceRid.split("\\|");
                String memberId = strArr[0];
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), memberId);
                if (StringUtils.isBlank(memberRid)) {
                    continue;
                }
                String deviceRid = strArr[1];
                createEdge(createMemberHasDevice, "MemberHasDevice", memberRid, deviceRid);
            }
        }
    }

}
