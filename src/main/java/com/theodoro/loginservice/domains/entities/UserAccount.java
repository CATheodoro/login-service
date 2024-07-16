package com.theodoro.loginservice.domains.entities;

import com.theodoro.loginservice.domains.enumerations.RoleEnum;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "LAST_UPDATE_DATE_PASSWORD", nullable = false)
    private ZonedDateTime lastUpdateDatePassword;

    @Column(name = "IS_ACCOUNT_NON_LOCKED", nullable = false)
    private boolean accountNonLocked;

    @Column(name = "IS_ENABLED", nullable = false)
    private boolean enabled;

    @Column(name = "IS_DEACTIVATED", nullable = false)
    private boolean deactivated;

    @ManyToMany(fetch = FetchType.EAGER)
    @Column(name = "ROLE", nullable = false)
    @JoinTable(
            name = "USER_ACCOUNT_ROLE",
            joinColumns = @JoinColumn(name = "ID_USER_ACCOUNT", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ID_ROLE", referencedColumnName = "ID")
    )
    private List<Role> roles;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private ZonedDateTime creationDate;

    public UserAccount() {
    }

    public UserAccount(List<Role> roles, String name, String email, String password, ZonedDateTime lastUpdateDatePassword) {
        this.roles = roles;
        this.name = name;
        this.email = email;
        this.password = password;
        this.lastUpdateDatePassword = lastUpdateDatePassword;
        this.accountNonLocked = true;
        this.enabled = false;
        this.deactivated = false;
    }

    @PrePersist
    private void prePersists() {
        this.creationDate = ZonedDateTime.now();
    }

    public boolean isAdmin(){
        return this.getRoles().stream().anyMatch(role -> role.getCode().equals(RoleEnum.ADMIN.name()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ZonedDateTime getLastUpdateDatePassword() {
        return lastUpdateDatePassword;
    }

    public void setLastUpdateDatePassword(ZonedDateTime lastUpdateDatePassword) {
        this.lastUpdateDatePassword = lastUpdateDatePassword;
    }

    public boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getCode()))
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
