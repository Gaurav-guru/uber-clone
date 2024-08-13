INSERT INTO RIDES(ID, START_TIME, END_TIME, STATUS, TOTAL_PRICE,DRIVER_ID,VEHICLE_TYPE, TRANSPORTS_PET, TRANSPORTS_BABY)
VALUES (1, '2023-01-30 09:35:22', '2023-01-30 09:35:23', 'FINISHED', 1100,4,1, true, true),
       (2, '2023-01-30 09:36:22', '2023-01-30 09:36:23', 'FINISHED', 1000,4,2, false, false),
       (3, '2023-01-30 09:37:22', '2023-01-30 09:37:23', 'FINISHED', 1500,4,3, true, false),
       (4, '2023-02-01 09:35:22', '2023-02-01 09:35:23', 'FINISHED', 2000,4,2, false, true),
       (5, '2023-02-02 09:35:22', '2023-02-02 09:35:23', 'FINISHED', 500,4,3, true, true),
       (6, '2023-02-02 09:36:22', '2023-02-02 09:36:23', 'FINISHED', 1000,4,1, false, false),
       (7, '2023-02-02 09:37:22', '2023-02-02 09:37:23', 'FINISHED', 1500,4,1, false, false);

INSERT INTO RIDES_PASSENGERS(RIDE_ID, PASSENGERS_ID) VALUES
                                                         (1, 1),
                                                         (2, 1),
                                                         (3, 1),
                                                         (4, 1),
                                                         (5, 1),
                                                         (6, 1),
                                                         (7, 1);

INSERT INTO RIDE_ROUTE(RIDE_ID, ROUTE_ID) VALUES
                                              (1, 1),
                                              (2, 2),
                                              (3, 3),
                                              (4, 4),
                                              (5, 1),
                                              (6, 2),
                                              (7, 3);