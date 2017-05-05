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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class PhoneWithCallTo2FullConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Value("#{snsOrientSqlProp.createCallTo}")
    private String createCallTo;

    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");
        String toPhone = (String) dataMap.get("PHONE_NO");
        if (StringUtils.isBlank(applyNo) || StringUtils.isBlank(toPhone)) {
            return null;
        }

        String fromPhone = CacheUtils.CACHE_APPLYNO_PHONE.get(applyNo);
        if (StringUtils.isBlank(fromPhone)) {
            return null;
        }

        String fromPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), fromPhone);
        String toPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), toPhone);
        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("CREATE_TIME") == null ? null : dataMap.get("CREATE_TIME"),
        };

        execute(createCallTo, MessageFormat.format(createCallTo, fromPhoneRid, toPhoneRid), args);
        return null;
    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
