package b203.varium.user.controller;

import b203.varium.user.dto.JoinDTO;
import b203.varium.user.dto.MyPageRespDTO;
import b203.varium.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 마이페이지 조회
    @GetMapping("/mypage")
    public ResponseEntity<MyPageRespDTO> viewMyPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // 구독 목록, 프로필 사진, 닉네임, 포인트

        return ResponseEntity.ok(userService.viewUserDetail(username));
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> joinUser(JoinDTO joinDTO) {

        System.out.println(joinDTO.getUsername());
        Map<String, String> result = userService.joinUser(joinDTO);

        return ResponseEntity.ok(result);
    }

    // 탈퇴
}