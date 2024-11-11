package ru.javaops.cloudjava.ordersservice.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.javaops.cloudjava.ordersservice.config.props.OrderServiceProps;
import ru.javaops.cloudjava.ordersservice.dto.GetMenuInfoRequest;
import ru.javaops.cloudjava.ordersservice.dto.GetMenuInfoResponse;
import ru.javaops.cloudjava.ordersservice.exception.OrderServiceException;

import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class MenuClient {

    private final WebClient webClient;
    private final OrderServiceProps props;

    public Mono<GetMenuInfoResponse> getMenuInfo(GetMenuInfoRequest request) {
        return webClient
                .post()
                .uri(props.getMenuInfoPath())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        Mono.error(new OrderServiceException("Menu Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE)))
                .bodyToMono(GetMenuInfoResponse.class)
                // Таймаут на получение данных от Menu Service
                .timeout(props.getDefaultTimeout())
                // Когда истекает время ожидания, выбрасывается TimeoutException
                .onErrorMap(TimeoutException.class, ex ->
                        new OrderServiceException("Request to Menu Service timed out", HttpStatus.SERVICE_UNAVAILABLE));
                //todo сделать с retryWhen
    }
}
