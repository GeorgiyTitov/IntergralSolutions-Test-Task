CREATE TABLE IF NOT EXISTS favorites (
    user_id UUID NOT NULL,
    movie_id BIGINT NOT NULL CHECK (movie_id > 0),
    title TEXT NOT NULL,
    poster_path TEXT,
    overview TEXT NOT NULL,
    release_date TEXT NOT NULL,
    rating DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, movie_id)
);
