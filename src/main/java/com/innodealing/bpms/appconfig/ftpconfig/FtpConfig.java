package com.innodealing.bpms.appconfig.ftpconfig;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import sun.net.ftp.FtpClient;

@Component
public class FtpConfig {
    @Value(value = "${ftp.host.ip}")
    private String  ip;
    @Value(value = "${ftp.host.port}")
    private int  port;
    @Value(value = "${ftp.login.username}")
    private String  username;
    @Value(value = "${ftp.login.password}")
    private String  password;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

