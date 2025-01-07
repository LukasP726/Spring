package com.example.demo.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:4200", "http://192.168.56.1:4200"})
public class CommandController {

    @GetMapping("/execute")
    public String executeCommand(String command) {
        
        StringBuilder output = new StringBuilder();

        try {
            ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-Command", command);
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

    private boolean isValidCommand(String command) {
        // Validace, např. povolení jen určitých příkazů
        return command != null && command.matches("^(backup|monitor|clean).*");
    }




        @GetMapping("/logs")
    public String getLogs(@RequestParam String logPath) {
        StringBuilder output = new StringBuilder();

        // Validace logovací cesty
        if (!isValidLogPath(logPath)) {
            return "Invalid log path";
        }

        try {
            // Připraví příkaz Get-Content s -Tail 10
            ProcessBuilder builder = new ProcessBuilder("powershell.exe", "-Command", "Get-Content", logPath, "-Tail", "10");
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

    private boolean isValidLogPath(String logPath) {
        // Povolené adresáře a soubory (lze přizpůsobit podle potřeby)
        Path path = Paths.get(logPath);
        try {
            // Zkontrolovat, zda soubor existuje a je čitelný
            return Files.exists(path) && Files.isReadable(path) && logPath.endsWith(".log");
        } catch (Exception e) {
            return false;
        }
    }
}
