package com.instakill.feed.application;

import com.instakill.feed.domain.FeedSort;
import com.instakill.post.domain.Post;
import com.instakill.post.domain.PostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NewestFeedRankingStrategyTest {

    private final PostRepository repository = Mockito.mock(PostRepository.class);
    private final NewestFeedRankingStrategy strategy = new NewestFeedRankingStrategy();

    @Test
    void declaresNewSort() {
        assertThat(strategy.sort()).isEqualTo(FeedSort.NEW);
    }

    @Test
    void delegatesToRepositoryLatest() {
        Instant now = Instant.now();
        List<Post> posts = List.of(
                new Post(UUID.randomUUID(), UUID.randomUUID(), "a", null, now, now, 1, 1),
                new Post(UUID.randomUUID(), UUID.randomUUID(), "b", null, now.minusSeconds(10), now, 2, 2)
        );
        when(repository.findLatest(1, 2)).thenReturn(posts);

        assertThat(strategy.fetch(repository, 1, 2)).isEqualTo(posts);
        verify(repository).findLatest(1, 2);
    }
}
