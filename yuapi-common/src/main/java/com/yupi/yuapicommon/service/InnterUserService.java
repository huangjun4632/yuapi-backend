package com.yupi.yuapicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuapicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author huangjun
 */
public interface InnterUserService  {
    /**
     * 数据库查用户ak是否一致
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey );

}
