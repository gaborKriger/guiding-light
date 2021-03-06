package com.kriger.guidinglight.model;

import com.kriger.guidinglight.model.forum.Question;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String activation;

    private Boolean enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JoinColumn(name="id")
    @EqualsAndHashCode.Exclude
    private UserPersonalData userPersonalData;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="role_id")}
    )
    @EqualsAndHashCode.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Question> questions = new HashSet<>();

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private Set<Question> answers = new HashSet<>();



    public void addRoles(String roleName) {
        if (this.roles == null || this.roles.isEmpty()) {
            this.roles = new HashSet<>();
        }
        this.roles.add(new Role(roleName));
    }
}







