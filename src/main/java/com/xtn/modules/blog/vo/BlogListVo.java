package com.xtn.modules.blog.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author xCoder
 */
@Data
public class BlogListVo {

    /**
     * id
     */
    @ApiModelProperty("id")
    private Long id;

    /**
     * 标题
     */
    @ApiModelProperty("标题")
    private String title;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 创建人id
     */
    @ApiModelProperty("创建人id")
    private Long createId;

    /**
     * 创建人名称
     */
    @ApiModelProperty("创建人名称")
    private String createName;

    /**
     * 博客标签（逗号分隔）
     */
    @ApiModelProperty("博客标签（逗号分隔）")
    private String labelName;

}
