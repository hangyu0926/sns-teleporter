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

import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsCommonAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasApplyFullConsumer extends SnsCommonAbstractTxConsumer {

    private String createMemberHasApply = "create edge MemberHasApply from {0} to {1} retry 100";
    private String createPhoneHasApply = "create edge PhoneHasApply from {0} to {1} retry 100";

    @Override
    protected void process() {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_MEMBERID.entrySet()) {
            String memberId = entry.getValue();
            String fromRid = CacheUtils.getMemberRid(memberId);
            String toRid = entry.getKey();
            if (StringUtils.isNotBlank(fromRid)) {
                //Member-MemberHasApply->ApplyInfo
                execute(createMemberHasApply, fromRid, toRid);
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_PHONERID.entrySet()) {
            String fromRid = entry.getValue();
            String toRid = entry.getKey();
            //Phone-PhoneHasApply->ApplyInfo
            execute(createPhoneHasApply, fromRid, toRid);
        }
    }

}
