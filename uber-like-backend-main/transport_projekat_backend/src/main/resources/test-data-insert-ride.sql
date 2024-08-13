INSERT INTO VEHICLE_TYPES (NAME, PRICE_PER_KM) VALUES
                                                   ('STANDARD', 150),
                                                   ('LUXURY', 250),
                                                   ('VAN', 200);

INSERT INTO USERS (ADDRESS,EMAIL,IS_ACTIVATED,IS_BLOCKED,NAME,PASSWORD,SURNAME,TELEPHONE_NUMBER,PROFILE_PICTURE) VALUES
('Abc','driver5@mail.com',true,false,'Test','Test1Test','Testing','06577869482','');

INSERT INTO RIDES(START_TIME, END_TIME, STATUS, TOTAL_PRICE,DRIVER_ID,VEHICLE_TYPE, TRANSPORTS_PET, TRANSPORTS_BABY)
VALUES ('2023-01-30 09:35:22', '2023-01-30 09:35:23', 'FINISHED', 1200,4,1, true, true);