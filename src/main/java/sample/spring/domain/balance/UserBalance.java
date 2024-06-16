package sample.spring.domain.balance;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.spring.domain.BaseEntity;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Entity
public class UserBalance extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private int balance;

  @Version
  private int version;

  public void increaseBalance(int amount) {
    if(amount < 0) {
      throw new IllegalArgumentException("충전 금액이 0보다 작습니다.");
    }
    this.balance += amount;
    this.version++;
  }

  @Builder
  private UserBalance(Long userId, int balance) {
    this.userId = userId;
    this.balance = balance;
  }

}
