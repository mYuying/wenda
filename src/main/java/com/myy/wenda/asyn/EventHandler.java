package com.myy.wenda.asyn;

import java.util.List;

public interface EventHandler {
    void doHandle(EventModel model); //事件处理器要处理什么样的逻辑的接口

    List<EventType> getSupportEventTypes();  // 事件处理器关注的事件类型列表
}
