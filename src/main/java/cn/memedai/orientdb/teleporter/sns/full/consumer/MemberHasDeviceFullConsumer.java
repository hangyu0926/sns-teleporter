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
public class MemberHasDeviceFullConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createMemberHasDevice}")
    private String createMemberHasDevice;

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        Set<String> memberRidAndDeviceRidSet = new HashSet<String>();
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYNO_DEVICERID.entrySet()) {
            String applyNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getApplyRid(applyNo);
            if (StringUtils.isNotBlank(fromRid)) {
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_APPLYRID_MEMBERID.get(fromRid));
                if (StringUtils.isNotBlank(memberRid)) {
                    memberRidAndDeviceRidSet.add(memberRid + "|" + toRid);
                }
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_DEVICERID.entrySet()) {
            String orderNo = entry.getKey();
            String toRid = entry.getValue();
            String fromRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isNotBlank(fromRid)) {
                String memberRid = snsService.getMemberRid(getODatabaseDocumentTx(), CacheUtils.CACHE_ORDERRID_MEMBERID.get(fromRid));
                if (StringUtils.isNotBlank(memberRid)) {
                    memberRidAndDeviceRidSet.add(memberRid + "|" + toRid);
                }
            }
        }

        if (!memberRidAndDeviceRidSet.isEmpty()) {
            for (String memberRidAndDeviceRid : memberRidAndDeviceRidSet) {
                String[] strArr = memberRidAndDeviceRid.split("\\|");
                String memberRid = strArr[0];
                String deviceRid = strArr[1];
                //Member-MemberHasDevice->Device
                execute(createMemberHasDevice, createMemberHasDevice, new Object[]{memberRid, deviceRid});
            }
        }
    }

}
