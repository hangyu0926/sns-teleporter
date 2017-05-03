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

import cn.memedai.orientdb.teleporter.BlockingQueueDataBatchProcessConsumer;
import cn.memedai.orientdb.teleporter.OrientSqlUtils;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2Consumer extends BlockingQueueDataBatchProcessConsumer {

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(List<Object> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        List<String> orientSqls = new ArrayList(dataList.size());
        for (Object obj : dataList) {
            Map<String, Object> dataMap = (Map<String, Object>) obj;
            String applyNo = (String) dataMap.get("APPL_NO");
            String toPhone = (String) dataMap.get("PHONE_NO");
            String reportTime = (String) dataMap.get("CREATE_TIME");
            if (StringUtils.isBlank(applyNo) || StringUtils.isBlank(toPhone) || reportTime == null) {
                continue;
            }

            String toPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), toPhone);

            String fromPhone = CacheUtils.CACHE_APPLYNO_PHONE.get(applyNo);
            if (StringUtils.isBlank(fromPhone)) {
                continue;
            }
            String fromPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), fromPhone);
            if (StringUtils.isBlank(fromPhoneRid) || StringUtils.isBlank(toPhoneRid)) {
                continue;
            }

            orientSqls.add(snsService.constructCallToSql(fromPhoneRid, toPhoneRid, dataMap));
        }
        return OrientSqlUtils.executeBatch(getODatabaseDocumentTx(), orientSqls);
    }

}
