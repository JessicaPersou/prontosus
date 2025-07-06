package com.persou.prontosus.gateway.database.jpa;

import com.persou.prontosus.domain.enums.ProfessionalRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 3, max = 50, message = "Nome de usuário deve ter entre 3 e 50 caracteres")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    @Column(unique = true, nullable = false, length = 20)
    @NotBlank(message = "Documento profissional é obrigatório")
    private String professionalDocument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfessionalRole role;

    @Column(length = 100)
    private String specialty;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "healthcareProfessional", fetch = FetchType.LAZY)
    private List<MedicalRecordEntity> medicalRecords;

    @OneToMany(mappedBy = "healthcareProfessional", fetch = FetchType.LAZY)
    private List<AppointmentEntity> appointments;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
