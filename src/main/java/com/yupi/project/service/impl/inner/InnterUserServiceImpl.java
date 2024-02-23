package com.yupi.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserMapper;
import com.yupi.yuapicommon.model.entity.User;
import com.yupi.yuapicommon.service.InnterUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnterUserServiceImpl implements InnterUserService {
    @Resource
    private UserMapper userMapper;
    @Override
    public User getInvokeUser(String accessKey, String secretKey) {
       if (StringUtils.isAnyBlank(accessKey,secretKey)){
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
        LambdaUpdateWrapper<User> queryWrapper = new LambdaUpdateWrapper<>();
       queryWrapper.eq(User::getAccessKey,accessKey)
                       .eq(User::getSecretKey,secretKey);
        return userMapper.selectOne(queryWrapper);
    }
}
