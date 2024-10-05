package com.whdcks3.data.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.common.CommonVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "announce")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
public class Announce extends CommonVO {
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Size(max = 5000)
    private String content;

}
