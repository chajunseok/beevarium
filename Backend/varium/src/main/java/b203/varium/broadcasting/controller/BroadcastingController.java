package b203.varium.broadcasting.controller;

import b203.varium.broadcasting.dto.ReqDTO;
import b203.varium.broadcasting.service.BroadcastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/live")
@RequiredArgsConstructor
public class BroadcastingController {

    private final BroadcastingService broadcastingService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> createBroadcasting(ReqDTO reqDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return ResponseEntity.ok(broadcastingService.startBroadcasting(auth.getName(),
                reqDTO.getBroadcastingTitle(), reqDTO.getBroadcastingImgUrl(), reqDTO.getTagList()));
    }

    // 사용자가 구독중인 스트리머 실시간 방송 목록
    @GetMapping("/list")
    public void viewBroadcastingList() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("user name : " + auth.getName());


    }

    // 랜덤 실시간 방송 5개 조회


    // 시청자 높은 순 방송 5개 조회


    @GetMapping("/end")
    public ResponseEntity<Map<String, String>> deleteBroadcasting() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(broadcastingService.endBroadcasting(auth.getName()));
    }
}