package com.persou.prontosus.gateway;

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

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
            .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByProfessionalDocument(String professionalDocument) {
        return userJpaRepository.findByProfessionalDocument(professionalDocument)
            .map(userMapper::toDomain);
    }

    @Override
    public List<User> findByRoleAndActiveTrue(ProfessionalRole role) {
        return userJpaRepository.findByRoleAndActiveTrue(role)
            .stream()
            .map(userMapper::toDomain)
            .toList();
    }

    @Override
    public List<User> findByActiveTrue() {
        return userJpaRepository.findByActiveTrue()
            .stream()
            .map(userMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByProfessionalDocument(String professionalDocument) {
        return userJpaRepository.existsByProfessionalDocument(professionalDocument);
    }

    @Override
    public List<User> findByRoleAndNameContaining(ProfessionalRole role, String name) {
        return userJpaRepository.findByRoleAndNameContaining(role, name)
            .stream()
            .map(userMapper::toDomain)
            .toList();
    }

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(user);
        var savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id)
            .map(userMapper::toDomain);
    }
}