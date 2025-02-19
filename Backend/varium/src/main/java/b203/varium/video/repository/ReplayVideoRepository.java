package b203.varium.video.repository;

import b203.varium.video.entity.ReplayVideo;
import b203.varium.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplayVideoRepository extends JpaRepository<ReplayVideo, Integer> {

    // 데이터 JPA 사용할 때 JPQL 사용하는 방식
    List<ReplayVideo> findAllByBroadcastStation_Id(int id);

    @Query("SELECT COALESCE(MAX(rv.replayVideoNo), 0) FROM ReplayVideo rv")
    int findMaxReplayVideoNo();

    ReplayVideo findById(int videoNo);
}