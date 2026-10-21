package com.instakill.feed.domain;

import com.instakill.post.domain.Post;
import com.instakill.post.domain.PostRepository;

import java.util.List;

/**
 * A pluggable way of ranking/fetching the feed. Each implementation declares
 * which {@link FeedSort} it serves; FeedService picks the right one at runtime.
 */
public interface FeedRankingStrategy {

    /** The sort mode this strategy implements. Must be unique across all strategies. */
    FeedSort sort();

    List<Post> fetch(PostRepository repository, int page, int size);
}
