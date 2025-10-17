// src/main/java/com/team8/diary/config/SecurityBeans.java
package com.team8.diary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
<<<<<<< HEAD:diary-service/src/main/java/com/team8/diary/SecurityConfig.java
import org.springframework.security.web.SecurityFilterChain;
=======
import org.springframework.security.crypto.password.PasswordEncoder;
>>>>>>> main:diary-service/src/main/java/com/team8/diary/config/SecurityBeans.java

@Configuration
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
<<<<<<< HEAD:diary-service/src/main/java/com/team8/diary/SecurityConfig.java

    //test
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
=======
}
>>>>>>> main:diary-service/src/main/java/com/team8/diary/config/SecurityBeans.java
