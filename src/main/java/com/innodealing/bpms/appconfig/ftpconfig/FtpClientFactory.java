package com.innodealing.bpms.appconfig.ftpconfig;

import com.innodealing.bpms.common.model.ConstantUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FtpClientFactory {
    @Autowired
    private FtpConfig ftpConfig;

    public FTPClient getClient() throws Exception {
        FTPClient client = new FTPClient();
        client.connect(ftpConfig.getIp(), ftpConfig.getPort());
        client.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        client.setFileType(FTP.BINARY_FILE_TYPE);
        int reply = client.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            client.disconnect();
            throw new Exception("ftp服务器连接不上了");
        }
        client.changeWorkingDirectory(ConstantUtil.FILE_URL);
        client.setBufferSize(1024);
        return client;
    }
}
