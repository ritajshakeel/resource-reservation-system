package com.ritajshakeel.rrs.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ritajshakeel.rrs.domain.Resource;
import com.ritajshakeel.rrs.persistence.ResourceTransactionManager;
import com.ritajshakeel.rrs.repository.ResourceRepository;

public class ResourceServiceTest {

    private ResourceRepository repository;
    private ResourceTransactionManager transactionManager;
    private ResourceService service;

    @Before
    public void setUp() {
        repository = mock(ResourceRepository.class);
        transactionManager = mock(ResourceTransactionManager.class);
        when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
            Function<ResourceRepository, ?> code = invocation.getArgument(0);
            return code.apply(repository);
        });
        service = new ResourceService(transactionManager);
    }

    @Test
    public void testRegisterSavesNewResource() {
        service.register("Meeting Room A");

        ArgumentCaptor<Resource> captor = ArgumentCaptor.forClass(Resource.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Meeting Room A");
    }

    @Test
    public void testListAllReturnsResourcesFromRepository() {
        Resource roomA = new Resource("Meeting Room A");
        Resource roomB = new Resource("Meeting Room B");
        when(repository.findAll()).thenReturn(Arrays.asList(roomA, roomB));

        List<Resource> result = service.listAll();

        assertThat(result).containsExactly(roomA, roomB);
    }
}