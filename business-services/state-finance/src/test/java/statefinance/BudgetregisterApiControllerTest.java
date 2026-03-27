package statefinance;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API tests for BudgetregisterApiController
 */
@Ignore
@RunWith(SpringRunner.class)
@Import(TestConfiguration.class)
public class BudgetregisterApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void budgetregisterCreatePostSuccess() throws Exception {
        mockMvc.perform(post("/dma-finance/budgetregister/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void budgetregisterCreatePostFailure() throws Exception {
        mockMvc.perform(post("/dma-finance/budgetregister/_create").contentType(MediaType
                        .APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

}
