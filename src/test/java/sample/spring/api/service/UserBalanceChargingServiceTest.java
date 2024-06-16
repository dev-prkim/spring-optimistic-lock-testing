package sample.spring.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import sample.spring.domain.balance.UserBalance;
import sample.spring.domain.balance.UserBalanceRepository;
import sample.spring.domain.history.UserBalanceHistory;
import sample.spring.domain.history.UserBalanceHistoryRepository;

@ActiveProfiles("test")
@SpringBootTest
class UserBalanceChargingServiceTest {

  @Autowired
  private BalanceChargingService balanceService;

  @Autowired
  private UserBalanceRepository userBalanceRepository;

  @Autowired
  private UserBalanceHistoryRepository userBalanceHistoryRepository;

  @AfterEach
  void tearDown() {
    userBalanceHistoryRepository.deleteAllInBatch();
    userBalanceRepository.deleteAllInBatch();
  }

  @DisplayName("낙관적 락을 건 상태에서 단건 처리 시 정상 동작")
  @Test
  void requestWithLock() {
    // given
    Long userId = 1L;
    UserBalance userBalance = createBalance(userId, 0);

    // when
    balanceService.chargeBalanceWithOptimisticLock(userId, 100000);

    // then
    UserBalance afterUserBalance = userBalanceRepository.findByUserId(userId).orElse(null);
    List<UserBalanceHistory> histories = userBalanceHistoryRepository.findByBalanceId(userBalance.getId());

    assertThat(afterUserBalance.getBalance()).isEqualTo(100000);
    assertThat(histories).hasSize(1);

  }

  private UserBalance createBalance(Long userId, int balanceAmt) {
    UserBalance userBalance = UserBalance.builder()
        .userId(userId)
        .balance(balanceAmt)
        .build();
    return userBalanceRepository.save(userBalance);
  }
}
