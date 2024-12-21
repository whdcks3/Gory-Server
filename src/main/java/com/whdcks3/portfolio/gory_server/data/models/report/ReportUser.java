package com.whdcks3.portfolio.gory_server.data.models.report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert

public class ReportUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_pid", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_pid", nullable = false)
    private User reporter;

    String category;

    @Size(max = 500)
    private String content;

    public ReportUser(String category, String content, User reporter, User reported) {
        this.category = category;
        this.content = content;
        this.reporter = reporter;
        this.user = reported;
    }
}
