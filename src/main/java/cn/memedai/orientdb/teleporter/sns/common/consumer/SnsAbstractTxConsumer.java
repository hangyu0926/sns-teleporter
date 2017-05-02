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

import cn.memedai.orientdb.teleporter.AbstractTxConsumer;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * Created by kisho on 2017/4/27.
 */
public abstract class SnsAbstractTxConsumer extends AbstractTxConsumer {

    @Override
    protected <RET> RET execute(String sql, Object... args) {
        sql = MessageFormat.format(sql, args);
        return super.execute(sql);
    }

    protected void createEdge(String createSql, String selectSql, String fromRid, String toRid) {
        if (StringUtils.isBlank(fromRid) || StringUtils.isBlank(toRid)) {
            return;
        }
        OResultSet ocrs = execute(selectSql, fromRid, toRid);
        if (ocrs == null || ocrs.isEmpty()) {
            execute(createSql, fromRid, toRid);
        }
    }

}
