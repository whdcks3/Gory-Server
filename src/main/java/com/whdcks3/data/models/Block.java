package com.whdcks3.data.models;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.whdcks3.common.CommonVO;
import com.whdcks3.data.models.user.User;

public class Block extends CommonVO {
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "user_pid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "block_pid", nullable = false)
    private User other;

}
