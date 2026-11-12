package com.uwasa.controller;

import com.uwasa.entity.Posts;
import com.uwasa.entity.Users;
import com.uwasa.service.PostsService;
import com.uwasa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private PostsService postsService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
        return "user/login";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "user/join";
    }

    @PostMapping("/join")
    public String join(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam String nickname,
                       Model model) {
        boolean result = usersService.join(username, password, nickname);
        if (!result) {
            model.addAttribute("error", "이미 사용중인 아이디입니다.");
            return "user/join";
        }
        return "redirect:/user/login";
    }

    @GetMapping("/mypage")
    public String mypage(@RequestParam(defaultValue = "0") int page, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersService.findByUsername(auth.getName());
        Page<Posts> myPosts = postsService.getMyPostsPaged(user.getUserId(), page);
        model.addAttribute("user", user);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("currentPage", page);
        return "user/mypage";
    }

    @PostMapping("/revealReader")
    public String revealReader(@RequestParam Long postId,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersService.findByUsername(auth.getName());
        String readerNickname = postsService.revealReader(postId, user.getUserId());
        if (readerNickname == null) {
            model.addAttribute("error", "포인트가 부족하거나 확인할 수 없습니다.");
        } else {
            model.addAttribute("readerNickname", readerNickname);
        }
        Page<Posts> myPosts = postsService.getMyPostsPaged(user.getUserId(), page);
        model.addAttribute("user", user);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("currentPage", page);
        return "user/mypage";
    }
}