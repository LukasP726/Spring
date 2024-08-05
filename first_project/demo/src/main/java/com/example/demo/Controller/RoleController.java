package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.Role;
import com.example.demo.Model.User;
import com.example.demo.Service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> getAllRoles() {
      return roleService.getAllRoles();
    }

    @PostMapping
    public ResponseEntity<Integer> createRole(@RequestBody Role role) {
        int response = roleService.saveRole(role);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id " + id));
    }

    // Přidejte další metody podle potřeby, např. pro aktualizaci role
}
