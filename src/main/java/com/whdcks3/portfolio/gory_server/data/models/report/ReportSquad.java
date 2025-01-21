package com.whdcks3.portfolio.gory_server.data.models.report;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.whdcks3.portfolio.gory_server.common.BaseEntity;
import com.whdcks3.portfolio.gory_server.data.models.squad.Squad;
import com.whdcks3.portfolio.gory_server.data.models.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_squad")
@Getter
@Setter
@NoArgsConstructor
public class ReportSquad extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "squad_pid", nullable = false)
    private Squad suqad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_pid", nullable = false)
    private User reproter;

    String category;

    @Size(max = 500)
    private String cotent;

    public ReportSquad(Squad squad, User reporter, String category, String cotent) {
        this.suqad = squad;
        this.reproter = reporter;
        this.category = category;
        this.cotent = cotent;
    }
}
