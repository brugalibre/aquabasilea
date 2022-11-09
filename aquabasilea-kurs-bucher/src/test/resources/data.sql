CREATE TABLE IF NOT EXISTS users
(
    id       varchar(255) not null,
    password varchar(255),
    phone_nr  varchar(255),
    username varchar(255),
    CONSTRAINT UNIQUE_USERNAME unique (username),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user_entity_roles
(
    user_entity_id varchar(255) not null,
    CONSTRAINT FK_USER_ENTITY_ROLES_TO_USER FOREIGN KEY (user_entity_id) REFERENCES users (id),
    roles          varchar(255)
);

CREATE TABLE IF NOT EXISTS coursedef
(
    id                VARCHAR(250) PRIMARY KEY,
    user_id           VARCHAR(250) NOT NULL,
    CONSTRAINT FK_COURSE_DEF_TO_USER FOREIGN KEY (user_id) REFERENCES users (id),
    course_name       VARCHAR(250) NOT NULL,
    course_instructor VARCHAR(250) NOT NULL,
    course_date       TIMESTAMP    NOT NULL,
    course_location   VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_config
(
    id      varchar(255) not null,
    user_id varchar(255) not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user_config_entity_course_locations
(
    user_config_entity_id varchar(255) not null,
    course_locations      varchar(255)
);

CREATE TABLE IF NOT EXISTS weeklycourses
(
    id          VARCHAR(250) PRIMARY KEY,
    user_id     VARCHAR(250) NOT NULL,
    CONSTRAINT  FK_WEEKLY_COURSES_TO_USER FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS course
(
    id                VARCHAR(250) PRIMARY KEY,
    weeklycourses_id  VARCHAR(250) NOT NULL,
    CONSTRAINT FK_WEEKLY_COURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses (id),
    course_name       VARCHAR(250) NOT NULL,
    course_instructor VARCHAR(250) NOT NULL,
    course_date       TIMESTAMP    NOT NULL,
    is_paused         BOOLEAN      NOT NULL DEFAULT FALSE,
    has_course_def    BOOLEAN      NOT NULL DEFAULT FALSE,
    course_location   VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS coursedef
(
    id              VARCHAR(250) PRIMARY KEY,
    user_id         VARCHAR(250) NOT NULL,
    CONSTRAINT      FK_COURSE_DEF_TO_USER FOREIGN KEY (user_id) REFERENCES users (id),
    course_name     VARCHAR(250) NOT NULL,
    course_date     TIMESTAMP    NOT NULL,
    course_location VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS statistics
(
    id                         VARCHAR(250) PRIMARY KEY,
    user_id                    VARCHAR(250) NOT NULL,
    CONSTRAINT FK_STATISTICS_TO_USER FOREIGN KEY (user_id) REFERENCES users (id),
    last_course_def_update     TIMESTAMP,
    next_course_def_update     TIMESTAMP,
    booking_failed_counter     INTEGER NOT NULL DEFAULT 0,
    booking_successful_counter INTEGER NOT NULL DEFAULT 0
);
