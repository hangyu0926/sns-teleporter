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
package cn.memedai.orientdb.teleporter.sns.common.producer;

import cn.memedai.orientdb.teleporter.Constants;
import cn.memedai.orientdb.teleporter.JdbcDataProducer;

import java.util.List;
import java.util.Map;

/**
 * Created by kisho on 2017/4/6.
 */
public abstract class SnsCommonJdbcDataProducer extends JdbcDataProducer {

    protected static final String IN_DATA_LIST = "#inDataList";

    private Object[] inSqlData;

    @Override
    public void run() {
        try {
            onStart();
            int countPerTime = 1000;
            long startTime = System.currentTimeMillis();
            while (true) {
                int time = getAtomicInteger().getAndIncrement();
                int fromIndex = time * countPerTime;
                if (fromIndex > getInSqlData().length - 1) {
                    break;
                }
                int toIndex = fromIndex + countPerTime - 1;
                if (toIndex >= getInSqlData().length) {
                    toIndex = getInSqlData().length - 1;
                }
                StringBuilder inBuilder = new StringBuilder();
                for (int i = fromIndex; i <= toIndex; i++) {
                    inBuilder.append("'")
                            .append(getInSqlData()[i])
                            .append("'")
                            .append(",");
                }
                String inStr = inBuilder.toString().replaceAll(",$", "");
                String sql = getSql().replace(IN_DATA_LIST, inStr);
                List<Map<String, Object>> dataList = getJdbcTemplate().queryForList(sql);
                log.debug("{} @ {}ms", sql, (System.currentTimeMillis() - startTime));
                if (dataList == null
                        || dataList.isEmpty()) {
                    break;
                }
                for (Map<String, Object> dataMap : dataList) {
                    try {
                        getBlockingQueue().put(dataMap);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (dataList.size() < countPerTime) {
                    break;
                }
                onEnd();
            }
        } finally {
            try {
                //发送银弹，通知消费者结束了
                getBlockingQueue().put(Constants.SILVER_BOLT);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public Object[] getInSqlData() {
        return inSqlData;
    }

    public void setInSqlData(Object[] inSqlData) {
        this.inSqlData = inSqlData;
    }

}
