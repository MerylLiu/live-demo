package com.swy.live;

import com.jds.core.common.ServiceLocator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.swy.*", "com.jds.*"})
@SpringBootApplication
public class LiveApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(LiveApplication.class, args);

        ServiceLocator locator = new ServiceLocator();
        locator.setApplicationContext(ctx);
    }

}
