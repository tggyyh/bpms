package com.innodealing.bpms.unit;

import java.util.UUID;

/**
 * Created by Administrator on 2017/3/28.
 */
public class Generate {
    public static String generateGUID() {
        UUID tmpuuid = UUID.randomUUID();
        String tmpstr = tmpuuid.toString();
        tmpstr = tmpstr.replaceAll("-", "");
        return tmpstr;
    }
}
