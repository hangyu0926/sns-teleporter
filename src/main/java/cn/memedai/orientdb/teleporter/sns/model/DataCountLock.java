package cn.memedai.orientdb.teleporter.sns.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Collections.sort;

/**
 * Created by kisho on 2017/4/8.
 */
public class DataCountLock {

    private Lock lock = new ReentrantLock();

    private int count = 0;

    public int getAndIncrement() {
        lock.lock();
        try {
            return count++;
        } finally {
            lock.unlock();
        }
    }

    public int getAndDecrement() {
        lock.lock();
        try {
            return count--;
        } finally {
            lock.unlock();
        }
    }

}
