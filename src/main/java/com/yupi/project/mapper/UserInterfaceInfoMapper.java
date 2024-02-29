package com.yupi.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.yuapicommon.model.entity.UserInterfaceInfo;

import java.util.List;


/**
* @author 46325
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Mapper
* @createDate 2024-02-09 17:10:48
* @Entity com.yupi.project.model.entity.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {
    //select interfaceInfoId,sum(totalNum) as totalNum  from user_interface_info group by interfaceInfoId order by totalNum desc limit 3
    List<UserInterfaceInfo> listTopInvokeFaceInfo(int limit);
}




