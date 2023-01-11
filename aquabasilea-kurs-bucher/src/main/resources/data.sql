CREATE TABLE IF NOT EXISTS weeklycourses
(
    id      VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS coursedef
(
    id                VARCHAR(255) NOT NULL,
    course_date       timestamp(6) NOT NULL,
    course_instructor VARCHAR(255) NOT NULL,
    course_location   VARCHAR(255) NOT NULL,
    course_name       VARCHAR(255) NOT NULL,
    user_id           VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS course
(
    id                VARCHAR(255) NOT NULL,
    course_date       timestamp(6) NOT NULL,
    course_instructor VARCHAR(255) NOT NULL,
    course_location   VARCHAR(255) NOT NULL,
    course_name       VARCHAR(255) NOT NULL,
    has_course_def    boolean      NOT NULL,
    is_paused         boolean      NOT NULL,
    weeklycourses_id  VARCHAR(255),
    CONSTRAINT FK_COURSE_WEEKLYCOURSES FOREIGN KEY (weeklycourses_id) REFERENCES weeklycourses (id),
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

CREATE TABLE IF NOT EXISTS user_config_entity_course_locations
(
    user_config_entity_id VARCHAR(255) NOT NULL,
    course_locations      VARCHAR(255),
    CONSTRAINT FK_USERS_CONFIG_ENTITY_COURSE_LOCATIONS_USER_CONFIG FOREIGN KEY (user_config_entity_id) REFERENCES user_config (id)
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
