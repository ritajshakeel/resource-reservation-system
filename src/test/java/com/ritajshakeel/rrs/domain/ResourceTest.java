package com.ritajshakeel.rrs.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.Test;

public class ResourceTest {

    @Test
    public void testCreatingResourceWithNullNameThrowsException() {
        assertThatThrownBy(() -> new Resource(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Name must not be null");
    }

    @Test
    public void testCreatingResourceWithEmptyNameThrowsException() {
        assertThatThrownBy(() -> new Resource(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Name must not be empty");
    }
    
    @Test
    public void testGetNameReturnsConstructorValue() {
        Resource resource = new Resource("Meeting Room A");

        assertThat(resource.getName()).isEqualTo("Meeting Room A");
    }
}