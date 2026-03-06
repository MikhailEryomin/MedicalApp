from fastapi import APIRouter, Depends, Query
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.database import get_db
from app.models import Medicine
from app.schemas import MedicineOut
from app.dependencies import get_current_user

router = APIRouter(prefix="/api/medicines", tags=["Medicines"])


@router.get("", response_model=list[MedicineOut])
async def search_medicines(
    search: str = Query(default="", description="Search by name"),
    _=Depends(get_current_user),
    db: AsyncSession = Depends(get_db),
):
    query = select(Medicine)
    if search:
        query = query.where(Medicine.name.ilike(f"%{search}%"))
    query = query.order_by(Medicine.name).limit(50)
    result = await db.execute(query)
    medicines = result.scalars().all()
    return [
        MedicineOut(
            id=m.id,
            name=m.name,
            form=m.form.value,
            default_dosage=m.default_dosage,
        )
        for m in medicines
    ]