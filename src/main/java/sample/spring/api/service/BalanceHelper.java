package sample.spring.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.spring.domain.balance.UserBalance;
import sample.spring.domain.balance.UserBalanceRepository;
import sample.spring.domain.history.UserBalanceHistory;
import sample.spring.domain.history.UserBalanceHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceHelper {

  private final UserBalanceRepository userBalanceRepository;
  private final UserBalanceHistoryRepository userBalanceHistoryRepository;

  @Transactional
  public boolean chargeBalance(Long userId, int chargeAmount) {
    UserBalance userBalance = userBalanceRepository.findByUserId(userId).orElseThrow();
    int balanceBefore = userBalance.getBalance();
    int balanceAfter = userBalance.getBalance() + chargeAmount;
    int lastVersion = userBalance.getVersion();

    int updated = userBalanceRepository.updateBalanceWithVersion(userBalance.getId(), balanceAfter, lastVersion);
    log.info("updated :: {}", updated);

    if(updated > 0) {
      saveBalanceHistory(userBalance.getId(), balanceBefore, balanceAfter);
      return true;
    } else {
      return false;
    }

  }


  @Transactional
  public boolean chargeBalanceWithJpaVersion(Long userId, int chargeAmount) {
    UserBalance userBalance = userBalanceRepository.findByUserId(userId).orElseThrow();
    int balanceBefore = userBalance.getBalance();
    int balanceAfter = userBalance.getBalance() + chargeAmount;

    userBalance.increaseBalance(chargeAmount);
    saveBalanceHistory(userBalance.getId(), balanceBefore, balanceAfter);

    return true;
  }

  private void saveBalanceHistory(Long balanceId, int beforeBalance, int afterBalance) {
    UserBalanceHistory history = UserBalanceHistory.builder()
        .balanceId(balanceId)
        .balanceBefore(beforeBalance)
        .balanceAfter(afterBalance)
        .build();
    userBalanceHistoryRepository.save(history);
  }
}
