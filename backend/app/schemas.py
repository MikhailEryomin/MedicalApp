from datetime import date, datetime
from typing import Optional
from pydantic import BaseModel, EmailStr


class LoginRequest(BaseModel):
    email: EmailStr
    password: str
    role: str


class RegisterDoctorRequest(BaseModel):
    email: EmailStr
    password: str
    first_name: str
    last_name: str
    specialization: Optional[str] = None
    licence_number: Optional[str] = None


class RegisterPatientByDoctorRequest(BaseModel):
    email: EmailStr
    password: str
    first_name: str
    last_name: str
    birth_date: Optional[date] = None
    gender: Optional[str] = None
    allergies: Optional[list[str]] = []
    chronic_diseases: Optional[list[str]] = []


class TokenResponse(BaseModel):
    token: str
    user: dict


class DoctorProfile(BaseModel):
    id: int
    user_id: int
    first_name: str
    last_name: str
    specialization: Optional[str] = None
    licence_number: Optional[str] = None
    role: str = "DOCTOR"

    class Config:
        from_attributes = True


class PatientProfile(BaseModel):
    id: int
    user_id: int
    first_name: str
    last_name: str
    birth_date: Optional[date] = None
    gender: Optional[str] = None
    allergies: Optional[list[str]] = []
    chronic_diseases: Optional[list[str]] = []
    role: str = "PATIENT"

    class Config:
        from_attributes = True


class PatientListItem(BaseModel):
    id: int
    first_name: str
    last_name: str
    birth_date: Optional[date] = None
    gender: Optional[str] = None

    class Config:
        from_attributes = True


class PatientDetail(BaseModel):
    id: int
    first_name: str
    last_name: str
    birth_date: Optional[date] = None
    gender: Optional[str] = None
    allergies: Optional[list[str]] = []
    chronic_diseases: Optional[list[str]] = []

    class Config:
        from_attributes = True


class CreatePatientRequest(BaseModel):
    email: EmailStr
    password: str
    first_name: str
    last_name: str
    birth_date: Optional[date] = None
    gender: Optional[str] = None
    allergies: Optional[list[str]] = []
    chronic_diseases: Optional[list[str]] = []


class MedicineOut(BaseModel):
    id: int
    name: str
    form: str
    default_dosage: Optional[str] = None

    class Config:
        from_attributes = True


class CreatePrescriptionRequest(BaseModel):
    patient_id: int
    medicine_id: int
    dosage: str
    frequency: int
    duration_days: int
    notes: Optional[str] = None


class PrescriptionOut(BaseModel):
    id: int
    doctor_id: int
    patient_id: int
    medicine_id: int
    medicine_name: Optional[str] = None
    dosage: str
    frequency: int
    duration_days: int
    start_date: date
    end_date: date
    status: str
    notes: Optional[str] = None
    taken_count: int = 0
    total_count: int = 0

    class Config:
        from_attributes = True


class PrescriptionLogOut(BaseModel):
    id: int
    prescription_id: int
    taken_at: datetime

    class Config:
        from_attributes = True


class TakenCountOut(BaseModel):
    prescription_id: int
    taken_count: int
    total_count: int
    completed: bool