package com.test.mi_negocio;

import com.test_alquimiasoft.mi_negocio.MiNegocioApplication;  
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = MiNegocioApplication.class)
class MiNegocioApplicationTests {

    @Test
    void contextLoads() {
    }

}
