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

import cn.memedai.orientdb.teleporter.sns.common.consumer.SnsCommonAbstractTxConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class PhoneHasApplyIncrementConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createPhoneHasApply}")
    private String createPhoneHasApply;

    @Override
    protected void process() {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_PHONERID.entrySet()) {
            String fromRid = entry.getValue();
            String toRid = entry.getKey();
            createEdge(createPhoneHasApply, "PhoneHasApply", fromRid, toRid);
        }
    }

}
