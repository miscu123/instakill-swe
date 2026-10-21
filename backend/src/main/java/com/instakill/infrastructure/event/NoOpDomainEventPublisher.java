package com.instakill.infrastructure.event;

import com.instakill.interaction.domain.DomainEventPublisher;
import org.springframework.stereotype.Component;

/** Null Object: default publisher that intentionally does nothing. */
@Component
public class NoOpDomainEventPublisher implements DomainEventPublisher {

    @Override
    public void publish(Object event) {
        // intentionally empty
    }
}
