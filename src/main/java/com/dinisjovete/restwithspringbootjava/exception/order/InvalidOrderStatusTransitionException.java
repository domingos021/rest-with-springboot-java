package com.dinisjovete.restwithspringbootjava.exception.order;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderStatus;

/**
 * Exceção de domínio lançada quando se tenta uma transição de status inválida
 * (ex.: PENDING -> DELIVERED).
 *
 * Camada: Model/Domínio.
 * Responsabilidade: representar a violação da regra de negócio de forma
 * tipada, para o @ControllerAdvice poder mapeá-la para um HTTP 409 (Conflict)
 * ou 422, em vez de um erro 500 genérico.
 *
 * Estende RuntimeException (unchecked): o chamador não é obrigado a tratá-la
 * em cada método, e o tratamento fica centralizado no handler global.
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final OrderStatus from;
    private final OrderStatus to;

    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Transição de status inválida: " + from + " -> " + to);
        this.from = from;
        this.to = to;
    }

    public OrderStatus getFrom() {
        return from;
    }

    public OrderStatus getTo() {
        return to;
    }
}
