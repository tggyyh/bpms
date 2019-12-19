package com.innodealing.bpms.appconfig.history.commap;

import com.innodealing.bpms.model.User;
import com.innodealing.bpms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserMap {
    @Autowired
    private UserService userService;

    public static Map map = new HashMap();
    private static UserMap userMap;

    @PostConstruct
    private void init(){
        userMap = this;
        userMap.userService = this.userService;
    }

    public static void refreshMap(){
        List<User> userList = userMap.userService.findUser();
        map = userList.stream().collect(Collectors.toMap(User::getId, User::getName));
    }
}
