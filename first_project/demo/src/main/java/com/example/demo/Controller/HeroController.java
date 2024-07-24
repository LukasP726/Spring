package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Model.Hero;
import com.example.demo.Repository.HeroRepository;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")//heroes
@CrossOrigin(origins = {"http://localhost:4200","http://192.168.56.1:4200"})
public class HeroController {

    @Autowired
    private HeroRepository heroRepository;

    @GetMapping
    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    @PostMapping
    public void createHero(@RequestBody Hero hero) {
        heroRepository.save(hero);
    }

    @PutMapping("/detail/{id}")
    public Hero updateHero(@PathVariable Long id, @RequestBody Hero heroDetails) {
        Hero hero = heroRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hero not found with id " + id));
        hero.setName(heroDetails.getName());
        heroRepository.save(hero);
        return hero;
    }

    @DeleteMapping("/{id}")
    public void deleteHero(@PathVariable Long id) {
        Hero hero = heroRepository.findById(id).orElseThrow();
        heroRepository.delete(hero);
    }

    @GetMapping("/{id}")
    public Hero getHeroById(@PathVariable Long id) {
        return heroRepository.findById(id).orElseThrow();
    }

    @GetMapping("/")
    public List<Hero> searchHeroes(@RequestParam(name = "name") String term) {
        return heroRepository.findByNameContaining(term);
    }


}
