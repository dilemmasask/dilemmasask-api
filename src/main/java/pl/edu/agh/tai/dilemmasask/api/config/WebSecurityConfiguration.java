package pl.edu.agh.tai.dilemmasask.api.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import pl.edu.agh.tai.dilemmasask.api.model.User;
import pl.edu.agh.tai.dilemmasask.api.repository.UserRepository;

import java.time.LocalDateTime;

@EnableResourceServer
@Configuration
public class WebSecurityConfiguration extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/posts/**", "/posts")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/posts/**")
                .permitAll();
    }

    @Bean
    public PrincipalExtractor principalExtractor(UserRepository userRepository) {
        return map -> {
            String principalId = (String) map.get("id");
            User user = userRepository.findByPrincipalId(principalId);
            if (user == null) {
                user = new User();
                user.setEmail((String) map.get("email"));
                user.setFullName((String) map.get("name"));
                user.setPrincipalId(principalId);
                user.setCreated(LocalDateTime.now());
                userRepository.save(user);
            }
            return user;
        };
    }
}