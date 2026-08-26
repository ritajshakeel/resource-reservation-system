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

import com.ritajshakeel.rrs.domain.User;
import com.ritajshakeel.rrs.persistence.UserTransactionManager;
import com.ritajshakeel.rrs.repository.UserRepository;

public class UserServiceTest {

    private UserRepository repository;
    private UserTransactionManager transactionManager;
    private UserService service;

    @Before
    public void setUp() {
        repository = mock(UserRepository.class);
        transactionManager = mock(UserTransactionManager.class);
        when(transactionManager.doInTransaction(any())).thenAnswer(invocation -> {
            Function<UserRepository, ?> code = invocation.getArgument(0);
            return code.apply(repository);
        });
        service = new UserService(transactionManager);
    }

    @Test
    public void testRegisterSavesNewUser() {
        service.register("Alice");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Alice");
    }

    @Test
    public void testListAllReturnsUsersFromRepository() {
        User alice = new User("Alice");
        User bob = new User("Bob");
        when(repository.findAll()).thenReturn(Arrays.asList(alice, bob));

        List<User> result = service.listAll();

        assertThat(result).containsExactly(alice, bob);
    }
}