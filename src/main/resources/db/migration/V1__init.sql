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
                                            user_id INTEGER NOT NULL ,
                                            title varchar(255) NOT NULL ,
                                            content text NOT NULL ,
                                            created timestamp NOT NULL  DEFAULT current_timestamp,
                                            updated timestamp NOT NULL  DEFAULT current_timestamp,
                                            deleted BOOLEAN NOT NULL DEFAULT false,
                                            likes integer NOT NULL default 0,
                                            created_by varchar(50),
                                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ,
                                            unique (title)
);


CREATE TABLE roles (
                                            id SERIAL PRIMARY KEY ,
                                            name varchar(50) NOT NULL,
                                            user_system_role VARCHAR(64) NOT NULL,
                                            active BOOLEAN NOT NULL DEFAULT true,
                                            created_by varchar(50) NOT NULL

);


CREATE TABLE users_roles(
                                            user_id BIGINT NOT NULL ,
                                            role_id INT NOT NULL ,
                                            PRIMARY KEY (user_id, role_id),
                                            FOREIGN KEY (user_id) REFERENCES users (id),
                                            FOREIGN KEY (role_id) REFERENCES roles (id)
);




INSERT INTO users(username, password, email, created, updated, registration_status, last_login, deleted ) VALUES
                                                                                                              ('super_admin', '$2a$10$d.t5MzHeNA1T14klDlQHLuhXhQb2Imlf5O1BjRO6r6hG3N9x.osjq', 'superadmin@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false ),
                                                                                                              ('admin', '2a$10$0NuQG7SpF4Bsom/DBB/PEulzIWjvFtnokP0SoqF90tpE6J72WGOe.', 'admin@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false ),
                                                                                                              ('user', '$2a$10$wGjV6LbN2esPF2ILflWwB.oOVtXEFha2t/rU482m9AaEyO4FxfplC', 'user@email.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ACTIVE', CURRENT_TIMESTAMP, false );




INSERT INTO posts(user_id, title, content, created, updated, deleted,  likes) VALUES
                                                      (1,'First Post', 'This is content of the first post', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, false,  10),
                                                      (2,'Second Post', 'This is content of the second post', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, false,  3);


INSERT INTO roles(name, user_system_role, created_by) VALUES
                              ('SUPER_ADMIN', 'SUPER_ADMIN','SUPER_ADMIN'),
                              ('ADMIN', 'ADMIN','SUPER_ADMIN'),
                              ('USER', 'USER','SUPER_ADMIN');

INSERT INTO users_roles (user_id, role_id) VALUES
                        (1,1),
                        (2,2),
                        (3,3);