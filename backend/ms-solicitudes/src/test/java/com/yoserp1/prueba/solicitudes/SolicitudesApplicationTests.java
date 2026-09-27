package com.yoserp1.prueba.solicitudes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.outbox.enabled=false")
class SolicitudesApplicationTests {

	@Test
	void contextLoads() {
	}

}
