package sample.spring.domain.history;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.spring.domain.BaseEntity;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Entity
public class UserBalanceHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long balanceId;

  private Long userId;

  private int balanceBefore;

  private int balanceAfter;

  @Builder
  private UserBalanceHistory(Long balanceId, Long userId, int balanceBefore, int balanceAfter) {
    this.balanceId = balanceId;
    this.userId = userId;
    this.balanceBefore = balanceBefore;
    this.balanceAfter = balanceAfter;
  }
}
