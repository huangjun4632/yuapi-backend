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
     * 数据库查是否已分配给用户ak，sk
     * @param accessKey
     * @param secretKey
     * @return
     */
    User getInvokeUser(String accessKey , String secretKey);

}
