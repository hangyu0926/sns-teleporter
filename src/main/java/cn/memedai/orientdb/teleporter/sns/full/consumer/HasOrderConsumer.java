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

import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasOrderConsumer extends SnsAbstractTxConsumer {

    private String createApplyHasOrder = "create edge ApplyHasOrder from {0} to {1} retry 100";
    private String createPhoneHasOrder = "create edge PhoneHasOrder from {0} to {1} retry 100";
    private String createMemberHasOrder = "create edge MemberHasOrder from {0} to {1} retry 100";

    @Override
    protected void process() {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERRID_MEMBERID.entrySet()) {
            String toRid = entry.getKey();
            String memberId = entry.getValue();
            String fromRid = CacheUtils.getMemberRid(memberId);
            if (StringUtils.isNotBlank(fromRid)) {
                execute(createMemberHasOrder, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERRID_PHONERID.entrySet()) {
            String toRid = entry.getKey();
            String fromRid = entry.getValue();
            execute(createPhoneHasOrder, fromRid, toRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERNO_APPLYINFORID.entrySet()) {
            String fromRid = entry.getValue();
            String orderNo = entry.getKey();
            String toRid = CacheUtils.getOrderRid(orderNo);
            if (StringUtils.isNotBlank(toRid)) {
                execute(createApplyHasOrder, fromRid, toRid);
            }
        }
    }

}
