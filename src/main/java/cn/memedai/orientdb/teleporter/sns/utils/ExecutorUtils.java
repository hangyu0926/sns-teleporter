package cn.memedai.orientdb.teleporter.sns.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kisho on 2017/4/6.
 */
public final class ExecutorUtils {

    private static ExecutorService ES = Executors.newCachedThreadPool();

    public static ExecutorService getES() {
        return ES;
    }

}
