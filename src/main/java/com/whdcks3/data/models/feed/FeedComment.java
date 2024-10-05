package com.whdcks3.data.models.feed;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.common.CommonVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feed_comment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@DynamicInsert
public class FeedComment extends CommonVO {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "feed_pid", nullable = false)
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    private String content;

    @Column(nullable = true)
    private long feedCommentPid;

}
