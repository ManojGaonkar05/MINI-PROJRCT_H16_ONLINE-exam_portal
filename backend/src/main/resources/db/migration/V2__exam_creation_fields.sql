ALTER TABLE exams
    ADD COLUMN subject VARCHAR(120) NOT NULL DEFAULT 'General',
    ADD COLUMN total_marks NUMERIC(10,2) NOT NULL DEFAULT 100.00,
    ADD COLUMN passing_marks NUMERIC(10,2) NOT NULL DEFAULT 40.00;

ALTER TABLE exams
    ADD CONSTRAINT ck_exams_duration_fr06 CHECK (duration_minutes BETWEEN 5 AND 240),
    ADD CONSTRAINT ck_exams_total_marks CHECK (total_marks > 0),
    ADD CONSTRAINT ck_exams_passing_marks CHECK (passing_marks >= 0 AND passing_marks <= total_marks);
