package com.test.qr_generator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QrController {

    @GetMapping("/")
    public String turnstile() {
        return "turnstile";
    }

    @GetMapping("/home")
    public String index() {
        return "index";
    }

    @GetMapping("/reader")
    public String reader() {
        return "qr-reader";
    }

    @GetMapping("/reader-page")
    public String readerPage() {
        return "qr-reader";
    }

    @GetMapping("/turnstile-page")
    public String turnstilePage() {
        return "turnstile";
    }
}
