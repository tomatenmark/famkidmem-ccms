let stompClient = null;

function connectPush() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/push/subscribe', function (message) {
            handleMessage(message);
        });
    });
}

async function handleMessage(message){
    //TODO: handle messages
}