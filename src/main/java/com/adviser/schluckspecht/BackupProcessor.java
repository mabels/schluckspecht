package com.adviser.schluckspecht;

import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class BackupProcessor {
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
                 while(true) {
                     try {
                         final DriveTask dt = q.take();
                         dt.process(lc, dc, q);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
            }
        });
        thread.start();
    }
}
