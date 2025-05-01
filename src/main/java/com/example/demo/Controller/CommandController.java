package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://192.168.56.1:4200"})
public class CommandController {

    /**
     * REST endpoint, který vrací posledních 20 řádků ze zvoleného log souboru.
     * Cesta k souboru je zadána přes query parametr 'logPath'.
     */
    @GetMapping("/logs")
    public String getLogs(@RequestParam String logPath) {
        StringBuilder output = new StringBuilder();

        try {
            // Zranitelnost vznikne, pokud nezajistíš správné zpracování vstupu
            ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-Command", "Get-Content", "logs\\"+logPath, "-Tail", "20");
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();

        } catch (IOException | InterruptedException e) {
            return "Error executing command: " + e.getMessage();
        }

        return output.toString();
    }

}
