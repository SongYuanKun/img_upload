package com.cdvcloud.servlet;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;


/**
 * @author Administrator
 */
public class UploadImgServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(URLDecoder.class);

    /**
     * 上传文件
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        logger.info("接收到文件流");
        InputStream in;
        String fileName = "";
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter;
            iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                in = item.openStream();
                if (item.isFormField()) {
                    String value = Streams.asString(in, "UTF-8");
                    if ("fileName".equals(name)) {
                        logger.info("获取文件名:" + value);
                        fileName = value;
                    }
                    if ("imgBase64".equals(name)) {
                        logger.info("获取base64格式流");
                        BASE64Decoder decoder = new BASE64Decoder();
                        // Base64解码
                        byte[] imageByte = null;
                        try {
                            value = URLDecoder.decode(value, "utf-8");
                            imageByte = decoder.decodeBuffer(value);
                            for (int i = 0; i < imageByte.length; ++i) {
                                // 调整异常数据
                                if (imageByte[i] < 0) {
                                    imageByte[i] += 256;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String os = System.getProperty("os.name");
                        String path = File.separator + "www" + File.separator + System.currentTimeMillis() + "_" + fileName;
                        if (os.toLowerCase().startsWith("win")) {
                            path = "d:" + File.separator + "www" + File.separator + System.currentTimeMillis() + "_" + fileName;
                        }
                        File imageFile = new File(path);
                        FileUtils.copyToFile(new ByteArrayInputStream(imageByte), imageFile);
                        logger.info("截图文件保存到：" + path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
