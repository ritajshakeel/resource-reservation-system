package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThat;
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
    
    @Test
    public void testGetNameReturnsConstructorValue() {
        User user = new User("Alice");

        assertThat(user.getName()).isEqualTo("Alice");
    }
    
    @Test
    public void testToStringReturnsName() {
        User user = new User("Alice");

        assertThat(user.toString()).isEqualTo("Alice");
    }
}