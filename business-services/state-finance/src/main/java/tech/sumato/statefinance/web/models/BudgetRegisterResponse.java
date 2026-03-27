package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Response object for Budget Register operations
 */
@ApiModel(description = "Response object for Budget Register operations")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2026-01-17T13:44:07.086+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetRegisterResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("BudgetRegister")
        @Valid
        private List<BudgetRegister> budgetRegister = null;


        public BudgetRegisterResponse addBudgetRegisterItem(BudgetRegister budgetRegisterItem) {
            if (this.budgetRegister == null) {
            this.budgetRegister = new ArrayList<>();
            }
        this.budgetRegister.add(budgetRegisterItem);
        return this;
        }

}

