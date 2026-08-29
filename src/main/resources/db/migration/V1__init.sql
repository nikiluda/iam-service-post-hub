CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY ,
    username varchar(30) NOT NULL UNIQUE,
    password varchar(80) NOT NULL,
    email varchar(50) UNIQUE,
    created timestamp NOT NULL  DEFAULT current_timestamp,
    updated timestamp NOT NULL  DEFAULT current_timestamp,
    registration_status varchar(50) NOT NULL ,
    last_login timestamp,
    deleted boolean NOT NULL DEFAULT false
);





CREATE TABLE posts(
                                            id BIGSERIAL PRIMARY KEY ,
                                            title varchar(255) NOT NULL ,
                                            content text NOT NULL ,
                                            created timestamp NOT NULL  DEFAULT current_timestamp,
                                            updated timestamp NOT NULL  DEFAULT current_timestamp,
                                            deleted BOOLEAN NOT NULL DEFAULT false,
                                            likes integer NOT NULL default 0,
                                            unique (title)
);
INSERT INTO users(username, password, email, created, updated, registration_status, last_login, deleted ) VALUES
                                                                                                              ('first_username', 'password1', 'first_user@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false ),
                                                                                                              ('second_username', 'password2', 'second_user@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false ),
                                                                                                              ('third_username', 'password3', 'third_user@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false );




INSERT INTO posts(title, content, created, updated, deleted,  likes) VALUES
                                                      ('First Post', 'This is content of the first post', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, false,  10),
                                                      ('Second Post', 'This is content of the second post', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, false,  3);