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
package cn.memedai.orientdb.teleporter.sns.common;

import cn.memedai.orientdb.teleporter.OrientSqlUtils;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kisho on 2017/4/27.
 */
@Service
public class SnsService {

    @Value("#{snsOrientSqlProp.updateDevice}")
    private String updateDevice;

    @Value("#{snsOrientSqlProp.updateIp}")
    private String updateIp;

    @Value("#{snsOrientSqlProp.updatePhone}")
    private String updatePhone;

    @Value("#{snsOrientSqlProp.selectStore}")
    private String selectStore;

    @Value("#{snsOrientSqlProp.selectMember}")
    private String selectMember;

    @Value("#{snsOrientSqlProp.createCallTo2}")
    private String createCallTo2;

    private Lock phoneLock = new ReentrantLock();

    public String processMemberAndPhone(ODatabaseDocumentTx tx,
                                        String memberId,
                                        String phone) {
        if (StringUtils.isBlank(memberId) || StringUtils.isBlank(phone)) {
            return null;
        }
        //Phone
        String phoneRid = getPhoneRid(tx, phone);

        if (StringUtils.isBlank(phoneRid)) {
            return null;
        }
        //memberId->Phone
        String phoneRids = CacheUtils.getMemberPhoneRids(memberId);
        if (StringUtils.isBlank(phoneRids)) {
            CacheUtils.setMemberPhoneRids(memberId, phoneRid);
        } else {
            if (!phoneRids.contains(phoneRid)) {
                CacheUtils.setMemberPhoneRids(memberId, phoneRids + "|" + phoneRid);
            }
        }
        return phoneRid;
    }

    public String processApplyAndIp(ODatabaseDocumentTx tx,
                                    String applyNo,
                                    String ip,
                                    String ipCity) {
        String ipRid = null;
        if (StringUtils.isNotBlank(ip)) {
            ipRid = CacheUtils.getIpRid(ip);
            if (StringUtils.isBlank(ipRid)) {
                ipRid = getRid(execute(tx, updateIp, updateIp, new Object[]{ip, ipCity, ip}));
                if (StringUtils.isNotBlank(ipRid)) {
                    CacheUtils.setIpRid(ip, ipRid);
                }
            }
            if (StringUtils.isNotBlank(applyNo)) {
                CacheUtils.setApplyNoIpRid(applyNo, ipRid);
            }
        }
        return ipRid;
    }

    public String processApplyAndDevice(ODatabaseDocumentTx tx,
                                        String applyNo,
                                        String deviceId) {
        String deviceRid = null;
        if (StringUtils.isNotBlank(deviceId)) {
            deviceRid = CacheUtils.getDeviceRid(deviceId);
            if (StringUtils.isBlank(deviceRid)) {
                deviceRid = getRid(execute(tx, updateDevice, updateDevice, new Object[]{deviceId, deviceId}));
                if (StringUtils.isNotBlank(deviceRid)) {
                    CacheUtils.setDeviceRid(deviceId, deviceRid);
                }
            }
            if (StringUtils.isNotBlank(applyNo)) {
                CacheUtils.setApplyNoDeviceRid(applyNo, deviceRid);
            }
        }
        return deviceRid;
    }


    public String getPhoneRid(ODatabaseDocumentTx tx, String phone) {
        String phoneRid = CacheUtils.getPhoneRid(phone);
        if (StringUtils.isBlank(phoneRid)) {
            phoneLock.lock();
            try {
                phoneRid = getRid(execute(tx, updatePhone, updatePhone, new Object[]{phone, phone}));
                if (StringUtils.isNotBlank(phoneRid)) {
                    CacheUtils.setPhoneRid(phone, phoneRid);
                }
            } finally {
                phoneLock.unlock();
            }
        }
        return phoneRid;
    }

    public String getStoreRid(ODatabaseDocumentTx tx, String storeId) {
        String storeRid = CacheUtils.getStoreRid(storeId);
        if (StringUtils.isBlank(storeRid)) {
            storeRid = getRid(execute(tx, selectStore, selectStore, new Object[]{storeId}));
            if (StringUtils.isNotBlank(storeRid)) {
                CacheUtils.setStoreRid(storeId, storeRid);
            }
        }
        return storeRid;
    }


    public String getMemberRid(ODatabaseDocumentTx tx, String memberId) {
        if (StringUtils.isBlank(memberId)) {
            return null;
        }
        String memberRid = CacheUtils.getMemberRid(memberId);
        if (StringUtils.isBlank(memberRid)) {
            memberRid = getRid(execute(tx, selectMember, selectMember, new Object[]{memberId}));
            CacheUtils.setMemberRid(memberId, memberRid);
        }
        return memberRid;
    }

    protected String getRid(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof OResultSet) {
            OResultSet ors = (OResultSet) obj;
            if (ors != null && !ors.isEmpty()) {
                return ((ODocument) ors.get(0)).getIdentity().toString();
            }
        } else if (obj instanceof ODocument) {
            return ((ODocument) obj).getIdentity().toString();
        }
        return null;
    }

    public <RET> RET execute(ODatabaseDocumentTx tx, String templateSql, String sql, Object[] args) {
        return OrientSqlUtils.execute(tx, templateSql, sql, args);
    }

    public String getStartDatetime(String startDatetime, int i) {
        if (StringUtils.isBlank(startDatetime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(Calendar.getInstance().getTimeInMillis() - i * 3600 * 24 * 1000);
            return sdf.format(date) + " 00:00:00";
        }
        return startDatetime;
    }

    public String constructCallToSql(String fromPhoneRid,
                                     String toPhoneRid,
                                     Map<String, Object> dataMap) {
        String templateSql = MessageFormat.format(createCallTo2, fromPhoneRid, toPhoneRid);
        return templateSql.replace("#callCnt", getValue(dataMap.get("CALL_CNT"))).
                replace("#callLen", getValue(dataMap.get("CALL_LEN"))).
                replace("#callInCnt", getValue(dataMap.get("CALL_IN_CNT")))
                .replace("#callOutCnt", getValue(dataMap.get("CALL_OUT_CNT")))
                .replace("#reportTime", "'" + dataMap.get("CREATE_TIME").toString() + "'");
    }

    private String getValue(Object value) {
        return value == null ? "0" : value.toString();
    }

}
