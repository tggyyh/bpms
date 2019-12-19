package com.innodealing.bpms.appconfig.shiroconfig;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhou on 2017/3/8.
 */
public class CustomShiroSessionDAO extends AbstractSessionDAO {

    private static Logger logger = LoggerFactory.getLogger(CustomShiroSessionDAO.class);
    private final static String SESSION_PREFIX = "bpms_shiro_session:";
    @Autowired
    @Qualifier("redisTemplate1")
    private RedisTemplate<String, Object> redisTemplate;
    private int defaultExpireTime = 86400;

    public CustomShiroSessionDAO(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CustomShiroSessionDAO(int defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        String shiroSessionId = SESSION_PREFIX + sessionId.toString();
        redisTemplate.opsForValue().set(shiroSessionId, session);
        redisTemplate.expire(sessionId.toString(), this.defaultExpireTime, TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
//        logger.info("doReadSession session");
        String shiroSessionId = SESSION_PREFIX + sessionId.toString();
        Object session = redisTemplate.opsForValue().get(shiroSessionId);
        return (Session) session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
//        logger.info("update session");
        Serializable sessionId = session.getId();
        String shiroSessionId = SESSION_PREFIX + sessionId.toString();

        redisTemplate.opsForValue().set(shiroSessionId, session);
        redisTemplate.expire(sessionId.toString(), this.defaultExpireTime, TimeUnit.SECONDS);
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            return;
        }
        Serializable sessionId = session.getId();
        String shiroSessionId = SESSION_PREFIX + sessionId.toString();
        if (shiroSessionId != null) {
            redisTemplate.delete(shiroSessionId.toString());
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        return null;
    }


}