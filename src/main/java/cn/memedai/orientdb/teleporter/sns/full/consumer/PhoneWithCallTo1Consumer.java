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

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class PhoneWithCallTo1Consumer extends BlockingQueueDataConsumer {

    private static final String CREATE_CALL_TO_SQL = "create edge CallTo from {0} to {1} set callCnt = ?,callLen=?,callInCnt=?,callOutCnt=?,reportTime=? retry 100";

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String reportno = (String) dataMap.get("REPORTNO");
        String toPhone = (String) dataMap.get("PHONE_NUM");
        if (StringUtils.isBlank(reportno) || StringUtils.isBlank(toPhone)) {
            return null;
        }
        String fromPhone = CacheUtils.CACHE_REPORTERNO_PHONE.get(reportno);
        if (StringUtils.isBlank(fromPhone)) {
            return null;
        }

        String toPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), toPhone);
        String fromPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), fromPhone);
        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("REPORTTIME") == null ? null : dataMap.get("REPORTTIME"),
        };

        execute(MessageFormat.format(CREATE_CALL_TO_SQL, fromPhoneRid, toPhoneRid), args);

        return null;

    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
