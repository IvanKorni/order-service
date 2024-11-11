package ru.javaops.cloudjava.ordersservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.javaops.cloudjava.ordersservice.dto.CreateOrderRequest;
import ru.javaops.cloudjava.ordersservice.dto.OrderResponse;
import ru.javaops.cloudjava.ordersservice.service.MenuOrderService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/menu-orders")
public class MenuOrderController {

    public static final String USER_HEADER = "X-User-Name";

    private final MenuOrderService menuOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderResponse> submitMenuOrder(@RequestBody @Valid CreateOrderRequest request,
                                               @RequestHeader(USER_HEADER) String username) {
        return menuOrderService.createOrder(request, username);
    }

    @GetMapping
    public Flux<OrderResponse> getOrdersOfUser(
            @RequestParam(value = "from", defaultValue = "0")
            int from,
            @RequestParam(value = "size", defaultValue = "10")
            int size,
            @RequestHeader(USER_HEADER) String username) {
        log.info("Received request to GET orders of user with name={}", username);
        return menuOrderService.getOrdersOfUser(username, from, size);
    }
}
