package com.persou.prontosus.gateway;

import com.persou.prontosus.domain.User;
import com.persou.prontosus.domain.enums.ProfessionalRole;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProfessionalDocument(String professionalDocument);

    List<User> findByRoleAndActiveTrue(ProfessionalRole role);

    List<User> findByActiveTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByProfessionalDocument(String professionalDocument);

    List<User> findByRoleAndNameContaining(ProfessionalRole role, String name);

    User save(User user);

    Optional<User> findById(Long id);
}
