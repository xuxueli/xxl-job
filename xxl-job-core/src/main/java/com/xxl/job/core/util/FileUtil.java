package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * file tool
 *
 * @author xuxueli 2017-12-29 17:56:48
 */
public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * delete recursively
     *
     * @param root
     * @return
     */
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


    public static void deleteFile(String fileName) {
        // file
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    public static void writeFileContent(File file, byte[] data) {
        // file
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        // append file content
        try {
            Files.write(file.toPath(), data, StandardOpenOption.APPEND);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static byte[] readFileContent(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new byte[0];
        }
    }

}
