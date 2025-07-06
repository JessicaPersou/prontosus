package com.persou.prontosus.gateway;

import com.persou.prontosus.domain.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface PatientRepository {

    @Transactional(readOnly = true)
    Optional<Patient> findByCpf(String cpf);

    @Transactional(readOnly = true)
    Optional<Patient> findById(String id);

    @Transactional(readOnly = true)
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    @Transactional(readOnly = true)
    List<Patient> findAll();

    @Transactional(readOnly = true)
    boolean existsByCpf(String cpf);

    @Transactional(readOnly = true)
    List<Patient> findByPhoneNumber(String phone);

    @Transactional
    Patient save(Patient patient);
}
