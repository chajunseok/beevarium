// Spring Data JPA 사용
package b203.varium.board.repository;

import com.pjt.beevarium.broadcastStation.entity.BroadcastStation;
import org.springframework.data.jpa.repository.JpaRepository;
import b203.varium.board.entity.BroadcastStationNotice;

import java.util.List;

public interface BroadcastStationNoticeRepository extends JpaRepository<BroadcastStationNotice, Integer> {
    // Spring Data JPA 기본 CRUD 메소드들이 자동으로 제공됩니다.
    List<BroadcastStationNotice> findByBroadcastStation(BroadcastStation broadcastStation);

}