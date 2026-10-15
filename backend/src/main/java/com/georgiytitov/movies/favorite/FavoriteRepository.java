package com.georgiytitov.movies.favorite;

import com.georgiytitov.movies.movie.MovieDto;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class FavoriteRepository {
    private static final String SELECT_FAVORITES = """
        SELECT * FROM favorites
        WHERE user_id = :user
        ORDER BY created_at DESC, movie_id
        """;
    private static final String INSERT_FAVORITE = """
        INSERT INTO favorites (
            user_id, movie_id, title, poster_path, overview, release_date, rating
        ) VALUES (:user, :movie, :title, :poster, :overview, :release, :rating)
        """;
    private static final String DELETE_FAVORITE = """
        DELETE FROM favorites WHERE user_id = :user AND movie_id = :movie
        """;

    private final JdbcClient jdbc;

    public FavoriteRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<MovieDto> findAll(UUID userId) {
        return jdbc.sql(SELECT_FAVORITES).param("user", userId)
            .query(
                (rs, row) ->
                    new MovieDto(
                        rs.getLong("movie_id"),
                        rs.getString("title"),
                        rs.getString("poster_path"),
                        rs.getString("overview"),
                        rs.getString("release_date"),
                        rs.getDouble("rating")
                    ))
            .list();
    }

    public void save(UUID userId, MovieDto movie) {
        jdbc.sql(INSERT_FAVORITE).param("user", userId).param("movie", movie.id())
            .param("title", movie.title())
            .param("poster", movie.posterPath())
            .param("overview", movie.overview())
            .param("release", movie.releaseDate())
            .param("rating", movie.rating())
            .update();
    }

    public boolean remove(UUID userId, long movieId) {
        return jdbc.sql(DELETE_FAVORITE).param("user", userId).param("movie", movieId).update() > 0;
    }
}
