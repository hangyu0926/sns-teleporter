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
import cn.memedai.orientdb.teleporter.OrientSqlUtils;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * Created by kisho on 2017/4/27.
 */
public abstract class SnsCommonAbstractTxConsumer extends AbstractTxConsumer {

    @Override
    protected <RET> RET execute(String templateSql, String sql, Object[] args) {
        sql = MessageFormat.format(sql, args);
        return super.execute(templateSql, sql, null);
    }

    protected void createEdge(String createSql, String edgeName, String fromRid, String toRid) {
        if (StringUtils.isBlank(fromRid) || StringUtils.isBlank(toRid)) {
            return;
        }
        if (!OrientSqlUtils.checkEdgeIfExists(getODatabaseDocumentTx(), edgeName, fromRid, toRid)) {
            execute(createSql, createSql, new Object[]{fromRid, toRid});
        }
    }

}
