package com.polymer.desox.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    private String roleNickName;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private List<User> users;



}
