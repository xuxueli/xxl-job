package com.xxl.job.core.util;

import java.io.File;

/**
 * file tool
 *
 * @author xuxueli 2017-12-29 17:56:48
 */
public class FileUtil {

    public static boolean deleteRecursively(File root) {
        if (root != null && root.exists()) {
            if (root.isDirectory()) {
                File[] children = root.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursively(child);
                    }
                }
            }
            return root.delete();
        }
        return false;
    }

}
