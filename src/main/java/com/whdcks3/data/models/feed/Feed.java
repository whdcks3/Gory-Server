package com.whdcks3.data.models.feed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.whdcks3.common.CommonVO;
import com.whdcks3.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "feed")
@Getter
@Setter
@NoArgsConstructor
@DynamicInsert
public class Feed extends CommonVO {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_pid", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

    @OneToMany(mappedBy = "feed", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<FeedImage> images;

    @JsonManagedReference
    @OneToMany(mappedBy = "feed", targetEntity = FeedComment.class, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FeedComment> commets = new ArrayList<>();

    @Column(nullable = true, columnDefinition = "INT DEFAULT 0")
    private int likeCount, viewCount, commetCount, reportCount;
}
