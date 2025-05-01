package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Role;
import com.example.demo.service.RoleService;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class RoleController {

    @Autowired
    private RoleService roleService;


    /**
     * Endpoint pro získání všech rolí.
     * 
     * Tento endpoint vrací seznam všech rolí v systému. Vrací všechny záznamy z databáze.
     */
    @GetMapping
    public List<Role> getAllRoles() {
      return roleService.getAllRoles();
    }

    /**
     * Endpoint pro vytvoření nové role.
     * 
     * Tento endpoint přijímá JSON data s informacemi o nové roli a uloží je do databáze.
     * Pokud je role úspěšně vytvořena, vrátí HTTP status 201 CREATED s ID nové role.
     */
    @PostMapping
    public ResponseEntity<Integer> createRole(@RequestBody Role role) {
        int response = roleService.saveRole(role);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    
    /**
     * Endpoint pro smazání role podle ID.
     * 
     * Tento endpoint umožňuje smazání existující role na základě ID. Pokud role existuje, je odstraněna.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint pro získání role podle jejího ID.
     * 
     * Tento endpoint vrací roli podle jejího ID. Pokud role neexistuje, vyvolá se výjimka s popisem chyby.
     */
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id " + id));
    }

    /**
     * Endpoint pro získání váhy role podle jejího ID.
     * 
     * Tento endpoint vrací váhu role na základě jejího ID. Pokud je role validní, váha se vrátí ve formátu JSON.
     * 
     */
    @GetMapping("/weight/{idRole}")
    public ResponseEntity<Integer> getWeightByRoleId(@PathVariable int idRole) {
        int weight = roleService.getWeightByRoleId(idRole);
        return ResponseEntity.ok(weight);
    }

}
