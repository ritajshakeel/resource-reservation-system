package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

public class UserTest {

    @Test
    public void testCreatingUserWithNullNameThrowsException() {
        assertThatThrownBy(() -> new User(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Name must not be null");
    }
    
    @Test
    public void testCreatingUserWithEmptyNameThrowsException() {
        assertThatThrownBy(() -> new User(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Name must not be empty");
    }
}