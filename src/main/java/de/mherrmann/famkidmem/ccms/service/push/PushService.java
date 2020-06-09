package de.mherrmann.famkidmem.ccms.service.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PushService {

    private final SimpMessagingTemplate pusher;
    private static PushMessage lastMessage;

    @Autowired
    public PushService(SimpMessagingTemplate pusher) {
        this.pusher = pusher;
    }

    public void push(PushMessage pushMessage){
        String message = asJsonString(pushMessage);
        String destination = "/push/subscribe";
        pusher.convertAndSend(destination, message);
        lastMessage = pushMessage;
    }

    public static PushMessage getLastMessage() {
        return lastMessage;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
