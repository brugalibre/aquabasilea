CREATE TABLE IF NOT EXISTS weeklycourses (
    id VARCHAR(250) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS course (
    id VARCHAR(250) PRIMARY KEY,
    weeklycourses_id VARCHAR(250),
    CONSTRAINT FK_WEEKLY_COURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses(id),
    course_name VARCHAR(250) NOT NULL,
    day_of_week VARCHAR(250) NOT NULL,
    time_of_the_day VARCHAR(250) NOT NULL,
    is_paused BOOLEAN DEFAULT FALSE
);
