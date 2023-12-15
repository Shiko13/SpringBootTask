INSERT INTO users (first_name, last_name, username, password, is_active, postfix)
VALUES
    ('John', 'Doe', 'john.doe', 'password1', true, 0),
    ('Jane', 'Smith', 'jane.smith', 'password2', true, 0),
    ('Alice', 'Johnson', 'alice.johnson', 'password3', true, 0),
    ('Bob', 'Brown', 'bob.brown', 'password4', true, 0),
    ('Eva', 'Davis', 'eva.davis', 'password5', true, 0),
    ('Michael', 'Wilson', 'michael.wilson', 'password6', true, 0),
    ('Olivia', 'Lee', 'olivia.lee', 'password7', true, 0),
    ('Daniel', 'Taylor', 'daniel.taylor', 'password8', true, 0),
    ('Sophia', 'Moore', 'sophia.moore', 'password9', true, 0),
    ('James', 'Johnson', 'james.johnson', 'password10', true, 0),
    ('Emily', 'White', 'emily.white', 'password11', true, 0),
    ('William', 'Anderson', 'william.anderson', 'password12', true, 0),
    ('Charlotte', 'Martin', 'charlotte.martin', 'password13', true, 0),
    ('Benjamin', 'Thompson', 'benjamin.thompson', 'password14', true, 0),
    ('Amelia', 'Harris', 'amelia.harris', 'password15', true, 0),
    ('Henry', 'Nelson', 'henry.nelson', 'password16', true, 0),
    ('Sofia', 'King', 'sofia.king', 'password17', true, 0),
    ('Alexander', 'Hall', 'alexander.hall', 'password18', true, 0),
    ('Mia', 'Wright', 'mia.wright', 'password19', true, 0),
    ('Joseph', 'Adams', 'joseph.adams', 'password20', true, 0);

INSERT INTO training_types (name) VALUES
                                      ('Strength Training'),
                                      ('Cardiovascular Exercise'),
                                      ('Yoga'),
                                      ('CrossFit'),
                                      ('Pilates'),
                                      ('Martial Arts'),
                                      ('Zumba'),
                                      ('Spinning'),
                                      ('Swimming'),
                                      ('Plyometrics');

INSERT INTO trainees (date_of_birth, address, id) VALUES
    ('1995-03-15', '123 Main St', 1),
    ('1992-08-20', '456 Elm St', 2),
    ('1990-04-10', '789 Oak St', 3),
    ('1998-12-05', '555 Pine St', 4),
    ('1994-07-28', '777 Birch St', 5),
    ('1997-09-14', '888 Cedar St', 6),
    ('1999-06-09', '999 Redwood St', 7),
    ('1993-02-25', '111 Maple St', 8),
    ('1996-11-03', '222 Willow St', 9),
    ('1991-01-01', '333 Birch St', 10);

INSERT INTO trainers (specialization, id) VALUES
        (1, 11),
        (2, 12),
        (3, 13),
        (4, 14),
        (5, 15),
        (6, 16),
        (7, 17),
        (8, 18),
        (9, 19),
        (10, 20);

INSERT INTO trainings (trainee_id, trainer_id, name, type_id, date, duration)
VALUES
    (1, 11, 'Training 1', 1, '2023-11-01', 60),
    (2, 12, 'Training 2', 2, '2023-11-02', 45),
    (3, 13, 'Training 3', 3, '2023-11-03', 90),
    (4, 14, 'Training 4', 4, '2023-11-04', 75),
    (5, 15, 'Training 5', 5, '2023-11-05', 60),
    (6, 16, 'Training 6', 6, '2023-11-06', 45),
    (7, 17, 'Training 7', 7, '2023-11-07', 90),
    (8, 18, 'Training 8', 8, '2023-11-08', 75),
    (9, 19, 'Training 9', 9, '2023-11-09', 60),
    (10, 20, 'Training 10', 10, '2023-11-10', 45);

INSERT INTO trainee_trainer (trainee_id, trainer_id) VALUES
      (1, 11),
      (1, 12),
      (2, 11),
      (3, 12),
      (4, 20),
      (5, 19),
      (4, 17),
      (6, 18),
      (7, 16),
      (8, 15),
      (9, 13),
      (10, 14),
      (1, 15),
      (2, 17),
      (5, 18),
      (3, 15),
      (3, 18)
;