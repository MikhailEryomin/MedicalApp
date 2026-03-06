from datetime import date, datetime, timedelta

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.orm import selectinload

from app.database import get_db
from app.models import (
    User, Doctor, Patient, Prescription, PrescriptionLog,
    PrescriptionStatus, Medicine, UserRole, doctor_patient,
)
from app.schemas import (
    CreatePrescriptionRequest, PrescriptionOut, PrescriptionLogOut, TakenCountOut,
)
from app.dependencies import get_current_user, get_current_doctor

router = APIRouter(prefix="/api", tags=["Prescriptions"])


async def _check_doctor_patient_link(db: AsyncSession, doctor_id: int, patient_id: int):
    result = await db.execute(
        select(doctor_patient).where(
            doctor_patient.c.doctor_id == doctor_id,
            doctor_patient.c.patient_id == patient_id,
        )
    )
    if result.first() is None:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Patient is not assigned to you",
        )


async def _get_taken_count(db: AsyncSession, prescription_id: int) -> int:
    result = await db.execute(
        select(func.count(PrescriptionLog.id))
        .where(PrescriptionLog.prescription_id == prescription_id)
    )
    return result.scalar() or 0


async def _prescription_to_out(db: AsyncSession, p: Prescription) -> PrescriptionOut:
    taken_count = await _get_taken_count(db, p.id)
    total_count = p.frequency * p.duration_days

    medicine_name = None
    if p.medicine:
        medicine_name = p.medicine.name
    else:
        med = await db.get(Medicine, p.medicine_id)
        medicine_name = med.name if med else None

    return PrescriptionOut(
        id=p.id,
        doctor_id=p.doctor_id,
        patient_id=p.patient_id,
        medicine_id=p.medicine_id,
        medicine_name=medicine_name,
        dosage=p.dosage,
        frequency=p.frequency,
        duration_days=p.duration_days,
        start_date=p.start_date,
        end_date=p.end_date,
        status=p.status.value,
        notes=p.notes,
        taken_count=taken_count,
        total_count=total_count,
    )


@router.post("/prescriptions", response_model=PrescriptionOut, status_code=201)
async def create_prescription(
    body: CreatePrescriptionRequest,
    doctor: Doctor = Depends(get_current_doctor),
    db: AsyncSession = Depends(get_db),
):
    await _check_doctor_patient_link(db, doctor.id, body.patient_id)

    medicine = await db.get(Medicine, body.medicine_id)
    if medicine is None:
        raise HTTPException(status_code=404, detail="Medicine not found")

    patient = await db.get(Patient, body.patient_id)
    if patient is None:
        raise HTTPException(status_code=404, detail="Patient not found")

    today = date.today()
    end = today + timedelta(days=body.duration_days)

    prescription = Prescription(
        doctor_id=doctor.id,
        patient_id=body.patient_id,
        medicine_id=body.medicine_id,
        dosage=body.dosage,
        frequency=body.frequency,
        duration_days=body.duration_days,
        start_date=today,
        end_date=end,
        status=PrescriptionStatus.ACTIVE,
        notes=body.notes,
    )
    db.add(prescription)
    await db.flush()

    prescription.medicine = medicine
    return await _prescription_to_out(db, prescription)


@router.get("/patients/{patient_id}/prescriptions", response_model=list[PrescriptionOut])
async def get_patient_prescriptions(
    patient_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    if current_user.role == UserRole.PATIENT:
        if current_user.patient is None or current_user.patient.id != patient_id:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    elif current_user.role == UserRole.DOCTOR:
        if current_user.doctor is None:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
        await _check_doctor_patient_link(db, current_user.doctor.id, patient_id)

    result = await db.execute(
        select(Prescription)
        .options(selectinload(Prescription.medicine))
        .where(Prescription.patient_id == patient_id)
        .order_by(Prescription.start_date.desc())
    )
    prescriptions = result.scalars().all()

    output = []
    for p in prescriptions:
        output.append(await _prescription_to_out(db, p))
    return output


@router.post("/prescriptions/{prescription_id}/log", response_model=PrescriptionLogOut, status_code=201)
async def mark_as_taken(
    prescription_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    prescription = await db.get(Prescription, prescription_id)
    if prescription is None:
        raise HTTPException(status_code=404, detail="Prescription not found")

    if current_user.role == UserRole.PATIENT:
        if current_user.patient is None or current_user.patient.id != prescription.patient_id:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    elif current_user.role == UserRole.DOCTOR:
        if current_user.doctor is None:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
        await _check_doctor_patient_link(db, current_user.doctor.id, prescription.patient_id)
    else:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")

    if prescription.status != PrescriptionStatus.ACTIVE:
        raise HTTPException(status_code=400, detail="Prescription is not active")

    log = PrescriptionLog(
        prescription_id=prescription_id,
        taken_at=datetime.utcnow(),
    )
    db.add(log)
    await db.flush()

    total_needed = prescription.frequency * prescription.duration_days
    taken_count = await _get_taken_count(db, prescription_id)

    if taken_count >= total_needed:
        prescription.status = PrescriptionStatus.COMPLETED
        await db.flush()

    return PrescriptionLogOut(
        id=log.id,
        prescription_id=log.prescription_id,
        taken_at=log.taken_at,
    )


@router.get("/prescriptions/{prescription_id}/taken-count", response_model=TakenCountOut)
async def get_taken_count(
    prescription_id: int,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    prescription = await db.get(Prescription, prescription_id)
    if prescription is None:
        raise HTTPException(status_code=404, detail="Prescription not found")

    if current_user.role == UserRole.PATIENT:
        if current_user.patient is None or current_user.patient.id != prescription.patient_id:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
    elif current_user.role == UserRole.DOCTOR:
        if current_user.doctor is None:
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied")
        await _check_doctor_patient_link(db, current_user.doctor.id, prescription.patient_id)

    total_needed = prescription.frequency * prescription.duration_days
    taken = await _get_taken_count(db, prescription_id)

    return TakenCountOut(
        prescription_id=prescription_id,
        taken_count=taken,
        total_count=total_needed,
        completed=taken >= total_needed,
    )