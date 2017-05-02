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
import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasOrderIncrementConsumer extends SnsAbstractTxConsumer {

    private String createApplyHasOrder = "create edge ApplyHasOrder from {0} to {1} retry 100";
    private String createPhoneHasOrder = "create edge PhoneHasOrder from {0} to {1} retry 100";
    private String createMemberHasOrder = "create edge MemberHasOrder from {0} to {1} retry 100";

    private String selectApplyHasOrder = "select from (select expand(out_ApplyHasOrder) from {0}) where in = {1}";
    private String selectPhoneHasOrder = "select from (select expand(out_PhoneHasOrder) from {0}) where in = {1}";
    private String selectMemberHasOrder = "select from (select expand(out_MemberHasOrder) from {0}) where in = {1}";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERRID_MEMBERID.entrySet()) {
            String toRid = entry.getKey();
            String memberId = entry.getValue();
            String fromRid = snsService.getMemberRid(getODatabaseDocumentTx(), memberId);
            if (StringUtils.isNotBlank(fromRid)) {
                createEdge(createMemberHasOrder, selectMemberHasOrder, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERRID_PHONERID.entrySet()) {
            String toRid = entry.getKey();
            String fromRid = entry.getValue();
            if (StringUtils.isNotBlank(fromRid)) {
                createEdge(createPhoneHasOrder, selectPhoneHasOrder, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_APPLYINFORID.entrySet()) {
            String fromRid = entry.getValue();
            String orderNo = entry.getKey();
            String toRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isNotBlank(toRid)) {
                createEdge(createApplyHasOrder, selectApplyHasOrder, fromRid, toRid);
            }
        }
    }

}
