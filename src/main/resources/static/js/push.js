let stompClient = null;

function connectPush(type) {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/push/subscribe', function (message) {
            handleMessage(message, type);
        });
    });
}

async function handleMessage(message, type){
    switch (type){
        case "add":handlePushAdd(message); break;
        case "index":loadThumbnail(message); break;
    }
}
