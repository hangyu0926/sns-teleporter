package cn.memedai.orientdb.teleporter.sns.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kisho on 2017/4/6.
 */
public final class CacheUtils {

    public static final Map<String, String> CACHE_STOREINFO_RID = new ConcurrentHashMap<String, String>(4000);
    public static final Map<String, String> CACHE_APPLYINFO_RID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERINFO_RID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_APPLYINFORID_STOREID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERINFORID_STOREID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERNO_APPLYINFORID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_DEVICEINFO_RID = new ConcurrentHashMap(45000);
    public static final Map<String, String> CACHE_APPLYNO_DEVICERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_IP_RID = new ConcurrentHashMap(350000);
    public static final Map<String, String> CACHE_APPLYNO_IPRID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_ORDERNO_IPRID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_ORDERNO_DEVICERID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_PHONE_MARK_RID = new ConcurrentHashMap();
    public static final Map<String, String> CACHE_PHONE_RID = new ConcurrentHashMap(1000000);
    public static final Map<String, String> CACHE_MEMBER_RID = new ConcurrentHashMap(1500000);
    public static final Map<String, String> CACHE_APPLYINFORID_MEMBERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERINFORID_MEMBERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_APPLYINFORID_PHONERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERINFORID_PHONERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_MEMBER_PHONERIDS = new ConcurrentHashMap(1500000);

    public static final Map<String, String> CACHE_REPORTERNO_PHONE = new ConcurrentHashMap(1500000);

    public static final List<String> COMMAND_SQL = new Vector<String>(1000);

    public static void setStoreInfoRid(String key, String value) {
        CACHE_STOREINFO_RID.put(key, value);
    }

    public static String getStoreInfoRid(String key) {
        return CACHE_STOREINFO_RID.get(key);
    }

    public static void setApplyInfoRid(String key, String value) {
        CACHE_APPLYINFO_RID.put(key, value);
    }

    public static String getApplyInfoRid(String key) {
        return CACHE_APPLYINFO_RID.get(key);
    }

    public static void setOrderInfoRid(String key, String value) {
        CACHE_ORDERINFO_RID.put(key, value);
    }

    public static String getOrderInfoRid(String key) {
        return CACHE_ORDERINFO_RID.get(key);
    }

    public static void setDeviceRid(String key, String value) {
        CACHE_DEVICEINFO_RID.put(key, value);
    }

    public static String getDeviceRid(String key) {
        return CACHE_DEVICEINFO_RID.get(key);
    }

    public static void setIpRid(String key, String value) {
        CACHE_IP_RID.put(key, value);
    }

    public static String getIpRid(String key) {
        return CACHE_IP_RID.get(key);
    }

    public static void setApplyInfoRidStoreId(String key, String value) {
        CACHE_APPLYINFORID_STOREID.put(key, value);
    }

    public static void setOrderInfoRidStoreId(String key, String value) {
        CACHE_ORDERINFORID_STOREID.put(key, value);
    }

    public static void setOrderNoApplyInfoRid(String key, String value) {
        CACHE_ORDERNO_APPLYINFORID.put(key, value);
    }

    public static void setApplyNoDeviceRid(String key, String value) {
        CACHE_APPLYNO_DEVICERID.put(key, value);
    }

    public static void setApplyNoIpRid(String key, String value) {
        CACHE_APPLYNO_IPRID.put(key, value);
    }

    public static void setOrderNoIpRid(String key, String value) {
        CACHE_ORDERNO_IPRID.put(key, value);
    }

    public static void setOrderNoDeviceRid(String key, String value) {
        CACHE_ORDERNO_DEVICERID.put(key, value);
    }

    public static void setPhoneMarkRid(String key, String value) {
        CACHE_PHONE_MARK_RID.put(key, value);
    }

    public static String getPhoneMarkRid(String key) {
        return CACHE_PHONE_MARK_RID.get(key);
    }

    public static void setPhoneRid(String key, String value) {
        CACHE_PHONE_RID.put(key, value);
    }

    public static String getPhoneRid(String key) {
        return CACHE_PHONE_RID.get(key);
    }

    public static void setMemberRid(String key, String value) {
        CACHE_MEMBER_RID.put(key, value);
    }

    public static String getMemberRid(String key) {
        return CACHE_MEMBER_RID.get(key);
    }

    public static void setApplyInfoRidMemberId(String key, String value) {
        CACHE_APPLYINFORID_MEMBERID.put(key, value);
    }

    public static void setOrderInfoRidMemberId(String key, String value) {
        CACHE_ORDERINFORID_MEMBERID.put(key, value);
    }

    public static void setApplyInfoRidPhoneRid(String key, String value) {
        CACHE_APPLYINFORID_PHONERID.put(key, value);
    }

    public static String getApplyInfoRidPhoneRid(String key) {
        return CACHE_APPLYINFORID_PHONERID.get(key);
    }

    public static void setOrderInfoRidPhoneRid(String key, String value) {
        CACHE_ORDERINFORID_PHONERID.put(key, value);
    }

    public static void setMemberPhoneRids(String key, String value) {
        CACHE_MEMBER_PHONERIDS.put(key, value);
    }

    public static String getMemberPhoneRids(String key) {
        return CACHE_MEMBER_PHONERIDS.get(key);
    }


    public static void setCacheReporternoPhone(String key, String value) {
        CACHE_REPORTERNO_PHONE.put(key, value);
    }

}
