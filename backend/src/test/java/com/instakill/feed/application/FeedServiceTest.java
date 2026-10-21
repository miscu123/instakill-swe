package com.instakill.feed.application;

import com.instakill.feed.domain.FeedRankingStrategy;
import com.instakill.feed.domain.FeedSort;
import com.instakill.post.domain.Post;
import com.instakill.post.domain.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedServiceTest {

    private final PostRepository repository = Mockito.mock(PostRepository.class);

    @Test
    void delegatesToStrategyMatchingRequestedSort() {
        FeedRankingStrategy newest = strategy(FeedSort.NEW);
        FeedRankingStrategy trending = strategy(FeedSort.TRENDING);
        List<Post> expected = List.of(post());
        when(trending.fetch(repository, 2, 5)).thenReturn(expected);
        FeedService service = new FeedService(repository, List.of(newest, trending));

        assertThat(service.listFeed(FeedSort.TRENDING, 2, 5)).isEqualTo(expected);
    }

    @Test
    void defaultsToNewWhenSortIsNull() {
        FeedRankingStrategy newest = strategy(FeedSort.NEW);
        List<Post> expected = List.of(post());
        when(newest.fetch(repository, 0, 5)).thenReturn(expected);
        FeedService service = new FeedService(repository, List.of(newest, strategy(FeedSort.TRENDING)));

        assertThat(service.listFeed(null, 0, 5)).isEqualTo(expected);
    }

    @Test
    void failsFastWhenTwoStrategiesClaimSameSort() {
        assertThatThrownBy(() -> new FeedService(repository,
                List.of(strategy(FeedSort.NEW), strategy(FeedSort.NEW))))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void failsWhenNoStrategyRegisteredForSort() {
        FeedService service = new FeedService(repository, List.of(strategy(FeedSort.NEW)));

        assertThatThrownBy(() -> service.listFeed(FeedSort.TRENDING, 0, 5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Post post() {
        Instant now = Instant.now();
        return new Post(UUID.randomUUID(), UUID.randomUUID(), "content", null, now, now, 0, 0);
    }

    private FeedRankingStrategy strategy(FeedSort sort) {
        FeedRankingStrategy s = mock(FeedRankingStrategy.class);
        when(s.sort()).thenReturn(sort);
        return s;
    }
}
