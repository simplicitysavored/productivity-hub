package xyz.yuanjin.project.productivityhub.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("xyz.yuanjin.project.productivityhub.infrastructure.repository")
@SpringBootApplication(scanBasePackages = "xyz.yuanjin.project.productivityhub")
public class ProductivityHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductivityHubApplication.class, args);
    }

}
