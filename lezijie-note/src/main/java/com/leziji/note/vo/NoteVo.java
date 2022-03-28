package com.leziji.note.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 接收分组名称
 */
@Getter
@Setter
public class NoteVo {
    private String groupName;//分组
    private long noteCount;//云记数量

    private  Integer typeId;//云记类型
}
