package com.qbb.minio.controller;

import com.qbb.minio.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;


/**
 * @author QiuQiu&LL (个人博客:https://www.cnblogs.com/qbbit)
 * @version 1.0
 * @date 2022-05-20  16:23
 * @Description:
 */
@Api(tags = "minio文件管理")
@RestController
@RequestMapping("/minio")
public class FileController {

    @Autowired
    private MinioConfig minioConfig;

    @ApiOperation("测试程序")
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/remove")
    public boolean remove() {
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getAccessKey(), minioConfig.getSecretKey());
            // 从bucket中删除文件(文件名)。
            minioClient.removeObject(minioConfig.getBucket(), "ed8aa56a8ec640d6a36a271ed4222605_xiaomi.png");
            System.out.println("successfully removed mybucket/myobject");
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
    }

    @ApiOperation("文件上传")
    @PostMapping("/fileUpload")
    public String fileUpload(MultipartFile file) {
        /**
         * 这里应该放在service层处理的,将上传的文件路径存入数据库,我就不这么麻烦了,直接返回给前端
         */
        String bucket = minioConfig.getBucket();
        String endpoint = minioConfig.getEndpoint();
        String accessKey = minioConfig.getAccessKey();
        String secretKey = minioConfig.getSecretKey();

        // 1.获取文件名
        String originalFilename = file.getOriginalFilename();

        // 2.修改文件名,防止上传重复文件名,导致文件覆盖
        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + originalFilename;
        // 文件流
        try (InputStream inputStream = file.getInputStream()) {
            // 3.使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient(endpoint, accessKey, secretKey);

            // 4.检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(bucket);
            if (isExist) {
                System.out.println("Bucket already exists");
            } else {
                // 不存在则创建一个名为bucket的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(bucket);
            }
            // 6.上传参数设置项
            PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1);

            // 7.设置此次上传的文件的内容类型
            String contentType = file.getContentType();
            options.setContentType(contentType);

            // 8.上传
            minioClient.putObject(bucket, fileName, inputStream, options);

            // 9.访问路径
            String url = endpoint + "/" + bucket + "/" + fileName;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
