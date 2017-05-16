package cn.memedai.orientdb.teleporter.sns.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kisho on 2017/4/6.
 */
public final class CacheUtils {

    public static final Map<String, String> CACHE_STORE_RID = new ConcurrentHashMap<String, String>(4000);
    public static final Map<String, String> CACHE_APPLY_RID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDER_RID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_APPLYRID_STOREID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERRID_STOREID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERNO_APPLYINFORID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_DEVICE_RID = new ConcurrentHashMap(45000);
    public static final Map<String, String> CACHE_APPLYNO_DEVICERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_IP_RID = new ConcurrentHashMap(350000);
    public static final Map<String, String> CACHE_APPLYNO_IPRID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_ORDERNO_IPRID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_ORDERNO_DEVICERID = new ConcurrentHashMap(500000);
    public static final Map<String, String> CACHE_PHONE_MARK_RID = new ConcurrentHashMap();
    public static final Map<String, String> CACHE_PHONE_RID = new ConcurrentHashMap(1000000);
    public static final Map<String, String> CACHE_MEMBER_RID = new ConcurrentHashMap(1500000);
    public static final Map<String, String> CACHE_APPLYRID_MEMBERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERRID_MEMBERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_APPLYRID_PHONERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_ORDERRID_PHONERID = new ConcurrentHashMap(50000);
    public static final Map<String, String> CACHE_MEMBER_PHONERIDS = new ConcurrentHashMap(1500000);

    public static final Map<String, String> CACHE_REPORTERNO_PHONE = new ConcurrentHashMap(1500000);
    public static final Map<String, String> CACHE_APPLYNO_PHONE = new ConcurrentHashMap(15000);

    public static final Map<String, String> CACHE_PHONE_SOURCERID = new ConcurrentHashMap();

    public static final Map<String, Map<String, String>> ID_ADDRESS = new HashMap<>();

    public static void setStoreRid(String key, String value) {
        CACHE_STORE_RID.put(key, value);
    }

    public static String getStoreRid(String key) {
        return CACHE_STORE_RID.get(key);
    }

    public static void setApplyRid(String key, String value) {
        CACHE_APPLY_RID.put(key, value);
    }

    public static String getApplyRid(String key) {
        return CACHE_APPLY_RID.get(key);
    }

    public static void setOrderRid(String key, String value) {
        CACHE_ORDER_RID.put(key, value);
    }

    public static String getOrderRid(String key) {
        return CACHE_ORDER_RID.get(key);
    }

    public static void setDeviceRid(String key, String value) {
        CACHE_DEVICE_RID.put(key, value);
    }

    public static String getDeviceRid(String key) {
        return CACHE_DEVICE_RID.get(key);
    }

    public static void setIpRid(String key, String value) {
        CACHE_IP_RID.put(key, value);
    }

    public static String getIpRid(String key) {
        return CACHE_IP_RID.get(key);
    }

    public static void setApplyRidStoreId(String key, String value) {
        CACHE_APPLYRID_STOREID.put(key, value);
    }

    public static void setOrderRidStoreId(String key, String value) {
        CACHE_ORDERRID_STOREID.put(key, value);
    }

    public static void setOrderNoApplyRid(String key, String value) {
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

    public static void setApplyRidMemberId(String key, String value) {
        CACHE_APPLYRID_MEMBERID.put(key, value);
    }

    public static void setOrderRidMemberId(String key, String value) {
        CACHE_ORDERRID_MEMBERID.put(key, value);
    }

    public static void setApplyRidPhoneRid(String key, String value) {
        CACHE_APPLYRID_PHONERID.put(key, value);
    }

    public static String getApplyRidPhoneRid(String key) {
        return CACHE_APPLYRID_PHONERID.get(key);
    }

    public static void setOrderRidPhoneRid(String key, String value) {
        CACHE_ORDERRID_PHONERID.put(key, value);
    }

    public static void setMemberPhoneRids(String key, String value) {
        CACHE_MEMBER_PHONERIDS.put(key, value);
    }

    public static String getMemberPhoneRids(String key) {
        return CACHE_MEMBER_PHONERIDS.get(key);
    }


    public static void setReporternoPhone(String key, String value) {
        CACHE_REPORTERNO_PHONE.put(key, value);
    }

    public static void setApplyNoPhone(String key, String value) {
        CACHE_APPLYNO_PHONE.put(key, value);
    }

    public static void setPhoneSourceRid(String key, String value) {
        CACHE_PHONE_SOURCERID.put(key, value);
    }

    public static String getPhoneSourceRid(String key) {
        return CACHE_PHONE_SOURCERID.get(key);
    }

}
