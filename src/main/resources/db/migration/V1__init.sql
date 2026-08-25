CREATE TABLE posts(
                                            id BIGSERIAL PRIMARY KEY ,
                                            title varchar(255) NOT NULL ,
                                            content text NOT NULL ,
                                            created timestamp NOT NULL  DEFAULT current_timestamp,
                                            likes integer NOT NULL default 0,
                                            unique (title)
);

INSERT INTO posts(title, content, created, likes) VALUES
                                                      ('First Post', 'This is content of the first post', CURRENT_TIMESTAMP, 10),
                                                      ('Second Post', 'This is content of the second post', CURRENT_TIMESTAMP, 3);