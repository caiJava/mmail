package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Administrator on 2019/3/14/014.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
