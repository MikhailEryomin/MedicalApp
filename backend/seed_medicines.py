import asyncio
from sqlalchemy import select
from app.database import async_session_maker, engine
from app.models import Medicine, MedicineForm, Base


MEDICINES = [
    ("Амоксициллин", MedicineForm.TABLET, "500mg"),
    ("Ибупрофен", MedicineForm.TABLET, "200mg"),
    ("Парацетамол", MedicineForm.TABLET, "500mg"),
    ("Цефтриаксон", MedicineForm.INJECTION, "1g"),
    ("Амброксол", MedicineForm.SYRUP, "15mg/5ml"),
    ("Диклофенак", MedicineForm.OINTMENT, "1%"),
    ("Омепразол", MedicineForm.TABLET, "20mg"),
    ("Метформин", MedicineForm.TABLET, "850mg"),
    ("Лоратадин", MedicineForm.TABLET, "10mg"),
    ("Азитромицин", MedicineForm.TABLET, "250mg"),
    ("Ципрофлоксацин", MedicineForm.TABLET, "500mg"),
    ("Дексаметазон", MedicineForm.INJECTION, "4mg/ml"),
    ("Нурофен", MedicineForm.SYRUP, "100mg/5ml"),
    ("Левомеколь", MedicineForm.OINTMENT, "40mg/g"),
    ("Аторвастатин", MedicineForm.TABLET, "20mg"),
]


async def seed():
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    async with async_session_maker() as session:
        for name, form, dosage in MEDICINES:
            result = await session.execute(select(Medicine).where(Medicine.name == name))
            if result.scalar_one_or_none() is None:
                session.add(Medicine(name=name, form=form, default_dosage=dosage))
        await session.commit()
        print(f"Seeded {len(MEDICINES)} medicines.")


if __name__ == "__main__":
    asyncio.run(seed())