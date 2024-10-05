package com.whdcks3.data.models.region;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "region")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@IdClass(RegionId.class)
public class Region {

    @Id
    @Column
    private String mainRegion;

    @Id
    @Column
    private String subRegion;

}
