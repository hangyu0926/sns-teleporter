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

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2IncrementConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    @Value("#{snsOrientSqlProp.createCallTo}")
    private String createCallTo;

    @Value("#{snsOrientSqlProp.updateCallTo}")
    private String updateCallTo;

    @Value("#{snsOrientSqlProp.selectCallTo}")
    private String selectCallTo;


    @Override
    protected Object process(Object obj) {
        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String applyNo = (String) dataMap.get("APPL_NO");
        String toPhone = (String) dataMap.get("PHONE_NO");
        if (StringUtils.isBlank(applyNo) || StringUtils.isBlank(toPhone)) {
            return null;
        }

        String applyInfoRid = CacheUtils.getApplyRid(applyNo);
        if (StringUtils.isBlank(applyInfoRid)) {
            return null;
        }

        String toPhoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), toPhone);
        String fromPhoneRid = CacheUtils.getApplyRidPhoneRid(applyInfoRid);
        if (StringUtils.isBlank(fromPhoneRid) || StringUtils.isBlank(toPhoneRid)) {
            return null;
        }

        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("CREATE_TIME") == null ? null : dataMap.get("CREATE_TIME"),
        };

        OResultSet ocrs = execute(selectCallTo, MessageFormat.format(selectCallTo, fromPhoneRid, toPhoneRid), null);
        if (CollectionUtils.isEmpty(ocrs)) {
            execute(createCallTo, MessageFormat.format(createCallTo, fromPhoneRid, toPhoneRid), args);
        } else {
            ODocument doc = (ODocument) ocrs.get(0);
            ORecordId oRecordId = doc.field("@rid");
            execute(updateCallTo, MessageFormat.format(updateCallTo, oRecordId.getIdentity().toString()), args);
        }
        return null;
    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
