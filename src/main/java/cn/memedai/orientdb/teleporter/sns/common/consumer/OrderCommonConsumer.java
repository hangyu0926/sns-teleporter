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
package cn.memedai.orientdb.teleporter.sns.common.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class OrderCommonConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(Object obj) {
        Object docObj = super.process(obj);
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String orderNo = (String) dataMap.get("order_no");
        String orderInfoRid = getRid(docObj);
        CacheUtils.setOrderRid(orderNo, orderInfoRid);

        String storeId = (String) dataMap.get("store_id");
        if (StringUtils.isNotBlank(storeId)) {
            CacheUtils.setOrderRidStoreId(orderInfoRid, storeId);
        }

        String memberId = dataMap.get("member_id").toString();
        String phone = (String) dataMap.get("mobile");
        if (StringUtils.isNotBlank(phone)) {
            String phoneRid = snsService.processMemberAndPhone(getODatabaseDocumentTx(), memberId, phone);
            if (StringUtils.isNotBlank(phoneRid)) {
                CacheUtils.setOrderRidPhoneRid(orderInfoRid, phoneRid);
            }
        }

        CacheUtils.setOrderRidMemberId(orderInfoRid, memberId);

        return getFirstODocumnet(docObj);
    }

}
