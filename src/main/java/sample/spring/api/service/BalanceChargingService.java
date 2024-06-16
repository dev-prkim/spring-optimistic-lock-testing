package sample.spring.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceChargingService {

  private final BalanceHelper balanceHelper;
  private static final int MAX_RETRY_CNT = 3;

  public void chargeBalanceWithOptimisticLock(Long userId, int chargeAmount) {
    int retryCnt = 0;
    boolean success = false;

    while (retryCnt < MAX_RETRY_CNT && !success) {
      try {
        success = balanceHelper.chargeBalance(userId, chargeAmount);
        if (!success) {
          log.info("충돌이 발생하여 재시도 합니다. userId={}, retryCnt={}", userId, retryCnt);
          retryCnt++;

          if (retryCnt >= MAX_RETRY_CNT) {
            throw new OptimisticLockingFailureException("충돌이 발생하였습니다.");
          }

          sleep((long) 50 * (retryCnt + 1)); // Exponential Backoff
        }
      } catch (OptimisticLockingFailureException e) {
        log.error("충돌이 발생하였습니다. userId={}", userId);
      }

    }

  }

  public void chargeBalanceWithJpaOptimisticLock(Long userId, int chargeAmount) {
    int retryCnt = 0;
    boolean success = false;

    while (retryCnt < MAX_RETRY_CNT && !success) {
      try {
        success = balanceHelper.chargeBalanceWithJpaVersion(userId, chargeAmount);
      } catch (OptimisticLockingFailureException e) {
        retryCnt++;
        if (retryCnt < MAX_RETRY_CNT) {
          log.info("충돌이 발생하여 재시도합니다. userId={}, retryCount={}", userId, retryCnt);
          sleep((long) 50 * (retryCnt + 1));
        } else {
          log.error("최대 재시도 횟수 초과. userId={}", userId);
          throw e; // 최종 재시도 실패 시 예외를 다시 던짐
        }
      }
    }

  }


  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("재시도 중 인터럽트 발생", ex);
    }
  }


}
