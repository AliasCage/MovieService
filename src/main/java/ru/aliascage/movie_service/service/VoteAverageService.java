package ru.aliascage.movie_service.service;

import java.util.concurrent.CompletableFuture;

public interface VoteAverageService {
    CompletableFuture<Void> runAsync(String genre);
}
