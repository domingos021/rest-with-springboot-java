package com.dinisjovete.restwithspringbootjava.data_model.entities.order;

/**
 * Enum OrderStatus (Status do Pedido).
 *
 * Camada: Model.
 * Responsabilidade: representar os estados possíveis do ciclo de vida de um
 * pedido e as transições permitidas entre eles.
 *
 * Fluxo normal:
 *   PENDING -> PAID -> PROCESSING -> SHIPPED -> DELIVERED
 *
 * Cancelamento:
 *   PENDING, PAID ou PROCESSING -> CANCELED
 *
 * Estados finais (não aceitam mais transição): DELIVERED e CANCELED.
 *
 * Importante: no Order este enum é gravado com EnumType.STRING, então
 * RENOMEAR um valor exige migração dos dados já salvos no banco.
 */
public enum OrderStatus {

    // Pedido criado, aguardando pagamento.
    PENDING,

    // Pagamento confirmado.
    PAID,

    // Pedido em separação/preparação.
    PROCESSING,

    // Pedido enviado à transportadora.
    SHIPPED,

    // Pedido entregue ao cliente (estado final).
    DELIVERED,

    // Pedido cancelado (estado final).
    CANCELED;

    /**
     * Informa se o pedido pode passar do status atual para o status destino.
     * Centraliza a regra aqui para ninguém pular etapas (ex.: PENDING -> DELIVERED).
     *
     * O switch é uma EXPRESSÃO sem "default" de propósito: o compilador exige
     * que todos os valores do enum estejam cobertos. Se alguém adicionar um
     * novo status e esquecer de definir sua regra, o código deixa de compilar
     * em vez de falhar silenciosamente em produção.
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (target == null) {
            return false;
        }

        return switch (this) {
            case PENDING -> target == PAID || target == CANCELED;
            case PAID -> target == PROCESSING || target == CANCELED;
            case PROCESSING -> target == SHIPPED || target == CANCELED;
            case SHIPPED -> target == DELIVERED;
            case DELIVERED, CANCELED -> false;
        };
    }

    /**
     * Indica se o status é final, ou seja, o pedido não muda mais.
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELED;
    }
}