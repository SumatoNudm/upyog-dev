package tech.sumato.statefinance.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.sumato.statefinance.web.entity.BudgetRegister;

@Repository
public interface StateFinanceRepository extends JpaRepository<BudgetRegister, Long> {


    BudgetRegister findByTenantIdAndBudgetRegisterId(String tenantId, Long budgetRegisterId);

}
