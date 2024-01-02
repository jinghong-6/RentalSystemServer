package com.example.rental.utils;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            System.out.println(session);

            String payload = message.getPayload();
            // 处理接收到的消息
            session.sendMessage(new TextMessage("Server received: " + payload));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "topic.queue1"),
//            exchange = @Exchange(name = "itcast.topic",type = ExchangeTypes.TOPIC),
//            key = "order.#"
//    ))
//    public void listenQueue1(@Payload String msg){
//        System.out.println(msg);
//    }
}

