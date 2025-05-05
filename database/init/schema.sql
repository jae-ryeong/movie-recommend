DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS ratings;


CREATE TABLE movies (
                        movie_id BIGINT PRIMARY KEY,
                        title VARCHAR(255),
                        genres VARCHAR(255)
);

CREATE TABLE ratings (
                         rating_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT,
                         movie_id BIGINT,
                         rating FLOAT,
                         timestamp BIGINT
);