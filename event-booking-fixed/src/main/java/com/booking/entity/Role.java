package com.booking.entity;

import com.booking.enums.RoleType;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleType name;

    public Long getId() {
        return id;
    }

    public RoleType getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(RoleType name) {
        this.name = name;
    }
}