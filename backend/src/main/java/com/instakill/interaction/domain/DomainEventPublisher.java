package com.instakill.interaction.domain;

/**
 * Outbound port for domain events. Implementations may log, push to Kafka,
 * dispatch asynchronously, etc.
 */
public interface DomainEventPublisher {

    void publish(Object event);
}
