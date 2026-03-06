from fastapi import FastAPI
from app.routers import (
    auth_router,
    users_router,
    patients_router,
    medicines_router,
    prescriptions_router,
)

app = FastAPI(
    title="MedicalApp API",
    description="Backend API for MedicalApp",
    version="1.0.0",
)

app.include_router(auth_router.router)
app.include_router(users_router.router)
app.include_router(patients_router.router)
app.include_router(medicines_router.router)
app.include_router(prescriptions_router.router)


@app.get("/", tags=["Health"])
async def root():
    return {"status": "ok", "message": "MedicalApp API is running"}