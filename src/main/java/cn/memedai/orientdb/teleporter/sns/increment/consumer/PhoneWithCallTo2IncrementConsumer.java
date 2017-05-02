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
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public class PhoneWithCallTo2IncrementConsumer extends BlockingQueueDataConsumer {

    @Resource
    private SnsService snsService;

    private static final String CREATE_CALL_TO_SQL = "create edge CallTo from {0} to {1} set callCnt = ?,callLen=?,callInCnt=?,callOutCnt=?,reportTime=? retry 100";

    private static final String SELECT_CALLTO_SQL = "select from (select expand(out_CallTo) from {0}) where in = {1}";

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

//        OResultSet ocrs = execute(MessageFormat.format(SELECT_CALLTO_SQL, fromPhoneRid, toPhoneRid));
//        if (ocrs == null || ocrs.isEmpty()) {
//            execute(, fromRid, toRid);
//        }

        Object[] args = new Object[]{
                getValue(dataMap.get("CALL_CNT")),
                getValue(dataMap.get("CALL_LEN")),
                getValue(dataMap.get("CALL_IN_CNT")),
                getValue(dataMap.get("CALL_OUT_CNT")),
                dataMap.get("CREATE_TIME") == null ? null : dataMap.get("CREATE_TIME"),
        };

        execute(MessageFormat.format(CREATE_CALL_TO_SQL, fromPhoneRid, toPhoneRid), args);
        return null;
    }

    protected String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
