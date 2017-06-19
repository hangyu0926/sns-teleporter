/*
 *
 *  * -------------------------------------------------------------------------------------
 *  * Mi-Me Confidential
 *  *
 *  * Copyright (C) 2015 Shanghai Mi-Me Financial Information Service Co., Ltd.
 *  * All rights reserved.
 *  *
 *  * No part of this file may be reproduced or transmitted in any form or by any means,
 *  * electronic, mechanical, photocopying, recording, or otherwise, without prior
 *  * written permission of Shanghai Mi-Me Financial Information Service Co., Ltd.
 *  * -------------------------------------------------------------------------------------
 *
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
public class CashLoanApplyCommonConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("apply_no");
        Object status = CacheUtils.CASH_LOAN_CA_RESULT.get(applyNo);
        String originalStatus = status == null ? "0" : status.toString();
        dataMap.put("status", status);
        dataMap.put("original_status", originalStatus);
        Object docObj = super.process(dataMap);


        String applyInfoRid = getRid(docObj);
        CacheUtils.setApplyRid(applyNo, applyInfoRid);

        String orderNo = (String) dataMap.get("order_no");
        if (StringUtils.isNotBlank(orderNo)) {
            CacheUtils.setOrderNoApplyRid(orderNo, applyInfoRid);
        }

        String storeId = (String) dataMap.get("store_id");
        if (StringUtils.isNotBlank(storeId)) {
            CacheUtils.setApplyRidStoreId(applyInfoRid, storeId);
        }

        String memberId = dataMap.get("member_id").toString();
        String phone = (String) dataMap.get("cellphone");
        if (StringUtils.isNotBlank(phone)) {
            String phoneRid = snsService.processMemberAndPhone(getODatabaseDocumentTx(), memberId, phone);
            if (StringUtils.isNotBlank(phoneRid)) {
                CacheUtils.setApplyRidPhoneRid(applyInfoRid, phoneRid);
            }
        }

        CacheUtils.setApplyRidMemberId(applyInfoRid, memberId);

        return getFirstODocumnet(docObj);
    }

}
