INSERT INTO medicines (name, form, default_dosage) VALUES
                                                       ('Амоксициллин', 'TABLET', '500mg'),
                                                       ('Ибупрофен', 'TABLET', '200mg'),
                                                       ('Парацетамол', 'TABLET', '500mg'),
                                                       ('Цефтриаксон', 'INJECTION', '1g'),
                                                       ('Амброксол', 'SYRUP', '15mg/5ml'),
                                                       ('Диклофенак', 'OINTMENT', '1%'),
                                                       ('Омепразол', 'TABLET', '20mg'),
                                                       ('Метформин', 'TABLET', '850mg'),
                                                       ('Лоратадин', 'TABLET', '10mg'),
                                                       ('Азитромицин', 'TABLET', '250mg'),
                                                       ('Ципрофлоксацин', 'TABLET', '500mg'),
                                                       ('Дексаметазон', 'INJECTION', '4mg/ml'),
                                                       ('Нурофен', 'SYRUP', '100mg/5ml'),
                                                       ('Левомеколь', 'OINTMENT', '40mg/g'),
                                                       ('Аторвастатин', 'TABLET', '20mg')
    ON CONFLICT DO NOTHING;