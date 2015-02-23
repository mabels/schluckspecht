package com.adviser.schluckspecht;

import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public interface DriveTask {
    void process(LocalStore lc, DriveConnection dc, BlockingQueue<DriveTask> q);
}
