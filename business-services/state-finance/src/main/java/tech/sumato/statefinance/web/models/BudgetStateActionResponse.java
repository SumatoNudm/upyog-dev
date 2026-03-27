package tech.sumato.statefinance.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BudgetStateActionResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("message")
    private String message;

}
