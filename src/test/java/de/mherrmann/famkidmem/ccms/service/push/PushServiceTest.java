package de.mherrmann.famkidmem.ccms.service.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mherrmann.famkidmem.ccms.WsTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import de.mherrmann.famkidmem.ccms.WsTestUtils.MyStompFrameHandler;
import de.mherrmann.famkidmem.ccms.WsTestUtils.MyStompSessionHandler;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PushServiceTest {

    @Value("${local.server.port}")
    private int port;


    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private WsTestUtils wsTestUtils = new WsTestUtils();

    @Autowired
    PushService pushService;

    @Before
    public void setUp() throws Exception {
        String wsUrl = "ws://localhost:" + port + "/ws";
        stompClient = wsTestUtils.createWebSocketClient();
        stompSession = stompClient.connect(wsUrl, new MyStompSessionHandler()).get();
    }

    @After
    public void tearDown() {
        stompSession.disconnect();
        stompClient.stop();
    }

    @Test
    public void shouldConnectToSocket() {
        assertThat(stompSession.isConnected()).isTrue();
    }


    @Test
    public void shouldPushTestMessage() throws Exception {
        PushMessage testMessage = PushMessage.thumbnailEncryptionProgress(55);
        CompletableFuture<String> resultKeeper = prepare();

        pushService.push(testMessage);

        assertThat(resultKeeper.get(2, SECONDS)).isEqualTo(asJsonString(testMessage));
        assertThat(PushService.getLastMessage()).isEqualTo(testMessage);
    }

    private CompletableFuture<String> prepare() throws Exception {
        CompletableFuture<String> resultKeeper = new CompletableFuture<>();
        stompSession.subscribe(
                "/push/subscribe",
                new MyStompFrameHandler(resultKeeper::complete));
        Thread.sleep(1000);
        return resultKeeper;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
