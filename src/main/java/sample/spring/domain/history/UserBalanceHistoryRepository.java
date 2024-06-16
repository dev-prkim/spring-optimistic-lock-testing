package sample.spring.domain.history;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceHistoryRepository extends JpaRepository<UserBalanceHistory, Long> {

  List<UserBalanceHistory> findByBalanceId(Long balanceId);
}
