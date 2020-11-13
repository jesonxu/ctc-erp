package com.dahantc.erp.vo.user.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.ICacheUserService;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("cacheUserService")
public class CacheUserServiceImpl implements ICacheUserService {
	private static final Logger logger = LogManager.getLogger(CacheUserServiceImpl.class);

	private static Map<String, User> CACHE_USER = new ConcurrentHashMap<String, User>();

	private volatile static long LAST_CACHE_USER_TIME = System.currentTimeMillis();

	@Resource
	private IUserService userService;

	@Override
	public User getUser(String userId) {
		User user = null;
		if (System.currentTimeMillis() - LAST_CACHE_USER_TIME > 120000) {
			CACHE_USER.clear();
			LAST_CACHE_USER_TIME = System.currentTimeMillis();
		}
		try {
			user = CACHE_USER.get(userId);
			if (user == null) {
				user = userService.read(userId);
				if (user != null) {
					CACHE_USER.put(userId, user);
				}
			}
		} catch (Exception e) {
			logger.error("获取ChannelDetail异常", e);
		}
		return user;
	}

}
