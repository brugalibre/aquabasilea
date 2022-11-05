CREATE TABLE IF NOT EXISTS weeklycourses (
    id VARCHAR(250) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS course (
    id VARCHAR(250) PRIMARY KEY,
    weeklycourses_id VARCHAR(250) NOT NULL,
    CONSTRAINT FK_WEEKLY_COURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses(id),
    course_name VARCHAR(250) NOT NULL,
    course_instructor VARCHAR(250) NOT NULL,
    course_date TIMESTAMP NOT NULL,
    is_paused BOOLEAN NOT NULL DEFAULT FALSE,
    has_course_def BOOLEAN NOT NULL DEFAULT FALSE,
    course_location VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS coursedef (
    id VARCHAR(250) PRIMARY KEY,
    course_name VARCHAR(250) NOT NULL,
    course_instructor VARCHAR(250) NOT NULL,
    course_date TIMESTAMP NOT NULL,
    course_location VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS statistics (
    id VARCHAR(250) PRIMARY KEY,
    last_course_def_update TIMESTAMP,
    next_course_def_update TIMESTAMP,
    booking_failed_counter INTEGER NOT NULL DEFAULT 0,
    booking_successful_counter INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS user_entity_roles
(
    user_entity_id varchar(255) not null,
    roles          varchar(255)
);

CREATE TABLE IF NOT EXISTS users
(
    id       varchar(255) not null,
    password varchar(255),
    username varchar(255),
    primary key (id)
);

alter table users
    add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);
alter table user_entity_roles
    add constraint FK80w28k99mayei90r6mycds2em foreign key (user_entity_id) references users;