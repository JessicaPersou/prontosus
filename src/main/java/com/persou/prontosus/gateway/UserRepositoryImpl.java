package com.persou.prontosus.gateway;

import com.persou.prontosus.config.exceptions.ResourceNotFoundException;
import com.persou.prontosus.config.mapper.UserMapper;
import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
import com.persou.prontosus.gateway.database.jpa.repository.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    public static final String USER_NOT_FOUND = "Usuário não existe";
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;


    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByProfessionalDocument(String professionalDocument) {
        return Optional.empty();
    }

    @Override
    public List<User> findByRoleAndActiveTrue(ProfessionalRole role) {
        return List.of();
    }

    @Override
    public List<User> findByActiveTrue() {
        return List.of();
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsByProfessionalDocument(String professionalDocument) {
        return false;
    }

    @Override
    public List<User> findByRoleAndNameContaining(ProfessionalRole role, String name) {
        return List.of();
    }

    @Override
    public User save(User user) {
        var existingUser = userJpaRepository.findById(user.id());
        if (existingUser.isEmpty()) {
            throw new ResourceNotFoundException(USER_NOT_FOUND);
        }
        var toSave = userMapper.toEntity(user);
        var save = userJpaRepository.save(toSave);
        return userMapper.toDomain(save);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomain);
    }
}
