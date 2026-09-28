package br.costa.DevJobs.infrastructure.Entity;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UsersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "confirm_password")
    private String confirmPassword;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "updated_at")
    private Instant updatedAt;

    private Integer attempts;

    public UsersEntity(
            String fullName,
            String email,
            String password,
            String confirmPassword,
            UserRoleEnum role,
            Instant createdAt,
            Instant updatedAt,
            Integer attempts
    ){
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.attempts = attempts;
    }


}
