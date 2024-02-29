package com.yupi.project.model.vo;


import com.yupi.yuapicommon.model.entity.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子视图
 *
 * @author huangjun
 * @TableName product
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoVO extends Post {

    /**
     * 调用次数
     */
    private Integer totalNum;

    private static final long serialVersionUID = 1L;
}