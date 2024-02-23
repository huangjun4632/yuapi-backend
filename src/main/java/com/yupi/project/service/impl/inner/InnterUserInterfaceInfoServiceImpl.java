package com.yupi.project.service.impl.inner;

import com.yupi.yuapicommon.model.entity.UserInterfaceInfo;
import com.yupi.yuapicommon.service.InnterUserInterfaceInfoService;
import com.yupi.yuapicommon.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnterUserInterfaceInfoServiceImpl implements InnterUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
      return   userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

}
