package de.mherrmann.famkidmem.ccms.service;

import de.mherrmann.famkidmem.ccms.exception.EncryptionException;
import de.mherrmann.famkidmem.ccms.service.push.PushMessage;
import de.mherrmann.famkidmem.ccms.service.push.PushService;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class FfmpegService {

    String ffmpegCommandName = "ffmpeg"; //used for unit testing

    private final PushService pushService;
    private final CryptoUtil cryptoUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(FfmpegService.class);

    @Autowired
    public FfmpegService(PushService pushService, CryptoUtil cryptoUtil) {
        this.pushService = pushService;
        this.cryptoUtil = cryptoUtil;
    }

    public int encryptVideo(String name) throws EncryptionException {
        try {
            Process process = buildFfmpegProcess(name);
            return handleFfmpeg(process);
        } catch(EncryptionException | IOException ex){
            throw new EncryptionException("Exception during video encryption (ffmpeg): " + ex);
        }
    }

    private Process buildFfmpegProcess(String name) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] commands = {
                ffmpegCommandName,
                "-y",
                "-i",
                "./files/video.mp4",
                "-hls_time", "10",
                "-hls_flags", "periodic_rekey",
                "-hls_key_info_file", "./files/enc.keyinfo",
                "-hls_playlist_type", "vod",
                "-hls_segment_filename", "\"./files/"+name+".%d.ts\"",
                "./files/index.m3u8"};
        return runtime.exec(commands);
    }

    private int handleFfmpeg(Process process) throws IOException, EncryptionException {
        updateKeyFilesForFfmpeg();

        //ffmpeg outputs all to stderr ^^
        BufferedReader bR = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        State state = new State();
        while ((line = bR.readLine()) != null) {
            handleFfmpegLine(line, state);
        }

        if(state.isErrorState()){
            LOGGER.error("Ffmpeg Error(s). See previous log entries for details");
            pushService.push(PushMessage.videoEncryptionError("Ffmpeg Error(s)."));
            throw new EncryptionException("Ffmpeg Error(s).");
        }

        LOGGER.info("Ffmpeg successfully encrypted the video.");

        return state.tsFiles;
    }

    private void handleFfmpegLine(String line, State state) throws IOException {
        LOGGER.debug("Ffmpeg says: " + line);
        handleFfmpegSpecialLine(line, state);
        String possibleBeginningsPattern = "^(ffmpeg|\\s|\\[|Input|Stream|Press|Output|video|frame|$).*$";
        if(!line.matches(possibleBeginningsPattern) || line.contains("error")){
            state.errorState = true;
            handleError(line);
            return;
        }
        if(line.matches("^(.*Opening\\s'crypto:.*|(video|frame).*)$")){
            sendProgress(line, state);
        }
    }

    private void handleFfmpegSpecialLine(String line, State state) throws IOException {
        if(line.matches("^\\s*Duration:\\s\\d+:\\d+:\\d+.*$")){
            setSeconds(state, line);
            setTsFilesExpected(state);
        }
        if(line.matches("^(.*Opening\\s'crypto:.*|video.*)$")){
            state.tsFiles++;
            updateKeyFilesForFfmpeg();
        }
        if(line.startsWith("video")){
            state.finished = true;
        }
        if(line.matches("^\\s*Stream.*?Video.*$")){
            setFramesExpected(state, line);
        }
        if(line.startsWith("frame")){
            setFrames(state, line);
        }
    }

    private void updateKeyFilesForFfmpeg() throws IOException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        String iv = cryptoUtil.generateSecureRandomKeyParamHex();
        String keyBase64 = cryptoUtil.toBase64(key);
        BufferedWriter writer = new BufferedWriter(new FileWriter("./files/enc.keyinfo"));
        writer.write("data:;base64,"+keyBase64+"\n./files/enc.key\n"+iv);
        writer.close();
        FileOutputStream stream = new FileOutputStream("./files/enc.key");
        stream.write(key);
        stream.close();
    }

    private void setFramesExpected(State state, String line){
        String pattern = "^\\s*Stream.*?Video.*?,\\s(\\d+)\\sfps,.*$";
        int framesPerSecond = Integer.valueOf(line.replaceFirst(pattern, "$1"));
        state.framesExpected = (int) Math.floor(state.seconds * framesPerSecond);
    }

    private void setFrames(State state, String line){
        String pattern = "^frame\\s*=\\s*(\\d+)\\s*fps.*$";
        state.frames = Integer.valueOf(line.replaceFirst(pattern, "$1"));
    }


    private void setSeconds(State state, String line){
        state.seconds = calculateSeconds(line);
    }

    private double calculateSeconds(String line){
        String pattern = "^\\s*Duration:\\s(\\d+):(\\d+):(\\d+\\.\\d+).*$";
        int hours = Integer.valueOf(line.replaceFirst(pattern, "$1"));
        int minutes = Integer.valueOf(line.replaceFirst(pattern, "$2")) + hours*60;
        return Double.valueOf(line.replaceFirst(pattern, "$3")) + minutes*60;
    }

    private void setTsFilesExpected(State state){
        state.tsFilesExpected = (int) Math.ceil(state.seconds / 10);
    }

    private void sendProgress(String line, State state){
        String logLine = extractRelevantAndBeautify(line, state);
        pushService.push(PushMessage.videoEncryptionProgress(logLine, state.frameLineBefore, state.getPercentage()));
        state.frameLineBefore = line.startsWith("frame");
    }

    private String extractRelevantAndBeautify(String line, State state){
        switch(line.substring(0, 5)){
            case "[hls ": return String.format("Chunk %d/%d", state.tsFiles+1, state.tsFilesExpected);
            case "frame": return String.format("Frame %d/%d", state.frames, state.framesExpected);
            case "video": return "Finished";
        }
        return "";
    }

    private void handleError(String line){
        LOGGER.error("Ffmpeg Error: " + line);
        pushService.push(PushMessage.videoEncryptionError(line));
    }

    private class State {
        double seconds;
        int frames;
        int framesExpected;
        int tsFiles = -1;
        int tsFilesExpected;
        boolean frameLineBefore;
        boolean errorState;
        boolean finished;

        int getPercentage() {
            if(framesExpected == 0){
                return 0;
            }
            int percentage = (int) Math.round(100.0 / framesExpected * frames);
            if (frames >= framesExpected) {
                percentage = 100;
            }
            return percentage;
        }

        boolean isErrorState() {
            return errorState || !finished;
        }
    }

}
