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

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private Role role;

    private User(String email, String password) {
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.role = Role.ROLE_NORMAL;
    }

    public static User register(String email, String password) {
        User user = new User(email, password);
        return user;
    }

    public void updatePassword(String password) {
        password = Objects.requireNonNull(password);
        this.password = password;
    }

    public void updateProfile(String profile) {
        profile = Objects.requireNonNull(profile);
        this.profile = profile;
    }

    /**
     * 권한을 승격시키는 함수
     * @throws IllegalArgumentException 권한이 관리자일경우
     */
    public void grantAuthority(Role role) {
        if(this.role.getPriority() < role.getPriority()) {
            throw new IllegalArgumentException("부여할 권한이 옳바르지 않습니다.");
        }
        this.role = role;
    }

    public void lossAuthority(Role role) {
        if(this.role.getPriority() > role.getPriority()) {
            throw new IllegalArgumentException("부여할 권한이 옳바르지 않습니다.");
        }
        this.role = role;
    }

    public void approve() {
        if(this.role != Role.ROLE_GUEST) {
            throw new IllegalStateException("GUEST 승인 받을 수 있습니다.");
        }

        this.role = Role.ROLE_NORMAL;
    }
}
