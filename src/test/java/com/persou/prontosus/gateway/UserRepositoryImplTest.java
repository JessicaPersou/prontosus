package com.persou.prontosus.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
import com.persou.prontosus.gateway.database.jpa.UserEntity;
import com.persou.prontosus.gateway.database.jpa.repository.UserJpaRepository;
import com.persou.prontosus.mocks.UserMock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userDomain = UserMock.mockDomain();
        userEntity = UserMock.mockEntity();
    }

    @Test
    void shouldFindByUsername() {

        String username = "testuser";
        when(userJpaRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        Optional<User> result = userRepository.findByUsername(username);


        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userDomain);
        verify(userJpaRepository, times(1)).findByUsername(username);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnEmptyWhenUsernameNotFound() {

        String username = "nonexistent";
        when(userJpaRepository.findByUsername(username)).thenReturn(Optional.empty());


        Optional<User> result = userRepository.findByUsername(username);


        assertThat(result).isEmpty();
        verify(userJpaRepository, times(1)).findByUsername(username);
        verify(userMapper, times(0)).toDomain(any());
    }

    @Test
    void shouldFindByEmail() {

        String email = "test@email.com";
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        Optional<User> result = userRepository.findByEmail(email);


        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userDomain);
        verify(userJpaRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {

        String email = "nonexistent@email.com";
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.empty());


        Optional<User> result = userRepository.findByEmail(email);


        assertThat(result).isEmpty();
        verify(userJpaRepository, times(1)).findByEmail(email);
        verify(userMapper, times(0)).toDomain(any());
    }

    @Test
    void shouldFindByProfessionalDocument() {

        String document = "CRM123456";
        when(userJpaRepository.findByProfessionalDocument(document)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        Optional<User> result = userRepository.findByProfessionalDocument(document);


        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userDomain);
        verify(userJpaRepository, times(1)).findByProfessionalDocument(document);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldFindByRoleAndActiveTrue() {

        ProfessionalRole role = ProfessionalRole.DOCTOR;
        List<UserEntity> entities = List.of(userEntity);
        List<User> domains = List.of(userDomain);

        when(userJpaRepository.findByRoleAndActiveTrue(role)).thenReturn(entities);
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        List<User> result = userRepository.findByRoleAndActiveTrue(role);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(userJpaRepository, times(1)).findByRoleAndActiveTrue(role);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldFindByActiveTrue() {

        List<UserEntity> entities = List.of(userEntity);
        List<User> domains = List.of(userDomain);

        when(userJpaRepository.findByActiveTrue()).thenReturn(entities);
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        List<User> result = userRepository.findByActiveTrue();


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(userJpaRepository, times(1)).findByActiveTrue();
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnTrueWhenUsernameExists() {

        String username = "existinguser";
        when(userJpaRepository.existsByUsername(username)).thenReturn(true);


        boolean result = userRepository.existsByUsername(username);


        assertThat(result).isTrue();
        verify(userJpaRepository, times(1)).existsByUsername(username);
    }

    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {

        String username = "nonexistentuser";
        when(userJpaRepository.existsByUsername(username)).thenReturn(false);


        boolean result = userRepository.existsByUsername(username);


        assertThat(result).isFalse();
        verify(userJpaRepository, times(1)).existsByUsername(username);
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {

        String email = "existing@email.com";
        when(userJpaRepository.existsByEmail(email)).thenReturn(true);


        boolean result = userRepository.existsByEmail(email);


        assertThat(result).isTrue();
        verify(userJpaRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {

        String email = "nonexistent@email.com";
        when(userJpaRepository.existsByEmail(email)).thenReturn(false);


        boolean result = userRepository.existsByEmail(email);


        assertThat(result).isFalse();
        verify(userJpaRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldReturnTrueWhenProfessionalDocumentExists() {

        String document = "CRM123456";
        when(userJpaRepository.existsByProfessionalDocument(document)).thenReturn(true);


        boolean result = userRepository.existsByProfessionalDocument(document);


        assertThat(result).isTrue();
        verify(userJpaRepository, times(1)).existsByProfessionalDocument(document);
    }

    @Test
    void shouldReturnFalseWhenProfessionalDocumentDoesNotExist() {

        String document = "CRM999999";
        when(userJpaRepository.existsByProfessionalDocument(document)).thenReturn(false);


        boolean result = userRepository.existsByProfessionalDocument(document);


        assertThat(result).isFalse();
        verify(userJpaRepository, times(1)).existsByProfessionalDocument(document);
    }

    @Test
    void shouldFindByRoleAndNameContaining() {

        ProfessionalRole role = ProfessionalRole.DOCTOR;
        String name = "João";
        List<UserEntity> entities = List.of(userEntity);
        List<User> domains = List.of(userDomain);

        when(userJpaRepository.findByRoleAndNameContaining(role, name)).thenReturn(entities);
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        List<User> result = userRepository.findByRoleAndNameContaining(role, name);


        assertThat(result).hasSize(1);
        assertThat(result).isEqualTo(domains);
        verify(userJpaRepository, times(1)).findByRoleAndNameContaining(role, name);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldSaveUser() {

        when(userMapper.toEntity(userDomain)).thenReturn(userEntity);
        when(userJpaRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        User result = userRepository.save(userDomain);


        assertThat(result).isEqualTo(userDomain);
        verify(userMapper, times(1)).toEntity(userDomain);
        verify(userJpaRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldFindById() {

        String id = "user-id";
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(userDomain);


        Optional<User> result = userRepository.findById(id);


        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userDomain);
        verify(userJpaRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnEmptyWhenIdNotFound() {

        String id = "nonexistent-id";
        when(userJpaRepository.findById(id)).thenReturn(Optional.empty());


        Optional<User> result = userRepository.findById(id);


        assertThat(result).isEmpty();
        verify(userJpaRepository, times(1)).findById(id);
        verify(userMapper, times(0)).toDomain(any());
    }
}