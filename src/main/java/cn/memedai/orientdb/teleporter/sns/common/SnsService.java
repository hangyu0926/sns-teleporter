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

import cn.memedai.orientdb.teleporter.Caches;
import cn.memedai.orientdb.teleporter.sns.utils.CacheUtils;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OResultSet;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kisho on 2017/4/27.
 */
@Service
public class SnsService {

    private static final Logger LOG = LoggerFactory.getLogger(SnsService.class);

    private static final String SQL_DEVICE = "update Device set deviceId=? upsert return after where deviceId=?";

    private static final String SQL_IP = "update Ip set ip=?,ipCity=? upsert return after where ip=?";

    private static final String UPDATE_PHONE_SQL = "update Phone set phone=? upsert return after where phone=?";

    private static final String SELECT_PHONE_SQL = "select from Phone where phone=?";

    private static final String SELECT_STORE_SQL = "select from Store where storeId=?";

    private static final String SELECT_MEMBER_SQL = "select from Member where memberId = ?";

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
                ipRid = getRid(execute(tx, SQL_IP, ip, ipCity, ip));
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
                deviceRid = getRid(execute(tx, SQL_DEVICE, deviceId, deviceId));
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
//            phoneRid = getRid(execute(tx, SELECT_PHONE_SQL, phone, phone));
//            if (StringUtils.isBlank(phone)) {
            phoneRid = getRid(execute(tx, UPDATE_PHONE_SQL, phone, phone));
//            }
            if (StringUtils.isNotBlank(phoneRid)) {
                CacheUtils.setPhoneRid(phone, phoneRid);
            }
        }
        return phoneRid;
    }

    public String getStoreRid(ODatabaseDocumentTx tx, String storeId) {
        String storeRid = CacheUtils.getStoreRid(storeId);
        if (StringUtils.isBlank(storeRid)) {
            storeRid = getRid(execute(tx, SELECT_STORE_SQL, storeId));
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
            memberRid = getRid(execute(tx, SELECT_MEMBER_SQL, memberId));
            CacheUtils.setMemberRid(memberId, memberRid);
        }
        return memberRid;
    }

//    public ODocument getFirstODocumnet(Object obj) {
//        if (obj instanceof OResultSet) {
//            OResultSet ors = (OResultSet) obj;
//            if (ors != null && !ors.isEmpty()) {
//                return (ODocument) ors.get(0);
//            }
//        } else if (obj instanceof ODocument) {
//            return (ODocument) obj;
//        }
//        return null;
//    }

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

    public <RET> RET execute(ODatabaseDocumentTx tx, String sql, Object... args) {
        int i = 0;
        while (i++ < 10) {
            try {
                return tx.command(new OCommandSQL(sql)).execute(args);
            } catch (OConcurrentModificationException e) {
                continue;
            } catch (Exception e) {
                LOG.error("{} @ {}", sql, e.getMessage());
                LOG.error("", e);
                if (args != null && args.length > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (Object arg : args) {
                        builder.append(arg).append(",");
                    }
                    sql += "|" + builder.toString().replaceAll(",$", "");
                }
                Caches.ERROR_SQL.add(sql);
                break;
            }
        }
        return null;
    }

    public String getStartDatetime(String startDatetime, int i) {
        if (StringUtils.isBlank(startDatetime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(Calendar.getInstance().getTimeInMillis() - i * 3600 * 24 * 1000);
            return sdf.format(date) + " 00:00:00";
        }
        return startDatetime;
    }

}
