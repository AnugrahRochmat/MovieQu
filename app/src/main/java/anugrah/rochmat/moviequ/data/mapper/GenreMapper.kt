package anugrah.rochmat.moviequ.data.mapper

import anugrah.rochmat.moviequ.data.datasource.local.MovieGenreEntity
import anugrah.rochmat.moviequ.data.model.GenreData
import anugrah.rochmat.moviequ.domain.entity.Genre

object GenreMapper {
    fun mapToMovieGenreEntity(genre: Genre): MovieGenreEntity {
        return MovieGenreEntity(
            id = genre.id,
            name = genre.name
        )
    }

    fun mapToDomain(entity: MovieGenreEntity): Genre {
        return Genre(
            id = entity.id,
            name = entity.name
        )
    }

    fun mapToMovieGenreEntityList(genres: List<Genre>): List<MovieGenreEntity> {
        return genres.map { mapToMovieGenreEntity(it) }
    }

    fun mapToDomainList(entities: List<MovieGenreEntity>): List<Genre> {
        return entities.map { mapToDomain(it) }
    }

    fun mapGenreDatasToDomain(genreDatas: List<GenreData>): List<Genre> {
        return genreDatas.map { genreData ->
            Genre(
                id = genreData.id,
                name = genreData.name
            )
        }
    }
}