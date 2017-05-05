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

import cn.memedai.orientdb.teleporter.AbstractDataConsumer;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class CacheIpFullConsumer extends AbstractDataConsumer {

    @Value("#{snsOrientSqlProp.selectAllIp}")
    private String selectAllIp;

    public void run() {
        long startTime = System.currentTimeMillis();
        OResultSet ocrs = execute(selectAllIp, selectAllIp, null);
        if (ocrs != null && !ocrs.isEmpty()) {
            for (int i = 0; i < ocrs.size(); i++) {
                ODocument doc = (ODocument) ocrs.get(i);
                CacheUtils.setIpRid((String) doc.field("ip"), doc.field("@rid").toString());
            }
        }
        log.debug("cache Ip use time : {}ms", (System.currentTimeMillis() - startTime));
    }

}
