package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.ChatRequest;
import it.unical.webdevelop.backafterglow.dto.ChatResponse;
import it.unical.webdevelop.backafterglow.services.Llama4_API;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/chat")

public class ChatController {
    private final Llama4_API llama;

    public ChatController(Llama4_API llama) {
        this.llama = llama;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest req) {
        ChatResponse res = new ChatResponse();
        try {
            res.setReply(llama.ask(req.getMessage()));
        } catch (Exception e) {
            res.setReply("Errore interno: " + e.getMessage());
        }
        return res;
    }
}