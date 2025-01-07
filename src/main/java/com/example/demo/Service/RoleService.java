package com.example.demo.Service;

import org.springframework.stereotype.Service;

import com.example.demo.Model.Role;
import com.example.demo.Repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public int saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public int getWeightByRoleId(int idRole) {
       return roleRepository.getWeightByRoleId(idRole);
    }
}

