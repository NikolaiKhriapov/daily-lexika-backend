package my.project.dailybudget.repositories.account;

import my.project.dailybudget.entities.account.ExpenseOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseOperationRepository extends JpaRepository<ExpenseOperation, Long> {

    List<ExpenseOperation> findAllByUserIdAndAccountFrom_IdOrderByTimestampDesc(Long userId, Long id);
}
