package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2019/3/14/014.
 */
public class FTPUtil {

    private static Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;

    public FTPUtil(String ip, int port, String user, String pwd){
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    /**
     *对外开放的方法,上传文件
     * @param fileList  要上传的文件list
     * @return          返回上传结果成功||失败
     * @throws IOException
     */
    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp,21,ftpUser,ftpPass);
        logger.info("开始上传文件");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("开始连接ftp服务器，结束上传，上传结果{}",result);
        return result;
    }

    /**
     * 上传文件
     * @param remotePath   设置上传目录
     * @param fileList     要上传的文件list
     * @return             返回上传结果成功||失败
     * @throws IOException
     */
    public boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean isSuccess = false;
        FileInputStream fis = null;
        if(connecServer(this.ip,this.port,this.user,this.pwd)){
            try {
                ftpClient.changeWorkingDirectory(remotePath);       //设置工作目录
                ftpClient.setBufferSize(1024);                      //设置缓冲区大小
                ftpClient.setControlEncoding("UTF-8");              //设置字符编码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  //设置文件为字节类型，防止乱码
                ftpClient.enterLocalActiveMode();
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(),fis);
                }
                isSuccess = true;
            } catch (IOException e) {
                logger.error("上传文件异常",e);
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return isSuccess;
    }

    /**
     * 连接ftp服务器方法
     * @param ip    ftp服务器地址
     * @param port  ftp服务器端口
     * @param user  ftp服务器登陆用户名
     * @param pwd   ftp服务器登陆密码
     * @return      返回连接结果：成功||失败
     */
    private boolean connecServer(String ip,int port,String user,String pwd){
        ftpClient = new FTPClient();
        boolean isSuccess = false;
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,pwd);
            isSuccess = true;
        } catch (IOException e) {
            logger.error("上传文件异常",e);
        }
        return isSuccess;
    }
}
