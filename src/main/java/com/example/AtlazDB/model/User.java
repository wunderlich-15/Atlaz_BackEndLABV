package com.example.AtlazDB.model;

import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "matricula")
    private String registration;

    @Column(name = "email")
    private String email;

    @Column(name = "senha_hash")
    private String passwordHash;

    @Column(name = "perfil")
    @Enumerated(EnumType.STRING)
    private Profile profile;

    @Column(name = "usuario_status")
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @ElementCollection(targetClass = CnhType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "usuario_cnh",
            joinColumns = @JoinColumn(name = "id_usuario")
    )
    @Column(name = "tipo_cnh")
    private Set<CnhType> cnhTypes;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

}