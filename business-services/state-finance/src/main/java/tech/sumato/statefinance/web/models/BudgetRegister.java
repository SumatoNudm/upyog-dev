package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * DMA-level Budget Register definition
 */
@ApiModel(description = "DMA-level Budget Register definition")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2026-01-17T13:44:07.086+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetRegister   {
        @JsonProperty("id")
        private String id = null;

        @JsonProperty("budgetRegisterNumber")
        private String budgetRegisterNumber = null;

        @JsonProperty("budgetRegisterName")
        private String budgetRegisterName = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("isActive")
        private Boolean isActive = true;

        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;


}

