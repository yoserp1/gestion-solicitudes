package com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.messaging;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.entity.OutboxEventoEntity;
import com.yoserp1.prueba.solicitudes.infrastructure.adapter.outbound.persistence.repository.OutboxEventoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("!docs")
@ConditionalOnProperty(name = "app.outbox.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OutboxPublisher {

	private final OutboxEventoRepository repository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final Clock clock;

	@Value("${app.kafka.solicitudes-topic:solicitudes.v1}")
	private String topic;

	@Scheduled(fixedDelayString = "${app.outbox.fixed-delay:1000}")
	public void publicarPendientes() {
		for (OutboxEventoEntity event : repository.findTop100ByPublicadoEnIsNullOrderByOcurridoEnAsc()) {
			publicar(event);
		}
	}

	private void publicar(OutboxEventoEntity event) {
		try {
			kafkaTemplate.send(topic, event.getAggregateId().toString(), event.getPayload()).get(10, TimeUnit.SECONDS);
			event.setPublicadoEn(OffsetDateTime.now(clock));
			repository.save(event);
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			registrarFallo(event, exception);
		} catch (Exception exception) {
			registrarFallo(event, exception);
		}
	}

	private void registrarFallo(OutboxEventoEntity event, Exception exception) {
		event.setIntentos(event.getIntentos() + 1);
		repository.save(event);
		log.warn("No fue posible publicar el evento {} en el intento {}", event.getEventId(), event.getIntentos(), exception);
	}
}
