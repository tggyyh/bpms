package com.innodealing.bpms.unit;


import com.innodealing.bpms.appconfig.ftpconfig.FtpConfig;
import com.innodealing.bpms.common.model.ConstantUtil;
import com.innodealing.bpms.controller.FileController;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;
import java.util.concurrent.*;

public class FtpUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    public static void close(FTPClient client) {
        if (client != null && client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void upload(String fileName,InputStream input, FTPClient client) throws Exception {
        client.enterLocalPassiveMode();
        boolean f = client.storeFile(fileName, input);
        logger.info("上传标志："+f);
        input.close();
    }

    public static void download(List<File> fList,FTPClient client,String localPath) throws IOException {
        OutputStream outputStream = null;
        for(File f: fList){
            File localFile= new File(localPath+ f.getName());
            if(localFile.exists()){
                return;
            }else{
                outputStream = new FileOutputStream(localPath+ f.getName());
                client.retrieveFile(f.getName(), outputStream);
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    public static void main(String[] args) {
        FTPClient ftp = new FTPClient();
        long begin = System.currentTimeMillis();
        try {
            ftp.connect("ftp.qa.innodealing.com",21);
            ftp.login("admin","12345678");
            ftp.changeWorkingDirectory("/bpms/attachment");
            File f = new File("C:\\Users\\Administrator\\Desktop\\ab.sql");
//            File f2=new File(f.getPath());
            int i=20000;
            for(;i<120000;i++) {
                String newName = i + ".sql";
                FileInputStream input = new FileInputStream(f);
                ftp.storeFile(newName, input);
                input.close();
                System.out.println(newName);
            }
            ftp.logout();
            ftp.disconnect();
            System.out.println(System.currentTimeMillis()-begin);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
