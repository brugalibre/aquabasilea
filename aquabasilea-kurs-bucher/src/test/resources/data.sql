CREATE TABLE IF NOT EXISTS weeklycourses (
    id VARCHAR(250) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS course (
    id VARCHAR(250) PRIMARY KEY,
    weeklycourses_id VARCHAR(250) NOT NULL,
    CONSTRAINT FK_WEEKLY_COURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses(id),
    course_name VARCHAR(250) NOT NULL,
    day_of_week VARCHAR(250) NOT NULL,
    time_of_the_day VARCHAR(250) NOT NULL,
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    has_course_def BOOLEAN NOT NULL DEFAULT FALSE,
    course_location VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS coursedef (
    id VARCHAR(250) PRIMARY KEY,
    course_name VARCHAR(250) NOT NULL,
    day_of_week VARCHAR(250) NOT NULL,
    course_date DATE NOT NULL,
    course_location VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS statistics (
    id VARCHAR(250) PRIMARY KEY,
    last_course_def_update TIMESTAMP,
    next_course_def_update TIMESTAMP,
    booking_failed_counter INTEGER NOT NULL DEFAULT 0,
    booking_successful_counter INTEGER NOT NULL DEFAULT 0
);

ALTER TABLE coursedef
    ADD COLUMN IF NOT EXISTS course_date DATE DEFAULT CURRENT_DATE();