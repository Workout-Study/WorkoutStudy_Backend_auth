package com.fitmate.oauth.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicInsert
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long userId;
    String oauthId;
    String oauthType;
    String nickName;
    Date createDate;
    Date updateDate;
}
