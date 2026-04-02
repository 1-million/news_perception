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
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String sessionId;
    private String text;
    private Date createTime;
}
