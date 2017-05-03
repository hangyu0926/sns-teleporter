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

import cn.memedai.orientdb.teleporter.JdbcDataProducer;
import cn.memedai.orientdb.teleporter.sns.common.SnsService;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * Created by kisho on 2017/4/6.
 */
@Resource
public class PhoneWithCallTo2CommonProducer extends JdbcDataProducer {

    @Resource
    private Properties snsProp;

    @Resource
    private SnsService snsService;

    private int lastMaxId;

    @Override
    protected void onStart() {
        String filePath = snsProp.getProperty("callToLastReadRecordPath");
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lastMax = prop.getProperty(getLastKey());
        if (StringUtils.isNotBlank(lastMax)) {
            lastMaxId = Integer.parseInt(lastMax);
        }
        log.debug("lastMaxId = " + lastMax);
        String subSql = null;
        if (lastMaxId > 0) {
            subSql = " ID>" + lastMaxId;
        } else {
            subSql = " CREATE_TIME>='" + snsService.getStartDatetime(null, 1) + "'";
        }
        setSql(getSql().replaceAll("#subSql", subSql));
    }

    @Override
    protected void onEnd() {
        int currentMaxId = getJdbcTemplate().queryForObject("select max(ID) from network.ca_bur_operator_contact", Integer.class);
        log.debug("currentMaxId : " + currentMaxId);
        String filePath = snsProp.getProperty("callToLastReadRecordPath");
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            //写入本次读取最大的id值
            PrintWriter writer = new PrintWriter(file);
            writer.println(getLastKey() + "=" + lastMaxId);
            writer.print(getCurrentKey() + "=" + currentMaxId);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getLastKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Calendar.getInstance().getTimeInMillis() - 3600 * 24 * 1000);
        return sdf.format(date);
    }

    private static String getCurrentKey() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

}
