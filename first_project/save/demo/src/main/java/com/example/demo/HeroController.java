package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")
@CrossOrigin(origins = "http://localhost:4200")
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

    @PutMapping("/{id}")
    public Hero updateHero(@PathVariable Long id, @RequestBody Hero heroDetails) {
        Hero hero = heroRepository.findById(id).orElseThrow();
        hero.setName(heroDetails.getName());
        heroRepository.save(hero);
        return hero;
    }

    @DeleteMapping("/{id}")
    public void deleteHero(@PathVariable Long id) {
        Hero hero = heroRepository.findById(id).orElseThrow();
        heroRepository.delete(hero);
    }
}
