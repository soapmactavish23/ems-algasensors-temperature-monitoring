package com.algaworks.algasensors.temperature.monitoring.infrastructure.rabbitmq;

import com.algaworks.algasensors.temperature.monitoring.api.model.TemperatureLogData;
import com.algaworks.algasensors.temperature.monitoring.domain.service.SensorAlertService;
import com.algaworks.algasensors.temperature.monitoring.domain.service.TemperatureMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {

    private final TemperatureMonitoringService temperatureMonitoringService;
    private final SensorAlertService sensorAlertService;

    @SneakyThrows
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROCESS_TEMPERATURE, concurrency = "2-3")
    public void handleTemperatureProcessing(@Payload TemperatureLogData temperatureLogData,
                                            @Headers Map<String, Object> headers) {
        temperatureMonitoringService.processTemperatureReading(temperatureLogData);
        Thread.sleep(Duration.ofSeconds(5));
    }

    @SneakyThrows
    @RabbitListener(queues = RabbitMQConfig.QUEUE_ALERTING, concurrency = "2-3")
    public void handleAlerting(@Payload TemperatureLogData temperatureLogData) {
        sensorAlertService.handleAlert(temperatureLogData);
        Thread.sleep(Duration.ofSeconds(5));
    }

}
