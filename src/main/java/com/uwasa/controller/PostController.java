package com.uwasa.controller;

import com.uwasa.entity.Posts;
import com.uwasa.entity.Users;
import com.uwasa.service.BannedIpsService;
import com.uwasa.service.PostsService;
import com.uwasa.service.ReactionsService;
import com.uwasa.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Random;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostsService postsService;

    @Autowired
    private BannedIpsService bannedIpsService;

    @Autowired
    private ReactionsService reactionsService;

    @Autowired
    private UsersService usersService;

    private static final String[] ADJ = {
        "무서운", "졸린", "배고픈", "수줍은", "용감한",
        "멍청한", "귀여운", "냄새나는", "빠른", "느린",
        "뚱뚱한", "날씬한", "시끄러운", "조용한", "웃긴",
        "슬픈", "행복한", "화난", "신난", "피곤한",
        "억울한", "당황한", "설레는", "지루한", "떨리는",
        "수상한", "어리바리한", "촉촉한", "바삭한", "느끼한",
        "간지러운", "두근거리는", "억센", "나른한", "통통한",
        "엉뚱한", "덜렁대는", "소심한", "뻔뻔한", "낯선"
    };

    private static final String[] NOUN = {
        "고양이", "두더지", "너구리", "펭귄", "하마",
        "기린", "악어", "다람쥐", "수달", "문어",
        "북극곰", "판다", "코알라", "미어캣", "카피바라",
        "땅돼지", "플라밍고", "바다사자", "해달", "라쿤",
        "스컹크", "아르마딜로", "개미핥기", "오리너구리", "알파카",
        "바다코끼리", "독수리", "부엉이", "앵무새", "홍학",
        "치타", "재규어", "망고몽구스", "비버", "고슴도치",
        "두루미", "물개", "바다표범", "낙타", "야크",
        "캥거루", "코뿔소", "하이에나", "자칼", "사막여우"
    };

    private String generateNickname() {
        Random random = new Random();
        if (random.nextInt(1800) == 0) {
            return "억울한 생수";
        }
        return ADJ[random.nextInt(ADJ.length)] + " " + NOUN[random.nextInt(NOUN.length)];
    }

    private String getOrCreateNickname(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Users user = usersService.findByUsername(auth.getName());
            return user != null ? user.getNickname() : generateNickname();
        }
        String nickname = (String) session.getAttribute("guestNickname");
        if (nickname == null) {
            nickname = generateNickname();
            session.setAttribute("guestNickname", nickname);
        }
        return nickname;
    }

    private Long getLoginUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Users user = usersService.findByUsername(auth.getName());
            return user != null ? user.getUserId() : null;
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("CF-Connecting-IP");
        if (ip != null && !ip.isEmpty()) return ip;
        ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) return ip.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    @GetMapping("/write")
    public String writeForm() {
        return "post/write";
    }

    @PostMapping("/write")
    public String write(@RequestParam String title,
                        @RequestParam String content,
                        HttpServletRequest request,
                        HttpSession session) {

        String ip = getClientIp(request);

        if (bannedIpsService.isBanned(ip)) {
            return "redirect:/?banned=true";
        }

        String writerNickname = getOrCreateNickname(session);
        Long userId = getLoginUserId();

        PostsService.WriteResult result = postsService.write(title, content, ip, writerNickname, userId);

        if (result == PostsService.WriteResult.BAD_WORD) {
            return "redirect:/post/write?badword=true";
        }
        if (result == PostsService.WriteResult.COOLDOWN) {
            return "redirect:/post/write?cooldown=true";
        }

        return "redirect:/";
    }

    @GetMapping("/read/{postId}")
    public String readGet(@PathVariable Long postId) {
        return "redirect:/";
    }

    @PostMapping("/read/{postId}")
    public String read(@PathVariable Long postId,
                       HttpServletRequest request,
                       HttpSession session,
                       Model model) {

        String ip = getClientIp(request);

        if (bannedIpsService.isBanned(ip)) {
            return "redirect:/?banned=true";
        }

        String readerNickname = getOrCreateNickname(session);
        Posts post = postsService.consume(postId, ip, readerNickname);

        if (post == null) {
            return "redirect:/?consumed=true";
        }

        model.addAttribute("post", post);
        return "post/read";
    }

    @PostMapping("/react")
    public String react(@RequestParam Long postId,
                        @RequestParam String emoji,
                        @RequestParam(required = false) String reportReason,
                        HttpServletRequest request) {

        String ip = getClientIp(request);
        reactionsService.react(postId, emoji, ip, reportReason);
        return "redirect:/";
    }
}