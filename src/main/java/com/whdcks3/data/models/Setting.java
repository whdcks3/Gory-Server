package com.whdcks3.data.models;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;

import com.google.auto.value.AutoValue.Builder;
import com.whdcks3.common.CommonVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "setting")
@Getter
@Setter
@Builder
@AllArgsConstructor
@DynamicInsert
public class Setting extends CommonVO {
    String privacy, terms;
}
