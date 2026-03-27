package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import tech.sumato.statefinance.util.GenericRequestDeserializer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = GenericRequestDeserializer.class)
public class GenericRequest<T> {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("data")
    private T data;


}