CREATE TABLE PATIENTS
(
    ID                      VARCHAR(36) PRIMARY KEY NOT NULL,
    CPF                     VARCHAR(11)             NOT NULL UNIQUE,
    FULL_NAME               VARCHAR(200)            NOT NULL,
    BIRTH_DATE              DATE                    NOT NULL,
    GENDER                  VARCHAR(10)             NOT NULL,
    PHONE_NUMBER            VARCHAR(15),
    EMAIL                   VARCHAR(100),
    ZIP_CODE                VARCHAR(10),
    STREET                  VARCHAR(200),
    NUMBER                  VARCHAR(10),
    COMPLEMENT              VARCHAR(100),
    NEIGHBORHOOD            VARCHAR(100),
    CITY                    VARCHAR(100),
    STATE                   VARCHAR(2),
    EMERGENCY_CONTACT_NAME  VARCHAR(200),
    EMERGENCY_CONTACT_PHONE VARCHAR(15),
    KNOWN_ALLERGIES         TEXT,
    CURRENT_MEDICATIONS     TEXT,
    CHRONIC_CONDITIONS      TEXT,
    CREATED_AT              TIMESTAMP               NOT NULL,
    UPDATED_AT              TIMESTAMP               NOT NULL
);

CREATE TABLE USERS
(
    ID                    VARCHAR(36) PRIMARY KEY NOT NULL,
    USERNAME              VARCHAR(50)             NOT NULL UNIQUE,
    PASSWORD              VARCHAR(255)            NOT NULL,
    FULL_NAME             VARCHAR(200)            NOT NULL,
    EMAIL                 VARCHAR(100)            NOT NULL UNIQUE,
    PROFESSIONAL_DOCUMENT VARCHAR(20)             NOT NULL UNIQUE,
    ROLE                  VARCHAR(50)             NOT NULL,
    SPECIALTY             VARCHAR(100),
    ACTIVE                BOOLEAN                 NOT NULL DEFAULT TRUE,
    CREATED_AT            TIMESTAMP               NOT NULL,
    UPDATED_AT            TIMESTAMP               NOT NULL,
    LAST_LOGIN_AT         TIMESTAMP
);

CREATE TABLE APPOINTMENTS
(
    ID                         VARCHAR(36) PRIMARY KEY NOT NULL,
    PATIENT_ID                 VARCHAR(36)             NOT NULL,
    HEALTHCARE_PROFESSIONAL_ID VARCHAR(36)             NOT NULL,
    SCHEDULED_DATE_TIME        TIMESTAMP               NOT NULL,
    STATUS                     VARCHAR(50)             NOT NULL,
    TYPE                       VARCHAR(50)             NOT NULL,
    REASON                     TEXT,
    NOTES                      TEXT,
    CREATED_AT                 TIMESTAMP               NOT NULL,
    UPDATED_AT                 TIMESTAMP               NOT NULL,
    FOREIGN KEY (PATIENT_ID) REFERENCES PATIENTS (ID),
    FOREIGN KEY (HEALTHCARE_PROFESSIONAL_ID) REFERENCES USERS (ID)
);

CREATE TABLE MEDICAL_RECORDS
(
    ID                         VARCHAR(36) PRIMARY KEY NOT NULL,
    PATIENT_ID                 VARCHAR(36)             NOT NULL,
    HEALTHCARE_PROFESSIONAL_ID VARCHAR(36)             NOT NULL,
    APPOINTMENT_ID             VARCHAR(36),
    CONSULTATION_DATE          TIMESTAMP               NOT NULL,
    CHIEF_COMPLAINT            TEXT                    NOT NULL,
    HISTORY_OF_PRESENT_ILLNESS TEXT,
    PHYSICAL_EXAMINATION       TEXT,
    DIAGNOSIS                  TEXT,
    TREATMENT                  TEXT,
    PRESCRIPTIONS              TEXT,
    OBSERVATIONS               TEXT,
    SYSTOLIC_PRESSURE          INT,
    DIASTOLIC_PRESSURE         INT,
    HEART_RATE                 INT,
    TEMPERATURE                DOUBLE PRECISION,
    RESPIRATORY_RATE           INT,
    WEIGHT                     DOUBLE PRECISION,
    HEIGHT                     DOUBLE PRECISION,
    OXYGEN_SATURATION          DOUBLE PRECISION,
    CREATED_AT                 TIMESTAMP               NOT NULL,
    UPDATED_AT                 TIMESTAMP               NOT NULL,
    FOREIGN KEY (PATIENT_ID) REFERENCES PATIENTS (ID),
    FOREIGN KEY (HEALTHCARE_PROFESSIONAL_ID) REFERENCES USERS (ID),
    FOREIGN KEY (APPOINTMENT_ID) REFERENCES APPOINTMENTS (ID)
);

CREATE TABLE FILE_ATTACHMENTS
(
    ID                VARCHAR(36) PRIMARY KEY NOT NULL,
    MEDICAL_RECORD_ID VARCHAR(36)             NOT NULL,
    FILE_NAME         VARCHAR(255)            NOT NULL,
    FILE_PATH         VARCHAR(255)            NOT NULL,
    CONTENT_TYPE      VARCHAR(100)            NOT NULL,
    FILE_SIZE         BIGINT                  NOT NULL,
    FILE_TYPE         VARCHAR(50)             NOT NULL,
    DESCRIPTION       TEXT,
    UPLOADED_AT       TIMESTAMP               NOT NULL,
    UPLOADED_BY       VARCHAR(36)             NOT NULL,
    FOREIGN KEY (MEDICAL_RECORD_ID) REFERENCES MEDICAL_RECORDS (ID),
    FOREIGN KEY (UPLOADED_BY) REFERENCES USERS (ID)
);