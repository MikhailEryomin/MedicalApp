ALTER TABLE patients
    ALTER COLUMN allergies DROP DEFAULT;

ALTER TABLE patients
ALTER COLUMN allergies TYPE TEXT USING allergies::text;

ALTER TABLE patients
    ALTER COLUMN allergies SET DEFAULT '[]';

ALTER TABLE patients
    ALTER COLUMN chronic_diseases DROP DEFAULT;

ALTER TABLE patients
ALTER COLUMN chronic_diseases TYPE TEXT USING chronic_diseases::text;

ALTER TABLE patients
    ALTER COLUMN chronic_diseases SET DEFAULT '[]';

ALTER TABLE doctors
ALTER COLUMN licence_number TYPE TEXT;