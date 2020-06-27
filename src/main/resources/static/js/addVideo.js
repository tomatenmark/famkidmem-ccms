//TODO: move to deleteVideo.js
function addTsFilenamesToVideoRemoveForm(){
    const m3u8Base64 = document.getElementById('m3u8').getAttribute('data-m3u8');
    //TODO: decrypt m3u8, find ts filenames and add them to videoRemoveForm
    //example for inputField for second ts file: <input type="hidden" name="tsFilename[1]" value="{FILENAME}">
}

let submit = false;

function addVideo(){
    if(submit){
        submit = false;
        let filesHtml = document.getElementById('files').innerHTML;
        document.getElementById('files').innerHTML = '';
        document.getElementById('formDummy').innerText = filesHtml;
        return true;
    }
    submit = true;
    document.getElementById('fileProcessingErrors').innerText = '';
    document.getElementById("progress").style.display = 'block';
    uploadFile("thumbnailFile", "Step 1/6: upload thumbnail", "/video/upload-thumbnail");
    return false;
}

function handlePushAdd(message){
    let messageObject = JSON.parse(message.body);
    switch(messageObject.message){
        case 'thumbnailUploadComplete':
            uploadFile("videoFile", "Step 2/6: upload video", "/video/upload-video");
            break;
        case 'videoUploadComplete':
            startEncryption();
            break;
        case 'finishedWithThumbnail':
            startShowVideoEncryption();
            break;
        case 'videoEncryptionProgress':
            showProgress(messageObject);
            break;
        case 'videoEncryptionError':
            showVideoEncryptionError(messageObject);
            break;
        case 'finishedWithVideo':
            startShowWebUploadProgress();
            break;
        case 'webBackendUploadProgress':
            document.getElementById('progressBar').value = messageObject.value;
            break;
        case 'finishedWithWebUpload':
            submitAddVideo();
            break;
        case 'error':
            document.getElementById('fileProcessingErrors').innerText = messageObject.details;
            break;
    }
}

function submitAddVideo(){
    document.getElementById('progressBar').value = 99.9;
    document.getElementById('step').innerText = 'Step 6/6: save video';
    document.videoDataForm.submit();
}

function startEncryption() {
    document.getElementById('step').innerText = 'Step 3/6: encrypt thumbnail';
    document.getElementById('progressBar').value = '';
    encrypt();
}

function startShowVideoEncryption() {
    document.getElementById('step').innerText = 'Step 4/6: encrypt video';
    document.getElementById('progressBar').value = '';
    document.getElementById('ffmpegProgress').innerHTML = '<div>starting...</div>';
    document.getElementById('ffmpegProgress').style.display = 'block';
    document.getElementById('autoScrollLabel').style.display = 'inline';
}

function startShowWebUploadProgress() {
    document.getElementById('step').innerText = 'Step 5/6: upload files to web-backend';
    document.getElementById('progressBar').value = '';
    document.getElementById('ffmpegProgress').style.display = 'none';
    document.getElementById('autoScrollLabel').style.display = 'none';
    uploadToWebBackend();
}

function showProgress(messageObject){
    document.getElementById('progressBar').value = messageObject.value;
    let logBox = document.getElementById('ffmpegProgress');
    if(messageObject.override){
        let entries = logBox.children.length;
        logBox.children[entries-1].innerText = messageObject.details;
    } else {
        logBox.innerHTML += '<div>' + messageObject.details + '</div>';
    }
    if(document.getElementById("autoScroll").checked){
        logBox.scrollTop = logBox.scrollHeight;
    }
}

function showVideoEncryptionError(messageObject){
    document.getElementById('progressBar').value = 0;
    document.getElementById('ffmpegProgress').innerHTML += '<div class="error">' + messageObject.details + '</div>';
}

function showError(message){
    document.getElementById('fileProcessingErrors').innerText = message;
    document.getElementById('progressBar').value = 0;
}

function handleError(e){
    showError("error. see console and/or log for details");
    console.error(JSON.stringify(e));
}

function encrypt(){
    let client = new XMLHttpRequest();
    client.addEventListener("error", handleError);
    client.open("POST", "/video/encrypt");
    client.send();
}

function uploadToWebBackend(){
    let client = new XMLHttpRequest();
    client.addEventListener("error", handleError);
    client.open("POST", "/video/upload-web");
    client.send();
}

function uploadFile(fileInputId, step, target) {
    let progress = document.getElementById("progressBar");
    progress.value = 0;

    let fileList = document.getElementById(fileInputId).files;
    let file = fileList[0];
    if(!file){
        showError("No file selected. Could not do: " + step);
        return;
    }

    document.getElementById("step").innerText = step;

    let formData = new FormData();
    let client = new XMLHttpRequest();

    formData.append("file", file);

    client.addEventListener("error", handleError);

    client.addEventListener("load", function(e) {
        progress.value = progress.max;
    });

    client.addEventListener("abort", function(e) {
        console.error(e.message);
        showError("Aborted upload");
    });

    client.upload.addEventListener("progress", function(e) {
        let p = Math.round(100 / e.total * e.loaded);
        progress.value = p;
    });

    client.open("POST", target);
    client.send(formData);
}

function checkSilvester(){
    let month = parseInt(document.getElementById('month').value);
    let day = parseInt(document.getElementById('day').value);
    let maybeSilvester = (month === 12 && day === 31) || (month === 1 && day === 1);
    document.getElementById('silvesterToggle').style.display = maybeSilvester ? 'inline' : 'none';
}
