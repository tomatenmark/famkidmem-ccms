let thumbnailMap = {};
let masterKey = '';

function buildIndex(){
    let videos = JSON.parse(document.getElementById('videos').getAttribute('data-raw'));
    masterKey = document.getElementById('videos').children[0].getAttribute('data-masterKey');
    document.getElementById('videos').innerHTML = '';
    for(let i = 0; i < videos.length; i++){
        let video = videos[i];
        buildIndexItem(video);
    }
    loadThumbnails(videos);
}

function buildIndexItem(video){
    let formattedDate = getFormattedDate(video.timestamp, video.showDateValues);
    let key = decryptKey(video.key.key, masterKey);
    let title = decryptToUtf8String(video.title, key, video.key.iv);
    let description = decryptToUtf8String(video.description, key, video.key.iv);
    let descriptionShort = getDescriptionShort(description);
    let item = `<div class="video" onclick="showMap();">
                    <div class="cell data">
                        <h3>${title}</h3>
                        <p>
                            ${formattedDate}<br>
                            <span title="${description}">${descriptionShort}</span>
                        </p>
                        <a class="button" href="/video/edit-data/${video.title}">Show/Edit Video data</a>
                    </div>
                    <div class="cell thumbnail">
                        <img src="/img/thumbnail-placeholder.png" alt="thumbnail" id="thumbnail_${video.title}" onclick="playVideo(video.m3u8.filename, video.m3u8.key.key, video.m3u8.key.iv);"><br>
                        <a class="button" href="/video/replace-thumbnail/${video.title}">Replace Thumbnail</a>
                    </div>
                    <div class="cell remove">
                        <a class="button" href="/video/remove/${video.title}">Remove Video</a>
                    </div>
                </div>`;
    document.getElementById('videos').innerHTML += item;
}

function loadThumbnails(videos){
    for(let i = 0; i < videos.length; i++){
        let video = videos[i];
        initLoadThumbnail(video.title, video.thumbnail.filename, video.thumbnail.key.key, video.thumbnail.key.iv);
    }
}

function initLoadThumbnail(title, filename, key, iv){
    thumbnailMap[filename] = {
        title: title,
        key: key,
        iv: iv,
        base64: ''
    };
    loadBase64FromFile(filename);
}

function loadThumbnail(message){
    let messageObject = JSON.parse(message.body);
    let filename = messageObject.message;
    let base64Encrypted = messageObject.details;
    let mapEntry = thumbnailMap[filename];
    let title = mapEntry.title;
    let keyEncrypted = mapEntry.key;
    let iv = mapEntry.iv;
    let key = decryptKey(keyEncrypted, masterKey);
    let base64 = decryptToBase64String(base64Encrypted, key, iv);
    document.getElementById(`thumbnail_${title}`).src = `data:image/png;base64,${base64}`;
    thumbnailMap[filename] = null;
}

function loadBase64FromFile(filename){
    let client = new XMLHttpRequest();
    client.addEventListener("error", handleFileReadError);
    client.open("GET", "/video/file/base64/"+filename);
    client.send();
}

function handleFileReadError(e){
    console.error("error while getting base 64 of file");
    console.error(JSON.stringify(e));
}

function playVideo(filename, key, iv){
    //TODO: implement
    //play video
}

function getDescriptionShort(description){
    description = description.substr(0, 300);
    description = description.replace(/\s+\S*$/g, '');
    description = description.trim();
    return description + '...';
}

function getFormattedDate(timestamp, showDateValues){
    let date = new Date(timestamp);
    let formattedDate = '' + date.getFullYear();
    if(showDateValues >= 6){
        formattedDate += '-' + date.getMonth();
    }
    if(showDateValues === 7){
        formattedDate += '-' + date.getDay();
    }
    return formattedDate;
}
