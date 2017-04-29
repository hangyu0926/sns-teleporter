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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by kisho on 2017/4/7.
 */
@Service
public class HasStoreIncrementConsumer extends SnsAbstractTxConsumer {

    private String createApplyHasStore = "create edge ApplyHasStore from {0} to {1} retry 100";
    private String createOrderHasStore = "create edge OrderHasStore from {0} to {1} retry 100";

    private String selectApplyHasStore = "select from (select expand(out_ApplyHasStore) from {0}) where in = {1}";
    private String selectOrderHasStore = "select from (select expand(out_OrderHasStore) from {0}) where in = {1}";

    @Resource
    private SnsService snsService;

    @Override
    protected void process() {
        for (Map.Entry<String, String> entry : CacheUtils.CACHE_APPLYINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            String toRid = CacheUtils.getStoreInfoRid(storeId);
            createEdge(createApplyHasStore, selectApplyHasStore, fromRid, toRid);
        }

        for (Map.Entry<String, String> entry : CacheUtils.CACHE_ORDERINFORID_STOREID.entrySet()) {
            String fromRid = entry.getKey();
            String storeId = entry.getValue();
            String toRid = snsService.getStoreRid(getODatabaseDocumentTx(), storeId);
            createEdge(createOrderHasStore, selectOrderHasStore, fromRid, toRid);
        }
    }

}
