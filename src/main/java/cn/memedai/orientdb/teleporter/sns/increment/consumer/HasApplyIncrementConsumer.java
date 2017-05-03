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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasApplyIncrementConsumer extends SnsCommonAbstractTxConsumer {

    private String createPhoneHasApply = "create edge PhoneHasApply from {0} to {1} retry 100";
    private String createMemberHasApply = "create edge MemberHasApply from {0} to {1} retry 100";

    private String selectPhoneHasApply = "select from (select expand(out_PhoneHasApply) from {0}) where in = {1}";
    private String selectMemberHasApply = "select from (select expand(out_MemberHasApply) from {0}) where in = {1}";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_MEMBERID.entrySet()) {
            String memberId = entry.getValue();
            String fromRid = snsService.getMemberRid(getODatabaseDocumentTx(), memberId);
            if (StringUtils.isBlank(fromRid)) {
                continue;
            }
            String toRid = entry.getKey();
            createEdge(createMemberHasApply, selectMemberHasApply, fromRid, toRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_PHONERID.entrySet()) {
            String fromRid = entry.getValue();
            String toRid = entry.getKey();
            createEdge(createPhoneHasApply, selectPhoneHasApply, fromRid, toRid);
        }
    }

}
