package com.szabodev.example.spring.product.order.microservice.service.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szabodev.example.spring.product.order.microservice.config.JmsConfig;
import com.szabodev.example.spring.product.order.microservice.dto.DeletedOrderDTO;
import com.szabodev.example.spring.product.order.microservice.dto.OrderRequestDTO;
import com.szabodev.example.spring.product.order.microservice.dto.OrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductStoreServiceImpl implements ProductStoreService {

    private final JmsTemplate jmsTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public OrderResponseDTO requestOrder(OrderRequestDTO request) {
        log.info("Sending request {} to {}", request, "order-request-queue");
        Message receivedMessage = jmsTemplate.sendAndReceive("order-request-queue", session -> {
            try {
                return session.createTextMessage(objectMapper.writeValueAsString(request));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to convert message");
            }
        });
        log.info("Response received: {}", receivedMessage);
        try {
            TextMessage textMessage = (TextMessage) receivedMessage;
            return textMessage != null ? objectMapper.readValue(textMessage.getText(), OrderResponseDTO.class) : null;
        } catch (JsonProcessingException | JMSException e) {
            log.error("Something went wrong", e);
        }
        return null;
    }

    @Override
    public void orderDeleted(DeletedOrderDTO deletedOrder) {
        log.info("Sending deleted order info {} to {}", deletedOrder, "order-request-queue");
        jmsTemplate.convertAndSend("order-deleted-queue", deletedOrder);
    }
}
