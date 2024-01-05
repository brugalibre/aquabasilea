CREATE TABLE IF NOT EXISTS weeklycourses
(
    id      VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS coursedef;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS user_config_entity_course_locations;
DROP TABLE IF EXISTS user_config;

CREATE TABLE IF NOT EXISTS courselocation
(
    center_id VARCHAR(255) NOT NULL,
    id        VARCHAR(255) NOT NULL,
    name      VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS coursedef
(
    id                VARCHAR(255) NOT NULL,
    course_date       timestamp(6) NOT NULL,
    course_instructor VARCHAR(255) NOT NULL,
    courselocation_id VARCHAR(255) NOT NULL,
    course_name       VARCHAR(255) NOT NULL,
    user_id           VARCHAR(255) NOT NULL,
    CONSTRAINT FK_COURSE_DEF_COURSELOCATION FOREIGN KEY (courselocation_id) REFERENCES courselocation (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS course
(
    id                  VARCHAR(255) NOT NULL,
    course_date         timestamp(6) NOT NULL,
    course_instructor   VARCHAR(255) NOT NULL,
    courselocation_id   VARCHAR(255) NOT NULL,
    course_name         VARCHAR(255) NOT NULL,
    has_course_def      boolean      NOT NULL,
    is_paused           boolean      NOT NULL,
    weeklycourses_id    VARCHAR(255),
    CONSTRAINT FK_COURSE_WEEKLYCOURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses (id),
    CONSTRAINT FK_COURSE_COURSELOCATION FOREIGN KEY (courselocation_id) REFERENCES courselocation (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS statistics
(
    id                         VARCHAR(255) NOT NULL,
    booking_failed_counter     integer      NOT NULL,
    booking_successful_counter integer      NOT NULL,
    last_course_def_update     timestamp(6),
    next_course_def_update     timestamp(6),
    user_id                    VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_config
(
    id      VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_config_course_locations
(
    user_config_entity_id VARCHAR(255) NOT NULL,
    course_locations_id   VARCHAR(255) NOT NULL,
    CONSTRAINT FK_USER_CONFIG_CL_COURSE_COURSELOCATION FOREIGN KEY (course_locations_id) REFERENCES courselocation (id),
    CONSTRAINT FK_USER_CONFIG_CL_USER_CONFIG FOREIGN KEY (user_config_entity_id) REFERENCES user_config (id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    username VARCHAR(255),
    phone_nr varchar(255),
    PRIMARY KEY (id),
    CONSTRAINT UNIQUE_USERNAME unique (username)
);

CREATE TABLE IF NOT EXISTS user_entity_roles
(
    user_entity_id VARCHAR(255) NOT NULL,
    roles          VARCHAR(255),
    CONSTRAINT FK_USER_ENTITY_ROLES_USERS FOREIGN KEY (user_entity_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS contact_point_entity
(
    dm_contact_point_type VARCHAR(31)  NOT NULL,
    id                    VARCHAR(255) NOT NULL,
    contact_point_type    VARCHAR(255),
    phone_nr              VARCHAR(255),
    user_id               VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT FK_CONTACT_POINT_ENTITY_USERS FOREIGN KEY (user_id) REFERENCES users (id)
);
