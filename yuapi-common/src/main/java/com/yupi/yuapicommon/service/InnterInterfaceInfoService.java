package com.yupi.yuapicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yuapicommon.model.entity.InterfaceInfo;


/**
* @author 46325
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-12-23 21:33:05
*/
public interface InnterInterfaceInfoService  {

    /**
     * 查数据库查询模拟接口是否存在（请求路径、方法、参数）
     * @param path
     * @param method
     * @return
     */
    InterfaceInfo getInterfaceInfo(String path,String method);


}
