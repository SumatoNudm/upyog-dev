package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import tech.sumato.statefinance.web.entity.BudgetRegister;

import java.util.Date;

@Getter
@Setter
public class BudgetRegisterDTO {

    private Long id;

    private Long budgetRegisterId;

    private String tenantId;

    private String cityName;

    private String budgetRegisterNumber;

    private String budgetRegisterName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startingDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endingDate;

    private String currentFy;

    private String nextFy;

    public BudgetRegister mapToEntity() {
        BudgetRegister budgetRegister = new BudgetRegister();
        budgetRegister.setBudgetRegisterId(budgetRegisterId);
        budgetRegister.setTenantId(tenantId);
        budgetRegister.setCityName(cityName);
        budgetRegister.setBudgetRegisterName(budgetRegisterName);
        budgetRegister.setBudgetRegisterNumber(budgetRegisterNumber);
        budgetRegister.setStartingDate(startingDate);
        budgetRegister.setEndingDate(endingDate);
        budgetRegister.setCurrentFy(currentFy);
        budgetRegister.setNextFy(nextFy);
        return budgetRegister;
    }
}
