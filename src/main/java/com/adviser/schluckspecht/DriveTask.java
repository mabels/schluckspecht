package com.adviser.schluckspecht;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public interface DriveTask {
    void process() throws IOException;
    String getName();
}
