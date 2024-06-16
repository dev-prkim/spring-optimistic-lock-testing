package sample.spring.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
class UserBalanceChargingServiceConcurrentTest {

  @Autowired
  private BalanceChargingService balanceChargingService;

  @Autowired
  private UserBalanceRepository userBalanceRepository;

  @Autowired
  private UserBalanceHistoryRepository userBalanceHistoryRepository;

  @AfterEach
  void tearDown() {
    userBalanceHistoryRepository.deleteAllInBatch();
    userBalanceRepository.deleteAllInBatch();
  }

  @DisplayName("JPA 낙관적 락을 적용하여 동시 업데이트 요청을 처리한다.")
  @Test
  void concurrentRequestWithJpaOptimisticLock() throws InterruptedException {
    // given
    Long userId = 1L;
    UserBalance userBalance = createBalance(userId, 0);

    int concurrentRequestCount = 10;
    CountDownLatch latch = new CountDownLatch(concurrentRequestCount);
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    // when
    for (int i = 0; i < concurrentRequestCount; i++) {
      executorService.submit(() -> {
        try {
          balanceChargingService.chargeBalanceWithJpaOptimisticLock(userId, 100);
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executorService.shutdown();

    // then
    UserBalance afterUserBalance = userBalanceRepository.findByUserId(userId).orElse(null);
    List<UserBalanceHistory> histories = userBalanceHistoryRepository.findByBalanceId(userBalance.getId());

    assertThat(afterUserBalance.getBalance()).isEqualTo(1000);
    assertThat(histories).hasSize(10);

  }


  @DisplayName("낙관적 락을 적용하여 동시 업데이트 요청을 처리한다.")
  @Test
  void concurrentRequestWithOptimisticLock() throws InterruptedException {
    // given
    Long userId = 1L;
    UserBalance userBalance = createBalance(userId, 0);

    int concurrentRequestCount = 10;
    CountDownLatch latch = new CountDownLatch(concurrentRequestCount);
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    // when
    for (int i = 0; i < concurrentRequestCount; i++) {
      executorService.submit(() -> {
        try {
          balanceChargingService.chargeBalanceWithOptimisticLock(userId, 100);
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    executorService.shutdown();

    // then
    UserBalance afterUserBalance = userBalanceRepository.findByUserId(userId).orElse(null);
    List<UserBalanceHistory> histories = userBalanceHistoryRepository.findByBalanceId(userBalance.getId());

    assertThat(afterUserBalance.getBalance()).isEqualTo(1000);
    assertThat(histories).hasSize(10);

  }


  private UserBalance createBalance(Long userId, int balanceAmt) {
    UserBalance userBalance = UserBalance.builder()
        .userId(userId)
        .balance(balanceAmt)
        .build();
    return userBalanceRepository.save(userBalance);
  }
}
