package com.example.rental.utils.fastdfs;
import com.example.rental.config.FastdfsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class FastDFSService {
    @Resource
    private FastDFSClientWrapper fastDFSClientWrapper ;
    @Resource
    private FastdfsConfig fastdfsConfig ;

    public  String uploadFile(MultipartFile file){
        try {
            byte[] bytes = file.getBytes();
            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            String fileName = file.getName();
            long fileSize = file.getSize();
            log.info("文件上传文件属性[originalFileName:{},fileName:{},fileSize:{},extension:{}, bytes.lengt:{}]",originalFileName,fileName,fileSize,extension,bytes.length);

            String url =  fastDFSClientWrapper.uploadFile(bytes, fileSize, extension);
            String resultUrl = fastdfsConfig.getOuturl() + url;

            log.info("文件地址：{}",resultUrl);
            return resultUrl;
        } catch (IOException e) {
            log.error("fastdfs上传文件失败，{}",e);
        }
        return null;
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件URL
     * @return 文件字节
     * @throws IOException
     */
    public  byte[] downloadFile(String fileUrl) throws IOException {
        byte[] bytes = fastDFSClientWrapper.downloadFile(fileUrl);
        return bytes;
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件URL
     * @return 文件字节
     * @throws IOException
     */
    public  void deleteFile(String fileUrl) throws IOException {
        fastDFSClientWrapper.deleteFile(fileUrl);
    }
}
