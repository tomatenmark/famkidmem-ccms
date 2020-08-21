let thumbnailMap = {};
let masterKey = '';
let videoMap = {key: "", iv: ""};

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
    let encryptedTitleBase64 = video.title;
    let title = decryptToUtf8String(encryptedTitleBase64, key, video.key.iv);
    let encryptedTitleUrlBase64 = encryptedTitleBase64.replace(/\+/g, '-').replace(/\//g, '_');
    let description = decryptToUtf8String(video.description, key, video.key.iv);
    let descriptionShort = getDescriptionShort(description);
    let item = `<div class="video">
                    <div class="cell data">
                        <h3>${title}</h3>
                        <p>
                            <span title="${description}">${descriptionShort}</span><br>
                            ${formattedDate}
                        </p>
                        <a class="button" href="/video/edit-data/${encryptedTitleUrlBase64}">Show/Edit Video data</a>
                    </div>
                    <div class="cell thumbnail">
                        <img src="/img/thumbnail-placeholder.jpg" alt="thumbnail" id="thumbnail_${video.title}" onclick="initPlayVideo('${video.m3u8.filename}', '${video.m3u8.key.key}', '${video.m3u8.key.iv}');"><br>
                        <a class="button" href="/video/replace-thumbnail/${encryptedTitleUrlBase64}">Replace Thumbnail</a>
                    </div>
                    <div class="cell remove">
                        <a class="button" href="/video/remove/${encryptedTitleUrlBase64}">Remove Video</a>
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

function loadThumbnail(filename, base64Encrypted){
    let mapEntry = thumbnailMap[filename];
    let title = mapEntry.title;
    let keyEncrypted = mapEntry.key;
    let iv = mapEntry.iv;
    let key = decryptKey(keyEncrypted, masterKey);
    let base64 = decryptToBase64String(base64Encrypted, key, iv);
    document.getElementById(`thumbnail_${title}`).src = `data:image/png;base64,${base64}`;
    thumbnailMap[filename] = null;
}

function initPlayVideo(filename, key, iv){
    videoMap.key = key;
    videoMap.iv = iv;
    loadBase64FromFile(filename);
}

function playVideo(base64Encrypted){
    let keyEncrypted = videoMap.key;
    let iv = videoMap.iv;
    let key = decryptKey(keyEncrypted, masterKey);
    let base64 = decryptToBase64String(base64Encrypted, key, iv);
    let source = 'data:application/vnd.apple.mpegurl;base64,'+base64;
    doHls(source);
    document.getElementById('overlayBackground').style.display = 'block';
    document.getElementById('video').style.display = 'block';
}

function closeVideo(){
    document.getElementById('overlayBackground').style.display = 'none';
    document.getElementById('video').style.display = 'none';
    document.getElementById('video').src = '';
}

function doHls(source){
    let hls;
    let error;
    if ( Hls.isSupported() ) {
        let video = document.getElementById('video');
        hls = new Hls();
        hls.on(Hls.Events.ERROR, function (event, data) {
            error = data;
            console.log("there was an error with hls");
        });
        hls.loadSource(source);
        hls.attachMedia(video);
    } else {
        alert('Your Browser does not support HLS or does not have a proper plugin to do. Try do install/enable mse (Media Source Extensions)');
    }
}

function handlePushIndex(message){
    let messageObject = JSON.parse(message.body);
    let filename = messageObject.message;
    if(filename.indexOf(".jpg") >= 0){
        loadThumbnail(filename, messageObject.details)
    }
    if(filename.indexOf(".m3u8") >= 0){
        playVideo(messageObject.details)
    }
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

function getDescriptionShort(description){
    if(description.length <= 300){
        return description;
    }
    description = description.substr(0, 300);
    description = description.replace(/\s+\S*$/g, '');
    description = description.trim();
    return description + '...';
}

function getFormattedDate(timestamp, showDateValues){
    let date = new Date(timestamp);
    let formattedDate = '' + date.getFullYear();
    if(showDateValues >= 6){
        formattedDate += '-' + (date.getMonth()+1);
    }
    if(showDateValues === 7){
        formattedDate += '-' + date.getDate();
    }
    return formattedDate;
}
