package com.ritajshakeel.rrs.guice;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.Test;

public class RRSModuleTest {

    @Test
    public void testMissingPropertiesFileThrowsIllegalStateException() {
        assertThatThrownBy(() -> new RRSModule("/does-not-exist.properties"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Could not find resource /does-not-exist.properties");
    }

    @Test
    public void testCorruptedStreamThrowsUncheckedIOException() {
        InputStream throwingStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated read failure");
            }
        };

        assertThatThrownBy(() -> new RRSModule(throwingStream, "/db.properties"))
            .isInstanceOf(UncheckedIOException.class)
            .hasMessage("Could not load /db.properties");
    }
}