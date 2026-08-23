package com.ritajshakeel.rrs.guice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceLearningTest {

    private interface IMyService {
    }

    private static class MyService implements IMyService {
    }

    private static class MyClient {
        IMyService service;

        @Inject
        public MyClient(IMyService service) {
            this.service = service;
        }
    }

    @Test
    public void injectAbstractTypeViaBinding() {
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(IMyService.class).to(MyService.class);
            }
        };
        Injector injector = Guice.createInjector(module);

        MyClient client = injector.getInstance(MyClient.class);

        assertThat(client.service).isInstanceOf(MyService.class);
    }
}