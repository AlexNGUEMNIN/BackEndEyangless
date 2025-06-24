package com.eyangless.Back;

import java.util.HashSet;

import com.eyangless.Back.Entity.Role;
import com.eyangless.Back.ServiceImpl.RoleServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EyanglessApplication {

	public static void main(String[] args) {
		SpringApplication.run(EyanglessApplication.class, args);
	}

	@Bean
	CommandLineRunner start(RoleServiceImpl rs){
		return args -> {

//			rs.creer(new Role("Moderateur", new HashSet<>()));
			rs.creer(new Role("Bailleur", new HashSet<>()));
//			rs.creer(new Role("Locataire", new HashSet<>()));
		};
	}
}
