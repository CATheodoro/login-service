package com.theodoro.loginservice.domains.services;

import com.theodoro.loginservice.domains.entities.Role;
import com.theodoro.loginservice.domains.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Optional<Role> findByCode(String code) {
        return roleRepository.findByCode(code);
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Optional<Role> findById(String id) {
        return roleRepository.findById(id);
    }
}
