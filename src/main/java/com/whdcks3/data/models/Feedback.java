package com.whdcks3.data.models;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import com.whdcks3.common.CommonVO;

public class Feedback extends CommonVO {

    private String namenickname;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

}
