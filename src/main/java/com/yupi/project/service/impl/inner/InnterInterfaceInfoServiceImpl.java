package com.yupi.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.InterfaceInfoMapper;
import com.yupi.yuapicommon.model.entity.InterfaceInfo;
import com.yupi.yuapicommon.service.InnterInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnterInterfaceInfoServiceImpl implements InnterInterfaceInfoService {
    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url,method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaUpdateWrapper<InterfaceInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(InterfaceInfo::getUrl,url)
                .eq(InterfaceInfo::getMethod,method);
        return interfaceInfoMapper.selectOne(wrapper);
    }
}
