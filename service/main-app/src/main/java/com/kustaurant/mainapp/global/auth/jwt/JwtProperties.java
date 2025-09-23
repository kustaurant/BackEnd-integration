package com.kustaurant.mainapp.global.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    @DurationUnit(SECONDS)
    private Duration accessTtl;
    @DurationUnit(SECONDS)
    private Duration refreshTtl;
}
