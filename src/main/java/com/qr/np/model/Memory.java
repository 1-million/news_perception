package com.qr.np.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memory {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;
    private String sessionId;
    private Integer sort;
    private String type;
    private String text;
    private Date createTime;
}
