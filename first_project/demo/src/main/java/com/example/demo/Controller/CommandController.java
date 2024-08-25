package com.example.demo.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class CommandController {

    @GetMapping("/execute")
    @CrossOrigin(origins = {"http://localhost:4200", "http://192.168.56.1:4200"})
    public String executeCommand(@RequestParam String command) {
        StringBuilder output = new StringBuilder();

        // Povolené příkazy pro bezpečnost
        if (!isValidCommand(command)) {
            return "Invalid command.";
        }

        try {
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true); // Spojit chybový výstup s běžným výstupem
            Process process = builder.start();

            // Asynchronní čtení výstupu procesu
            Future<?> future = Executors.newSingleThreadExecutor().submit(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        synchronized (output) {
                            output.append(line).append("\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Čekání na dokončení procesu s timeoutem
            try {
                process.waitFor(30, TimeUnit.SECONDS); // 30 sekund timeout
                future.get(30, TimeUnit.SECONDS); // Čekání na dokončení čtení
            } catch (InterruptedException | TimeoutException e) {
                process.destroy(); // Zastavit proces, pokud překročí timeout
                return "Process timed out.";
            } catch (Exception e) {
                return "Error executing command: " + e.getMessage();
            }

        } catch (IOException e) {
            return "Error executing command: " + e.getMessage();
        }

        return output.toString();
    }

    // Funkce pro validaci povolených příkazů
    private boolean isValidCommand(String command) {
        // Implementuj validaci příkazů podle potřeby
        // Např. kontrola, zda příkaz je v seznamu povolených
        // nebo použití regex pro ověření syntaxe
        return command != null && !command.trim().isEmpty();
    }
}
