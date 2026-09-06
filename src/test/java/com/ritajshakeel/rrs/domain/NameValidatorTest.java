package com.ritajshakeel.rrs.domain;

import java.lang.reflect.Constructor;

import org.junit.Test;

public class NameValidatorTest {

    @Test
    public void testPrivateConstructorCanBeInvokedViaReflection() throws Exception {
        Constructor<NameValidator> constructor = NameValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}