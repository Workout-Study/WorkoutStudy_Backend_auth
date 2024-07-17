package com.fitmate.oauth.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    long userTokenId;

    @OneToOne(mappedBy = "userToken", cascade = CascadeType.PERSIST)
    private Users users;
    String authAccessToken;
    String accessToken;
    String refreshToken;

    public UserToken() {}
}
