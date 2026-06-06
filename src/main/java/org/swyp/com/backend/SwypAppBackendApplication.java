package org.swyp.com.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SwypAppBackendApplication {

    static void main(String[] args) {
        SpringApplication.run(SwypAppBackendApplication.class, args);
    }
}
