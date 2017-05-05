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
package cn.memedai.orientdb.teleporter.sns.common.consumer;

import cn.memedai.orientdb.teleporter.BlockingQueueDataConsumer;
import cn.memedai.orientdb.teleporter.OrientSqlUtils;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */

public class PhoneAndPhoneMarkAndPhoneSourceCommonConsumer extends BlockingQueueDataConsumer {


    @Value("#{snsOrientSqlProp.updatePhoneMark}")
    private String updatePhoneMark;

    @Value("#{snsOrientSqlProp.updatePhoneSource}")
    private String updatePhoneSource;

    @Value("#{snsOrientSqlProp.createHasPhoneMark}")
    private String createHasPhoneMark;

    @Value("#{snsOrientSqlProp.createHasPhoneSource}")
    private String createHasPhoneSource;

    @Resource
    private SnsService snsService;

    @Override
    protected Object process(Object obj) {

        Map<String, Object> dataMap = (Map<String, Object>) obj;
        String phone = (String) dataMap.get("PHONE_NO");
        if (StringUtils.isBlank(phone)) {
            return null;
        }

        String phoneRid = snsService.getPhoneRid(getODatabaseDocumentTx(), phone);
        if (StringUtils.isBlank(phoneRid)) {
            return null;
        }

        String mark = (String) dataMap.get("PHONE_TYPE");
        if (StringUtils.isNotBlank(mark)) {
            String markRid = CacheUtils.getPhoneMarkRid(mark);
            if (StringUtils.isBlank(markRid)) {
                markRid = getRid(execute(updatePhoneMark, updatePhoneMark, new Object[]{mark, mark}));
                if (StringUtils.isNotBlank(markRid)) {
                    CacheUtils.setPhoneMarkRid(mark, markRid);
                }
            }
            if (!OrientSqlUtils.checkEdgeIfExists(getODatabaseDocumentTx(), "HasPhoneMark", phoneRid, markRid)) {
                execute(createHasPhoneMark, MessageFormat.format(createHasPhoneMark, phoneRid, markRid), null);
            }
        }

        String source = (String) dataMap.get("SOURCE");
        if (StringUtils.isNotBlank(source)) {
            String sourceRid = CacheUtils.getPhoneSourceRid(source);
            if (StringUtils.isBlank(sourceRid)) {
                sourceRid = getRid(execute(updatePhoneSource, updatePhoneSource, new Object[]{source, source}));
                if (StringUtils.isNotBlank(sourceRid)) {
                    CacheUtils.setPhoneSourceRid(source, sourceRid);
                }
            }
            if (!OrientSqlUtils.checkEdgeIfExists(getODatabaseDocumentTx(), "HasPhoneSource", phoneRid, sourceRid)) {
                execute(createHasPhoneSource, MessageFormat.format(createHasPhoneSource, phoneRid, sourceRid), null);
            }
        }

        return null;
    }

}
