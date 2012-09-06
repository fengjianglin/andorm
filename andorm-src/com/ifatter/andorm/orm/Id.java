
package com.ifatter.andorm.orm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    java.lang.annotation.ElementType.FIELD
})
public @interface Id {
}
