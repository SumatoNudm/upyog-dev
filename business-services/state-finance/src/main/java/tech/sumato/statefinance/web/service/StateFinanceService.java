package tech.sumato.statefinance.web.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.sumato.statefinance.repository.StateFinanceRepository;
import tech.sumato.statefinance.web.entity.BudgetRegister;
import tech.sumato.statefinance.web.models.BudgetRegisterDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StateFinanceService {

    private static final Logger LOG = LoggerFactory.getLogger(StateFinanceService.class);


    @Autowired
    private StateFinanceRepository stateFinanceRepository;


    public BudgetRegister findBudgetRegisterByTenantAndBudgetRegisterId(String tenantId, Long budgetRegisterId) {

        BudgetRegister budgetRegister = stateFinanceRepository.findByTenantIdAndBudgetRegisterId(tenantId, budgetRegisterId);


        return budgetRegister;

    }

    public BudgetRegister saveBudgetRegister(BudgetRegister budgetRegister) {
        return stateFinanceRepository.save(budgetRegister);
    }

    public List<BudgetRegisterDTO> findAllBudgets() {
       List<BudgetRegisterDTO> budgetRegisterDTOS =   stateFinanceRepository.findAll().stream().map(BudgetRegister::toDTO).collect(Collectors.toList());
        return budgetRegisterDTOS;
    }

    public Page<BudgetRegisterDTO> findBudgets(Pageable pageable) {
        return stateFinanceRepository
                .findAll(pageable)
                .map(BudgetRegister::toDTO);
    }


    public BudgetRegister findByTenantIdAndBudgetRegisterId(String tenantId, Long budgetRegisterId) {
        return stateFinanceRepository.findByTenantIdAndBudgetRegisterId(tenantId, budgetRegisterId);
    }

}
