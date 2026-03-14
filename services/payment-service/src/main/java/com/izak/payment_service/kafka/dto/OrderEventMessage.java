package com.izak.payment_service.kafka.dto;

import com.izak.payment_service.kafka.events.OrderEvent;

public class OrderEventMessage {
  private String event_type;
  private OrderEvent data;

  public String getEvent_type(){return event_type;}
  public void setEvent_type(String event_type) {
    this.event_type = event_type;
  }

  public OrderEvent getData(){return data;}
  public void setData(OrderEvent data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return "OrderEventMessage{" +
          "event_type='" + event_type + '\'' +
          ", data=" + data +
          '}';
  }
}
