package tech.sumato.statefinance.web.models;


import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Audit information
 */
@ApiModel(description = "Audit information")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2026-01-17T13:44:07.086+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails   {
        @JsonProperty("createdBy")
        private String createdBy = null;

        @JsonProperty("createdDate")
        private Long createdDate = null;

        @JsonProperty("lastModifiedBy")
        private String lastModifiedBy = null;

        @JsonProperty("lastModifiedDate")
        private Long lastModifiedDate = null;


}

