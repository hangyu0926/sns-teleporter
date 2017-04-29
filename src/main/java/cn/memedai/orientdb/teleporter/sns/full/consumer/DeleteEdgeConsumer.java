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
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import org.springframework.stereotype.Service;

/**
 * Created by kisho on 2017/4/6.
 */
@Service
public class DeleteEdgeConsumer extends AbstractDataConsumer {

    private String[] edges;

    public void run() {
        long startTime = System.currentTimeMillis();
        ODatabaseDocumentTx tx = null;
        try {
            if (edges == null || edges.length == 0) {
                return;
            }
            tx = getODatabaseDocumentTx();
            for (String edge : edges) {
                long s = System.currentTimeMillis();
                String sql = "delete edge " + edge;
                log.debug("start to execute delete sql : " + sql);
                execute(sql);
                log.debug("{}@{}ms", sql, (System.currentTimeMillis() - s));
            }
        } finally {
            log.debug("final use time : {}ms", (System.currentTimeMillis() - startTime));
            if (tx != null) {
                tx.close();
            }
        }
    }

    public void setEdges(String[] edges) {
        this.edges = edges;
    }

}
