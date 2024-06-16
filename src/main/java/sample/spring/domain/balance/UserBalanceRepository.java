package sample.spring.domain.balance;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {

  Optional<UserBalance> findByUserId(Long userId);

  @Modifying
  @Query("update UserBalance b set b.balance = :amount, b.version = b.version + 1 where b.id = :id and b.version = :version")
  int updateBalanceWithVersion(Long id, int amount, int version);
}
