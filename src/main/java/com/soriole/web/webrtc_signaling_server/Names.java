package com.soriole.web.webrtc_signaling_server;

public interface Names {
    String EVENT_BUS = "nextRTCEventBus";
    String EVENT_DISPATCHER = "nextRTCEventDispatcher";

    String SCHEDULER_SIZE = "${nextrtc.scheduler_size:50}";
    String SCHEDULER_NAME = "nextRTCPingScheduler";
    String SCHEDULED_PERIOD = "${nextrtc.ping_period:20}";
    String MAX_CONNECTION_SETUP_TIME = "${nextrtc.max_connection_setup_time:30}";
}
