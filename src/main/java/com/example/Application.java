package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/getCode")
    public String getCode(@RequestParam String country) {
        // Simplified country code lookup
        switch (country) {
            case "USA":
                return "US";
            case "Canada":
                return "CA";
            case "UK":
                return "GB";
            default:
                return "Unknown";
        }
    }
}
