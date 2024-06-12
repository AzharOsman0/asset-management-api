//Entry point for springboot application
package com.example.demo;

//Spring boot imports
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Declaring class
@SpringBootApplication
public class AssetManagementApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetManagementApiApplication.class, args);
	}

}
