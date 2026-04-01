package com.mycompany.iaeval.web.rest;

import com.mycompany.iaeval.service.AiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping
    public String ask(@RequestParam String message) {
        return aiService.askIA(message);
    }
}
