from fastapi import APIRouter, Depends

from app.models import User, UserRole
from app.schemas import DoctorProfile, PatientProfile
from app.dependencies import get_current_user

router = APIRouter(prefix="/api/users", tags=["Users"])


@router.get("/me")
async def get_my_profile(current_user: User = Depends(get_current_user)):
    if current_user.role == UserRole.DOCTOR and current_user.doctor:
        d = current_user.doctor
        return DoctorProfile(
            id=d.id,
            user_id=d.user_id,
            first_name=d.first_name,
            last_name=d.last_name,
            specialization=d.specialization,
            licence_number=d.licence_number,
            role="DOCTOR",
        )
    elif current_user.role == UserRole.PATIENT and current_user.patient:
        p = current_user.patient
        return PatientProfile(
            id=p.id,
            user_id=p.user_id,
            first_name=p.first_name,
            last_name=p.last_name,
            birth_date=p.birth_date,
            gender=p.gender.value if p.gender else None,
            allergies=p.allergies or [],
            chronic_diseases=p.chronic_diseases or [],
            role="PATIENT",
        )
    return {"detail": "Profile not configured"}