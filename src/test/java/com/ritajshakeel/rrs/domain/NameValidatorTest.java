package com.ritajshakeel.rrs.domain;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class NameValidatorTest {

    @Test
    public void testPrivateConstructorCanBeInvokedViaReflection() throws Exception {
        Constructor<NameValidator> constructor = NameValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        NameValidator instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}