package com.yupi.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yupi.project.common.ErrorCode;
import com.yupi.project.exception.BusinessException;
import com.yupi.project.mapper.UserInterfaceInfoMapper;
import com.yupi.yuapicommon.model.entity.InterfaceInfo;
import com.yupi.yuapicommon.model.entity.UserInterfaceInfo;
import com.yupi.yuapicommon.service.InnterUserInterfaceInfoService;
import com.yupi.yuapicommon.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnterUserInterfaceInfoServiceImpl implements InnterUserInterfaceInfoService {
    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;
    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
      return   userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

/*
    @Override
    public long isLeftNum(long userInterfaceInfID) {

        if (userInterfaceInfID <1 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //根据userInterfaceInfID 查剩余调用次数
        return 0;
    }
*/

    @Override
    public long isLeftNum(long interfaceInfoId, long userId) {

            if (interfaceInfoId<1 || userId <1 ){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            LambdaQueryWrapper<UserInterfaceInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                    .eq(UserInterfaceInfo::getUserId,userId)
                    .select(UserInterfaceInfo::getLeftNum);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoMapper.selectOne(queryWrapper);
        Integer leftNums  = userInterfaceInfo.getLeftNum();
        Long leftNum =  leftNums.longValue();
        return leftNum;

    }

}
