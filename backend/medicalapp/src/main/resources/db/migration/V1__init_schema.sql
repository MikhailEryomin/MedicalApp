CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE doctors (
                         id SERIAL PRIMARY KEY,
                         user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                         first_name VARCHAR(100) NOT NULL,
                         last_name VARCHAR(100) NOT NULL,
                         specialization VARCHAR(200),
                         licence_number VARCHAR(100)
);

CREATE TABLE patients (
                          id SERIAL PRIMARY KEY,
                          user_id INTEGER NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
                          first_name VARCHAR(100) NOT NULL,
                          last_name VARCHAR(100) NOT NULL,
                          birth_date DATE,
                          gender VARCHAR(10),
                          allergies JSONB DEFAULT '[]',
                          chronic_diseases JSONB DEFAULT '[]'
);

CREATE TABLE doctor_patient (
                                doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
                                patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                                PRIMARY KEY (doctor_id, patient_id)
);

CREATE TABLE medicines (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           form VARCHAR(20) NOT NULL,
                           default_dosage VARCHAR(100)
);

CREATE TABLE prescriptions (
                               id SERIAL PRIMARY KEY,
                               doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
                               patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
                               medicine_id INTEGER NOT NULL REFERENCES medicines(id) ON DELETE CASCADE,
                               dosage VARCHAR(100) NOT NULL,
                               frequency INTEGER NOT NULL,
                               duration_days INTEGER NOT NULL,
                               start_date DATE NOT NULL DEFAULT CURRENT_DATE,
                               end_date DATE NOT NULL,
                               status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                               notes TEXT
);

CREATE TABLE prescription_logs (
                                   id SERIAL PRIMARY KEY,
                                   prescription_id INTEGER NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
                                   taken_at TIMESTAMP NOT NULL DEFAULT NOW()
);