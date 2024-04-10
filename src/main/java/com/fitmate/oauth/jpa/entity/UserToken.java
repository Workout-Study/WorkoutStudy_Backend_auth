package com.fitmate.oauth.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long userTokenId;
    long userId;
    String accessToken;
    String refreshToken;
}
