package com.georgiytitov.movies.favorite;

import static com.georgiytitov.movies.support.TestMovies.FIRST;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.georgiytitov.movies.tmdb.TmdbClient;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.server.ResponseStatusException;

class FavoriteServiceTest {
    private static final UUID USER = UUID.fromString("2a9e33cb-0937-47bb-a13c-06f412ef128f");

    private final FavoriteRepository favorites = mock(FavoriteRepository.class);
    private final TmdbClient tmdb = mock(TmdbClient.class);
    private final FavoriteService service = new FavoriteService(favorites, tmdb);

    @Test
    void addingFavoritePersistsTheTmdbSnapshot() {
        when(tmdb.movie(FIRST.id())).thenReturn(FIRST);
        assertEquals(FIRST, service.add(USER, FIRST.id()));
        verify(favorites).save(USER, FIRST);
    }

    @Test
    void unknownMovieDoesNotCreateAFavorite() {
        var missing = new ResponseStatusException(NOT_FOUND, "Movie not found");
        when(tmdb.movie(FIRST.id())).thenThrow(missing);
        var actual = assertThrows(
            ResponseStatusException.class,
            () -> service.add(USER, FIRST.id())
        );
        assertSame(missing, actual);
        verifyNoInteractions(favorites);
    }

    @Test
    void missingFavoriteReturnsNotFound() {
        var error = assertThrows(
            ResponseStatusException.class,
            () -> service.remove(USER, FIRST.id())
        );
        assertEquals(NOT_FOUND, error.getStatusCode());
    }

    @Test
    void existingFavoriteCanBeRemoved() {
        when(favorites.remove(USER, FIRST.id())).thenReturn(true);
        service.remove(USER, FIRST.id());
        verify(favorites).remove(USER, FIRST.id());
    }

    @Test
    void duplicateDatabaseKeyBecomesAClientError() {
        when(tmdb.movie(FIRST.id())).thenReturn(FIRST);
        doThrow(new DuplicateKeyException("Duplicate favorite")).when(favorites).save(USER, FIRST);
        var error = assertThrows(
            ResponseStatusException.class,
            () -> service.add(USER, FIRST.id())
        );
        assertEquals(BAD_REQUEST, error.getStatusCode());
    }
}
