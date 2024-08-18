package com.example.webservice.config;

import com.example.webservice.dto.UserDTO;
import com.example.webservice.repository.UserFeignClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserFeignClient userFeignClient;

    @Autowired
    public CustomAuthenticationProvider(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }
//
//@Override
//public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//    String username = authentication.getName();
//    String password = (String) authentication.getCredentials();
//    try {
//        UserDTO userDTO = userFeignClient.getUserByUsername(username);
//
//        if (userDTO != null) {
//            boolean passwordMatches = userDTO.getPassword().equals(password);
//
//            if (passwordMatches) {
//                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userDTO.getRole());
//                List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);
//
//                UserDetails userDetails = org.springframework.security.core.userdetails.User
//                        .withUsername(userDTO.getUsername())
//                        .password(userDTO.getPassword())
//                        .authorities(authorities)
//                        .build();
//
//                return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
//            } else {
//                throw new BadCredentialsException("Invalid credentials");
//            }
//        } else {
//            throw new BadCredentialsException("User details are missing");
//        }
//    } catch (FeignException e) {
//        throw new BadCredentialsException("Authentication failed", e);
//    }
//}
@Override
public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName(); // Это возвращает то, что было установлено
    String password = (String) authentication.getCredentials();
    try {
        UserDTO userDTO = userFeignClient.getUserByUsername(username);

        if (userDTO != null) {
            // Проверьте, правильно ли происходит проверка пароля
            boolean passwordMatches = userDTO.getPassword().equals(password); // Пример: для зашифрованных паролей используется другой метод

            if (passwordMatches) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userDTO.getRole());
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(authority);

                UserDetails userDetails = org.springframework.security.core.userdetails.User
                        .withUsername(userDTO.getUsername())
                        .password(userDTO.getPassword()) // Убедитесь, что пароль зашифрован
                        .authorities(authorities)
                        .build();

                return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            } else {
                throw new BadCredentialsException("Invalid credentials");
            }
        } else {
            throw new BadCredentialsException("User details are missing");
        }
    } catch (FeignException e) {
        throw new BadCredentialsException("Authentication failed", e);
    }
}



    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
