package com.uwasa.controller;

import com.uwasa.entity.Inquiries;
import com.uwasa.entity.Users;
import com.uwasa.service.InquiriesService;
import com.uwasa.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inquiry")
public class InquiryController {

    @Autowired
    private InquiriesService inquiriesService;

    @Autowired
    private UsersService usersService;

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("CF-Connecting-IP");
        if (ip != null && !ip.isEmpty()) return ip;
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) return ip.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    @GetMapping("/write")
    public String writeForm() {
        return "inquiry/write";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title,
                        @RequestParam String content,
                        HttpServletRequest request,
                        HttpSession session) {

        String ip = getClientIp(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String nickname;
        Long userId = null;

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Users user = usersService.findByUsername(auth.getName());
            nickname = user != null ? user.getNickname() : "익명";
            userId = user != null ? user.getUserId() : null;
        } else {
            nickname = (String) session.getAttribute("guestNickname");
            if (nickname == null) nickname = "익명";
        }

        inquiriesService.write(title, content, ip, nickname, userId);
        return "redirect:/inquiry/list";
    }

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Users user = usersService.findByUsername(auth.getName());
            Page<Inquiries> inquiries = inquiriesService.findMyInquiriesPaged(user.getUserId(), page);
            model.addAttribute("inquiries", inquiries);
        } else {
            model.addAttribute("inquiries", null);
        }
        model.addAttribute("currentPage", page);
        return "inquiry/list";
    }

    @GetMapping("/detail/{inquiryId}")
    public String detail(@PathVariable Long inquiryId, Model model) {
        model.addAttribute("inquiry", inquiriesService.findById(inquiryId));
        return "inquiry/detail";
    }
}