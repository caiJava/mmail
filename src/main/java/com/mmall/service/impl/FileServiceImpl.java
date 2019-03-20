package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * Created by Administrator on 2019/3/14/014.
 */
@Service("iFileServiceImpl")
public class FileServiceImpl implements IFileService{

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**
     * 上传文件到ftp服务器，并且将本地文件删除
     * @param file      表单的MultipartFile对象
     * @param path      上传到本地的目录
     * @return          返回上传到ftp服务器的文件名
     */
    @Override
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}",fileName,path,uploadFileName);
        File filePath = new File(path);                         //如果目录不存在，则创建
        if(!filePath.exists()){
            filePath.setWritable(true);
            filePath.mkdirs();
        }

        File uploadFile = new File(path, uploadFileName);

        try{
            file.transferTo(uploadFile);                        //上传到本地
            boolean result = FTPUtil.uploadFile(Lists.newArrayList(uploadFile)); //上传到ftp
            uploadFile.delete();                                //删除本地文件
            if(result){
                return uploadFileName;
            }
        }catch(IOException e){
            logger.error("上传文件异常",e);
        }
        return null;
    }
}
