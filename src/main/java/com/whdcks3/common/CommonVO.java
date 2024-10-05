package com.whdcks3.common;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class CommonVO implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @CreationTimestamp
    @Column(nullable = false, name = "reg_dt", updatable = false)
    @ColumnDefault("current_timestamp")
    private LocalDateTime regDt;

    @UpdateTimestamp
    @Column(length = 20)
    private LocalDateTime updatedAt;

    @Setter
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean isEnable = true;
}
