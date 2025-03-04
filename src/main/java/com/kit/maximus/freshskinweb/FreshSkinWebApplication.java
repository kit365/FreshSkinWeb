package com.kit.maximus.freshskinweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching  // Bật cache
public class FreshSkinWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreshSkinWebApplication.class, args);
	}

}
