package com.adviser.schluckspecht;

import com.google.api.services.drive.model.File;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by menabe on 23.02.15.
 */
public class RetrieveRoot implements DriveTask {

    final SchluckSpecht schluckSpecht;

    public RetrieveRoot(SchluckSpecht schluckSpecht) {
        this.schluckSpecht = schluckSpecht;
    }

    @Override
    public String getName() {
        return "/";
    }

    @Override
    public void process()  {
        try {
            RetrieveFolder.mergeWithLocal(schluckSpecht, schluckSpecht.lc.retrieveRoot(), schluckSpecht.dc.retrieveRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
