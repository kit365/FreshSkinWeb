package com.kit.maximus.freshskinweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching  // Bật cache
@EnableFeignClients(basePackages = "com.kit.maximus.freshskinweb")
@EnableAsync
@EnableScheduling //bật gửi dữ liệu theo lịch trình
public class FreshSkinWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreshSkinWebApplication.class, args);
	}

}
