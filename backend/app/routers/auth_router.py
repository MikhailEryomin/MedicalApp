from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.database import get_db
from app.auth import verify_password, hash_password, create_access_token
from app.models import User, Doctor, Patient, UserRole, Gender, doctor_patient
from app.schemas import (
    LoginRequest, RegisterDoctorRequest, RegisterPatientByDoctorRequest, TokenResponse,
)
from app.dependencies import get_current_doctor

router = APIRouter(prefix="/api/auth", tags=["Auth"])


@router.post("/login", response_model=TokenResponse)
async def login(body: LoginRequest, db: AsyncSession = Depends(get_db)):
    result = await db.execute(
        select(User)
        .options(selectinload(User.doctor), selectinload(User.patient))
        .where(User.email == body.email)
    )
    user = result.scalar_one_or_none()

    if user is None or not verify_password(body.password, user.password_hash):
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")

    if user.role.value != body.role:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"This account is registered as {user.role.value}, not {body.role}",
        )

    token = create_access_token({"user_id": user.id, "role": user.role.value})

    profile_id = None
    if user.role == UserRole.DOCTOR and user.doctor:
        profile_id = user.doctor.id
    elif user.role == UserRole.PATIENT and user.patient:
        profile_id = user.patient.id

    return TokenResponse(
        token=token,
        user={"id": user.id, "role": user.role.value, "profile_id": profile_id},
    )


@router.post("/register/doctor", response_model=TokenResponse)
async def register_doctor(body: RegisterDoctorRequest, db: AsyncSession = Depends(get_db)):
    existing = await db.execute(select(User).where(User.email == body.email))
    if existing.scalar_one_or_none():
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail="Email already registered")

    user = User(
        email=body.email,
        password_hash=hash_password(body.password),
        role=UserRole.DOCTOR,
    )
    db.add(user)
    await db.flush()

    doctor = Doctor(
        user_id=user.id,
        first_name=body.first_name,
        last_name=body.last_name,
        specialization=body.specialization,
        licence_number=body.licence_number,
    )
    db.add(doctor)
    await db.flush()

    token = create_access_token({"user_id": user.id, "role": user.role.value})
    return TokenResponse(
        token=token,
        user={"id": user.id, "role": user.role.value, "profile_id": doctor.id},
    )


@router.post("/register", response_model=TokenResponse)
async def register_patient_by_doctor(
    body: RegisterPatientByDoctorRequest,
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

    token = create_access_token({"user_id": user.id, "role": user.role.value})
    return TokenResponse(
        token=token,
        user={"id": user.id, "role": user.role.value, "profile_id": patient.id},
    )