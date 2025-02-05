package com.example.movieofficial.api.movie.usecases.impl;

import com.example.movieofficial.api.movie.repositories.MovieRepository;
import com.example.movieofficial.api.movie.usecases.RatingUseCase;
import com.example.movieofficial.utils.exceptions.InputInvalidException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DefaultRatingUseCase implements RatingUseCase {

    MovieRepository movieRepository;
    RedisTemplate<String, Object> redisTemplate;

    private static final DefaultRedisScript<Boolean> CHECK_AND_ADD_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('SISMEMBER', KEYS[1], ARGV[1]) == 1 then " +
                    "   return false " +
                    "else " +
                    "   redis.call('SADD', KEYS[1], ARGV[1]) " +
                    "   return true " +
                    "end", Boolean.class
    );

    private boolean addIfNotExists(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute(CHECK_AND_ADD_SCRIPT, List.of(key), value));
    }

    @Override
    @Transactional
    public void execute(String movieSlug, Integer rating, String ratingKey) {
        String redisKey = "rating:" + movieSlug;
        if (!addIfNotExists(redisKey, ratingKey)) {
            throw new InputInvalidException("Input invalid", List.of("You already rated this movie."));
        }
        movieRepository.updateRating(movieSlug, rating);
        redisTemplate.delete("movie_detail:" + movieSlug);
    }

}
