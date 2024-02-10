package com.yupi.project.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.project.model.entity.UserInterfaceInfo;


/**
* @author 46325
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2024-02-09 17:10:48
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
     void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo,boolean add);

     /**
      * 统计调用次数
      * @param interfaceInfoId
      * @param userId
      * @return
      */
     boolean invokeCount(long interfaceInfoId , long userId);
}
