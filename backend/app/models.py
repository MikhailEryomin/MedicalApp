import enum
from datetime import date, datetime

from sqlalchemy import (
    Column, Integer, String, Text, Date, DateTime,
    Enum, ForeignKey, Table, JSON,
)
from sqlalchemy.orm import relationship

from app.database import Base


class UserRole(str, enum.Enum):
    DOCTOR = "DOCTOR"
    PATIENT = "PATIENT"


class Gender(str, enum.Enum):
    MALE = "MALE"
    FEMALE = "FEMALE"


class MedicineForm(str, enum.Enum):
    TABLET = "TABLET"
    SYRUP = "SYRUP"
    INJECTION = "INJECTION"
    OINTMENT = "OINTMENT"


class PrescriptionStatus(str, enum.Enum):
    ACTIVE = "ACTIVE"
    COMPLETED = "COMPLETED"
    CANCELLED = "CANCELLED"

doctor_patient = Table(
    "doctor_patient",
    Base.metadata,
    Column("doctor_id", Integer, ForeignKey("doctors.id", ondelete="CASCADE"), primary_key=True),
    Column("patient_id", Integer, ForeignKey("patients.id", ondelete="CASCADE"), primary_key=True),
)


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, nullable=False, index=True)
    password_hash = Column(String(255), nullable=False)
    role = Column(Enum(UserRole), nullable=False)

    doctor = relationship("Doctor", back_populates="user", uselist=False)
    patient = relationship("Patient", back_populates="user", uselist=False)


class Doctor(Base):
    __tablename__ = "doctors"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False)
    first_name = Column(String(100), nullable=False)
    last_name = Column(String(100), nullable=False)
    specialization = Column(String(200), nullable=True)
    licence_number = Column(String(100), nullable=True)

    user = relationship("User", back_populates="doctor")
    patients = relationship("Patient", secondary=doctor_patient, back_populates="doctors")
    prescriptions = relationship("Prescription", back_populates="doctor")


class Patient(Base):
    __tablename__ = "patients"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False)
    first_name = Column(String(100), nullable=False)
    last_name = Column(String(100), nullable=False)
    birth_date = Column(Date, nullable=True)
    gender = Column(Enum(Gender), nullable=True)
    allergies = Column(JSON, nullable=True, default=list)
    chronic_diseases = Column(JSON, nullable=True, default=list)

    user = relationship("User", back_populates="patient")
    doctors = relationship("Doctor", secondary=doctor_patient, back_populates="patients")
    prescriptions = relationship("Prescription", back_populates="patient")


class Medicine(Base):
    __tablename__ = "medicines"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(255), nullable=False, index=True)
    form = Column(Enum(MedicineForm), nullable=False)
    default_dosage = Column(String(100), nullable=True)

    prescriptions = relationship("Prescription", back_populates="medicine")


class Prescription(Base):
    __tablename__ = "prescriptions"

    id = Column(Integer, primary_key=True, index=True)
    doctor_id = Column(Integer, ForeignKey("doctors.id", ondelete="CASCADE"), nullable=False)
    patient_id = Column(Integer, ForeignKey("patients.id", ondelete="CASCADE"), nullable=False)
    medicine_id = Column(Integer, ForeignKey("medicines.id", ondelete="CASCADE"), nullable=False)
    dosage = Column(String(100), nullable=False)
    frequency = Column(Integer, nullable=False)
    duration_days = Column(Integer, nullable=False)
    start_date = Column(Date, nullable=False, default=date.today)
    end_date = Column(Date, nullable=False)
    status = Column(Enum(PrescriptionStatus), nullable=False, default=PrescriptionStatus.ACTIVE)
    notes = Column(Text, nullable=True)

    doctor = relationship("Doctor", back_populates="prescriptions")
    patient = relationship("Patient", back_populates="prescriptions")
    medicine = relationship("Medicine", back_populates="prescriptions")
    logs = relationship("PrescriptionLog", back_populates="prescription", cascade="all, delete-orphan")


class PrescriptionLog(Base):
    __tablename__ = "prescription_logs"

    id = Column(Integer, primary_key=True, index=True)
    prescription_id = Column(Integer, ForeignKey("prescriptions.id", ondelete="CASCADE"), nullable=False)
    taken_at = Column(DateTime, nullable=False, default=datetime.utcnow)

    prescription = relationship("Prescription", back_populates="logs")