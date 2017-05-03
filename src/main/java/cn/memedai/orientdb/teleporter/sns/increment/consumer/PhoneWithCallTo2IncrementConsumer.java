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

import cn.memedai.orientdb.teleporter.BlockingQueueDataBatchProcessConsumer;
import cn.memedai.orientdb.teleporter.OrientSqlUtils;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2IncrementConsumer extends BlockingQueueDataBatchProcessConsumer {

    @Resource
    private SnsService snsService;

    private static final String CREATE_CALL_TO_SQL = "create edge CallTo from {0} to {1} set callCnt = #callCnt,callLen=#callLen,callInCnt=#callInCnt,callOutCnt=#callOutCnt,reportTime=#reportTime";

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
            String createTime = dataMap.get("CREATE_TIME") == null ? null : dataMap.get("CREATE_TIME").toString();
            if (StringUtils.isBlank(applyNo) || StringUtils.isBlank(toPhone) || createTime == null) {
                continue;
            }

            String applyInfoRid = CacheUtils.getApplyRid(applyNo);
            if (StringUtils.isBlank(applyInfoRid)) {
                continue;
            }

            String toPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), toPhone);
            String fromPhoneRid = CacheUtils.getApplyRidPhoneRid(applyInfoRid);
            if (StringUtils.isBlank(fromPhoneRid) || StringUtils.isBlank(toPhoneRid)) {
                continue;
            }

            String templateSql = MessageFormat.format(CREATE_CALL_TO_SQL, fromPhoneRid, toPhoneRid);
            String orientSql = templateSql.replace("#callCnt", getValue(dataMap.get("CALL_CNT"))).
                    replace("#callLen", getValue(dataMap.get("CALL_LEN"))).
                    replace("#callInCnt", getValue(dataMap.get("CALL_IN_CNT")))
                    .replace("#callOutCnt", getValue(dataMap.get("CALL_OUT_CNT")))
                    .replace("#reportTime", "'" + dataMap.get("CREATE_TIME").toString() + "'");
            orientSqls.add(orientSql);
        }
        return OrientSqlUtils.executeBatch(getODatabaseDocumentTx(), orientSqls);
    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
