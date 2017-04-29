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
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasPhoneIncrementConsumer extends SnsAbstractTxConsumer {

    private static final String CREATE_HASPHONE_SQL = "create edge HasPhone from {0} to {1} retry 100";
    private static final String SELECT_HASPHONE_SQL = "select from (select expand(out_HasPhone) from {0}) where in = {1}";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_MEMBER_PHONERIDS.entrySet()) {
            String memberId = entry.getKey();
            String fromRid = snsService.getMemberRid(getODatabaseDocumentTx(), memberId);
            String phoneRids = entry.getValue();
            //Member-HasPhone->Phone
            String[] phoneRidArr = phoneRids.split("\\|");
            for (String toRid : phoneRidArr) {
                if (StringUtils.isNotBlank(fromRid)) {
                    OResultSet ocrs = execute(SELECT_HASPHONE_SQL, fromRid, toRid);
                    if (ocrs == null || ocrs.isEmpty()) {
                        execute(CREATE_HASPHONE_SQL, fromRid, toRid);
                    }
                }
            }
        }
    }

}
