let stompClient = null;
let cC;
let pushType;

function checkConnected(){
    const connected = stompClient !== null && stompClient.connected !== undefined && stompClient.connected;
    if(!connected){
        clearInterval(cC);
        cC = null;
        connectPush(pushType);
    }
}

function connectPush(type) {
    pushType = type;
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        cC = setInterval('checkConnected()', 1000);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/push/subscribe', function (message) {
            handleMessage(message, pushType);
        });
    });
}

async function handleMessage(message, type){
    switch (type){
        case "add":handlePushAdd(message); break;
        case "index":handlePushIndex(message); break;
    }
}
