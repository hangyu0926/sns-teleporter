package cn.memedai.orientdb.teleporter.sns.utils;

/**
 * Created by kisho on 2017/4/12.
 */
public class ConstantUtils {

    public static final String SILVER_BOLT = "";

    public static boolean isSilverBolt(Object obj) {
        return obj == null ? false : "".equals(obj.toString());
    }

}
