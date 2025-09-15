package com.guo.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author guo
 * @Date 2023 04 05 12 28
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTagDto {

    private Long id;

    //标签名
    private String name;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
    //备注
    private String remark;
}
