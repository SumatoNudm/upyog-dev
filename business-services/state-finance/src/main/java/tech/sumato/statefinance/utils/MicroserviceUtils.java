package tech.sumato.statefinance.utils;

import io.swagger.util.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.sumato.statefinance.web.models.BudgetStateActionDTO;
import tech.sumato.statefinance.web.models.BudgetStateActionResponse;
import tech.sumato.statefinance.web.models.RequestInfo;
import tech.sumato.statefinance.web.models.StateActionResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.json.XMLTokener.entity;

@Service
public class MicroserviceUtils {



    @Value("${egov.finance.host}")
    private String egovFinanceHost;

    @Value("${egov.finance.host.local}")
    private String egovFinanceHostLocal;

    @Value("${egov.finance.budget.action}")
    private String financeStateActionUrl;

    @Autowired
    private RestTemplate restTemplate;


    private final Logger LOGGER = LoggerFactory.getLogger(MicroserviceUtils.class);

    public RequestInfo createRequestInfo() {
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setApiId("apiId");
        requestInfo.setVer("ver");
        requestInfo.setTs(getEpochDate(new Date()));
        return requestInfo;
    }


    public static Long getEpochDate(Date date) {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
        // Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(date);
        long epoch = Instant.from(fmt.parse(strDate)).toEpochMilli();
        return epoch;
    }


    public String getFinanceHost(String tenantId) {

        LOGGER.info("local:" + egovFinanceHostLocal);

        if (null != egovFinanceHostLocal && !egovFinanceHostLocal.isEmpty()) {
            return egovFinanceHostLocal;
        }

        if (tenantId == null || !tenantId.contains(".")) {
            throw new IllegalArgumentException("Invalid tenantId: " + tenantId);
        }

        String[] parts = tenantId.split("\\.");

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid tenant structure: " + tenantId);
        }

        String ulbCode = parts[1];

        String[] financeHostParts = egovFinanceHost.split("//");

        if (financeHostParts.length < 2) {
            throw new IllegalArgumentException("Invalid finance host url: " + tenantId);
        }

        return String.format("%s//%s-%s", financeHostParts[0], ulbCode, financeHostParts[1]);
    }


    public void submitBudgetAction(BudgetStateActionDTO budgetStateActionDTO, String tenantId) throws Exception {

        String url = getFinanceHost(tenantId) + financeStateActionUrl;

        LOGGER.info(url);


        HttpHeaders headers = new HttpHeaders();
        headers.set("REFERER", "");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BudgetStateActionDTO> requestObj = new HttpEntity<>(budgetStateActionDTO, headers);

        StateActionResponse response = restTemplate.postForObject(url, requestObj, StateActionResponse.class);




//        restTemplate.postForObject(url, budgetStateActionDTO, BudgetStateActionResponse.class);

    }


}
