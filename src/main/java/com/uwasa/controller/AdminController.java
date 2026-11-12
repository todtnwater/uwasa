package com.uwasa.controller;

import com.uwasa.entity.BannedIps;
import com.uwasa.entity.Inquiries;
import com.uwasa.entity.Reactions;
import com.uwasa.service.BannedIpsService;
import com.uwasa.service.InquiriesService;
import com.uwasa.service.ReactionsService;
import com.uwasa.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BannedIpsService bannedIpsService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private InquiriesService inquiriesService;

    @Autowired
    private ReactionsService reactionsService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<BannedIps> bannedIps = bannedIpsService.findAllPaged(page);
        model.addAttribute("bannedIps", bannedIps);
        model.addAttribute("currentPage", page);
        return "admin/dashboard";
    }

    @PostMapping("/ban")
    public String ban(@RequestParam String ip,
                      @RequestParam String reason,
                      @RequestParam(required = false) String expiredAt) {
        LocalDateTime expiredAtTime = (expiredAt != null && !expiredAt.isEmpty())
                ? LocalDateTime.parse(expiredAt) : null;
        bannedIpsService.ban(ip, reason, expiredAtTime);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/unban")
    public String unban(@RequestParam Long banId) {
        bannedIpsService.unban(banId);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/inquiries")
    public String inquiries(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Inquiries> inquiries = inquiriesService.findAllPaged(page);
        model.addAttribute("inquiries", inquiries);
        model.addAttribute("currentPage", page);
        return "admin/inquiries";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long inquiryId, @RequestParam String answer) {
        inquiriesService.answer(inquiryId, answer);
        return "redirect:/admin/inquiries";
    }

    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Reactions> reports = reactionsService.findReportsPaged(page);
        model.addAttribute("reports", reports);
        model.addAttribute("currentPage", page);
        return "admin/reports";
    }
}