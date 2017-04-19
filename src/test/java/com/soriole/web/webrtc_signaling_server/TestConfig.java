package com.soriole.web.webrtc_signaling_server;

import org.mockito.Answers;
import org.mockito.Mockito;
import com.soriole.web.webrtc_signaling_server.api.NextRTCEventBus;
import org.springframework.context.annotation.*;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
@ComponentScan(basePackages = "com.soriole.web.webrtc_signaling_server")
@PropertySource("classpath:nextrtc.properties")
public class TestConfig {

    @Primary
    @Bean(name = Names.EVENT_BUS)
    public NextRTCEventBus eventBus() {
        return new NextRTCEventBus();
    }

    @Primary
    @Bean(name = Names.SCHEDULER_NAME)
    public ScheduledExecutorService scheduler() {
        return Mockito.mock(ScheduledExecutorService.class, Answers.RETURNS_DEEP_STUBS.get());
    }

}
