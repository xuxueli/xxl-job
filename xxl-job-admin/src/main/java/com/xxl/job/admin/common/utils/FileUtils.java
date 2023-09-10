package com.xxl.job.admin.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件操作工具类
 * @author Rong.Jia
 * @date 2023/09/10
 */
@Slf4j
public class FileUtils {

    /**
     * file转 MultipartFile
     *
     * @param file 文件
     * @return {@link MultipartFile}
     * @throws IOException IO异常
     */
    public static MultipartFile file2MultipartFile(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = FileUtil.getInputStream(file);
            return new CommonMultipartFile(file.getName(), inputStream);
        }catch (Exception e) {
            log.error("file2Multipart {}", e.getMessage());
            throw new IOException(e);
        }finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * MultipartFile 转换对象
     * @author Rong.Jia
     * @date 2022/03/23
     */
    public static class CommonMultipartFile implements MultipartFile {

        private final String name;
        private String originalFilename;
        private String contentType;
        private final byte[] content;

        /**
         * Create a new MultipartFileDto with the given content.
         * @param name the name of the file
         * @param content the content of the file
         */
        public CommonMultipartFile(String name, byte[] content) {
            this(name, name, null, content);
        }

        /**
         * Create a new MultipartFileDto with the given content.
         * @param name the name of the file
         * @param contentStream the content of the file as stream
         * @throws IOException if reading from the stream failed
         */
        public CommonMultipartFile(String name, InputStream contentStream) throws IOException {
            this(name, name, null, IoUtil.readBytes(contentStream));
        }

        /**
         * Create a new MultipartFileDto with the given content.
         * @param name the name of the file
         * @param originalFilename the original filename (as on the client's machine)
         * @param contentType the content type (if known)
         * @param content the content of the file
         */
        public CommonMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = (originalFilename != null ? originalFilename : "");
            this.contentType = contentType;
            this.content = (content != null ? content : new byte[0]);
        }

        /**
         * Create a new MultipartFileDto with the given content.
         * @param name the name of the file
         * @param originalFilename the original filename (as on the client's machine)
         * @param contentType the content type (if known)
         * @param contentStream the content of the file as stream
         * @throws IOException if reading from the stream failed
         */
        public CommonMultipartFile(String name, String originalFilename, String contentType, InputStream contentStream) throws IOException {
            this(name, originalFilename, contentType, IoUtil.readBytes(contentStream));
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return (this.content.length == 0);
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return this.content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            FileUtil.writeBytes(this.content, dest);
        }
    }

}
