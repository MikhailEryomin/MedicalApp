from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.auth import hash_password
from app.models import User, Doctor, Patient, UserRole, Gender, doctor_patient
from app.schemas import PatientListItem, PatientDetail, CreatePatientRequest
from app.dependencies import get_current_doctor, get_current_user

router = APIRouter(tags=["Patients"])


@router.get("/api/doctors/me/patients", response_model=list[PatientListItem])
async def get_my_patients(
    doctor: Doctor = Depends(get_current_doctor),
    db: AsyncSession = Depends(get_db),
):
    result = await db.execute(
        select(Patient)
        .join(doctor_patient, doctor_patient.c.patient_id == Patient.id)
        .where(doctor_patient.c.doctor_id == doctor.id)
    )
    patients = result.scalars().all()
    return [
        PatientListItem(
            id=p.id,
            first_name=p.first_name,
            last_name=p.last_name,
            birth_date=p.birth_date,
            gender=p.gender.value if p.gender else None,
        )
        for p in patients
    ]


@router.get("/api/patients/{patient_id}", response_model=PatientDetail)
async def get_patient_detail(
    patient_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    patient = await db.get(Patient, patient_id)
    if patient is None:
        raise HTTPException(status_code=404, detail="Patient not found")

    if current_user.role == UserRole.PATIENT:
        if current_user.patient is None or current_user.patient.id != patient_id:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    elif current_user.role == UserRole.DOCTOR:
        if current_user.doctor is None:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
        link = await db.execute(
            select(doctor_patient).where(
                doctor_patient.c.doctor_id == current_user.doctor.id,
                doctor_patient.c.patient_id == patient_id,
            )
        )
        if link.first() is None:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Patient is not assigned to you")

    return PatientDetail(
        id=patient.id,
        first_name=patient.first_name,
        last_name=patient.last_name,
        birth_date=patient.birth_date,
        gender=patient.gender.value if patient.gender else None,
        allergies=patient.allergies or [],
        chronic_diseases=patient.chronic_diseases or [],
    )


@router.post("/api/patients", response_model=PatientDetail, status_code=201)
async def create_patient(
    body: CreatePatientRequest,
    doctor: Doctor = Depends(get_current_doctor),
    db: AsyncSession = Depends(get_db),
):
    existing = await db.execute(select(User).where(User.email == body.email))
    if existing.scalar_one_or_none():
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email already registered")

    gender_enum = None
    if body.gender:
        try:
            gender_enum = Gender(body.gender)
        except ValueError:
            raise HTTPException(status_code=400, detail="Invalid gender. Use MALE or FEMALE.")

    user = User(
        email=body.email,
        password_hash=hash_password(body.password),
        role=UserRole.PATIENT,
    )
    db.add(user)
    await db.flush()

    patient = Patient(
        user_id=user.id,
        first_name=body.first_name,
        last_name=body.last_name,
        birth_date=body.birth_date,
        gender=gender_enum,
        allergies=body.allergies or [],
        chronic_diseases=body.chronic_diseases or [],
    )
    db.add(patient)
    await db.flush()

    await db.execute(doctor_patient.insert().values(doctor_id=doctor.id, patient_id=patient.id))

    return PatientDetail(
        id=patient.id,
        first_name=patient.first_name,
        last_name=patient.last_name,
        birth_date=patient.birth_date,
        gender=patient.gender.value if patient.gender else None,
        allergies=patient.allergies or [],
        chronic_diseases=patient.chronic_diseases or [],
    )