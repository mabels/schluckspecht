package com.adviser.schluckspecht;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class BackupProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(BackupProcessor.class);

    private Thread thread;

    public void join() {
        try {
            thread.join();
        } catch (Throwable th) {
            // ignore
        }
    }

    public void start(final LocalStore lc, final DriveConnection dc, final BlockingQueue<DriveTask> q) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DriveTask dt = null;
                while(true) {
                     try {
                         dt = q.take();
                         dt.process();
                     } catch (Exception e) {
                         LOG.error("DriveTask failed:"+(dt == null ? "NULL":dt.getName())+":", e);
                     }
                 }
            }
        });
        thread.start();
    }
}
