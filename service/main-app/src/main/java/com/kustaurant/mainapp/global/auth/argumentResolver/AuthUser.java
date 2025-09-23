package com.kustaurant.mainapp.global.auth.argumentResolver;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthUser {
}
