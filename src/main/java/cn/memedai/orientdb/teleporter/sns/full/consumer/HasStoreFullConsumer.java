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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasStoreFullConsumer extends SnsCommonAbstractTxConsumer {

    @Value("#{snsOrientSqlProp.createApplyHasStore}")
    private String createApplyHasStore;

    @Value("#{snsOrientSqlProp.createOrderHasStore}")
    private String createOrderHasStore;

    @Override
    protected void process() {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYRID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            //ApplyInfo-HasStore->StoreInfo
            String toRid = CacheUtils.getStoreRid(storeId);
            if (StringUtils.isBlank(toRid)) {
                log.info("storeId : " + storeId + " does not exist!");
            } else {
                execute(createApplyHasStore, createApplyHasStore, new Object[]{fromRid, toRid});
            }
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERRID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            //OrderInfo-HasStore->StoreInfo
            String toRid = CacheUtils.getStoreRid(storeId);
            if (StringUtils.isBlank(toRid)) {
                log.info("storeId : " + storeId + " does not exist!");
            } else {
                execute(createOrderHasStore, createOrderHasStore, new Object[]{fromRid, toRid});
            }
        }
    }

}
