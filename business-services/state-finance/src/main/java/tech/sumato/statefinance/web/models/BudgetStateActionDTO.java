package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import digit.models.coremodels.RequestInfoWrapper;

import javax.validation.constraints.NotNull;


public class BudgetStateActionDTO extends RequestInfoWrapper {

    @NotNull
    public BudgetRegisterAction action;

    public enum BudgetRegisterAction {
        APPROVE,
        REJECT
    }

    public String remarks;

    public String tenantId;

    public BudgetRegisterDTO budgetRegister;


}