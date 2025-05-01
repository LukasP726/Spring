package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // Získá seznam všech rolí.
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Získá roli podle jejího ID.
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Uloží roli do databáze (vložení nebo aktualizace).
    public int saveRole(Role role) {
        return roleRepository.save(role);
    }

    // Odstraní roli podle jejího ID.
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    // Získá váhu role podle jejího ID.
    public int getWeightByRoleId(int idRole) {
        return roleRepository.getWeightByRoleId(idRole);
    }

}

