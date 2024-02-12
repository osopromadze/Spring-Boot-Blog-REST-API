package com.sopromadze.blogapi.model.role;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

//@Entity
@Data
//@NoArgsConstructor
//@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name = "name")
    private RoleName name;

    public Role(RoleName name) {
        this.name = name;
    }
}
