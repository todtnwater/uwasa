package com.uwasa.controller;

import com.uwasa.service.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private PostsService postsService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postsService.getAlivePosts());
        model.addAttribute("countConsumedToday", postsService.countConsumedToday());
        model.addAttribute("countAlive", postsService.countAlive());
        model.addAttribute("countTotalConsumed", postsService.countTotalConsumed());
        return "index";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "privacy";
    }
} 