package com.flextalk.we.user.domain.entity;

import com.flextalk.we.cmmn.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ft_user")
@EqualsAndHashCode(of = {"id", "email"})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_email", length = 200, nullable = false, unique = true)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    @Column(name = "user_profile")
    private String profile;

    private User(String email, String password) {
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
    }

    public static User register(String email, String password) {
        User user = new User(email, password);
        return user;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }
}
