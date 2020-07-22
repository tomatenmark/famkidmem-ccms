package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.body.RequestBodyVideoData;
import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class VideoDataService {

    private final CryptoUtil cryptoUtil;

    @Autowired
    public VideoDataService(CryptoUtil cryptoUtil) {
        this.cryptoUtil = cryptoUtil;
    }

    void addEncryptedVideoMeta(RequestBodyVideoData videoDataRequest, HttpServletRequest request) throws GeneralSecurityException, UnsupportedEncodingException {
        byte[] key = cryptoUtil.generateSecureRandomKeyParam();
        byte[] iv = cryptoUtil.generateSecureRandomKeyParam();
        byte[] title = request.getParameter("title").getBytes("UTF-8");
        byte[] description = request.getParameter("description").getBytes("UTF-8");
        byte[] titleEncrypted = cryptoUtil.encrypt(title, key, iv);
        byte[] descriptionEncrypted = cryptoUtil.encrypt(description, key, iv);
        videoDataRequest.setTitle(cryptoUtil.toBase64(titleEncrypted));
        videoDataRequest.setDescription(cryptoUtil.toBase64(descriptionEncrypted));
        videoDataRequest.setKey(cryptoUtil.encryptKey(key));
        videoDataRequest.setIv(cryptoUtil.toBase64(iv));
    }

    List<String> getPersonsList(String personsFieldValue){
        String sanitizedPersonsFieldValue = personsFieldValue
                .replace(" ", "")
                .replaceAll("^,", "")
                .replaceAll(",$", "")
                .replaceAll(",,+", ",");
        String[] personsArray = sanitizedPersonsFieldValue.split(",");
        return Arrays.asList(personsArray);
    }

    List<Integer> getYearsList(HttpServletRequest request){
        List<Integer> yearsList = new ArrayList<>();
        Integer year = Integer.valueOf(request.getParameter("year"));
        Integer month = Integer.valueOf(request.getParameter("month"));
        boolean silvester = "silvester".equals(request.getParameter("silvester"));
        yearsList.add(year);
        if(silvester && month == 12){
            yearsList.add(year+1);
        }
        if(silvester && month == 1){
            yearsList.add(0, year-1);
        }
        return yearsList;
    }

    long getTimestamp(HttpServletRequest request){
        Integer year = Integer.valueOf(request.getParameter("year"));
        Integer month = Integer.valueOf(request.getParameter("month"));
        Integer day = Integer.valueOf(request.getParameter("day"));
        if(month < 1){
            month = 1;
        }
        if(day < 1){
            day = 1;
        }
        LocalDateTime date = LocalDateTime.of(year, month, day, 0, 0);
        ZonedDateTime zonedDateTime = date.atZone(ZoneId.of("Europe/Paris"));
        return zonedDateTime.toInstant().toEpochMilli();
    }

    int getShowDateValues(HttpServletRequest request){
        int showDateValues = 4;
        Integer month = Integer.valueOf(request.getParameter("month"));
        Integer day = Integer.valueOf(request.getParameter("day"));
        if(month > 0){
            showDateValues += 2;
        }
        if(day > 0){
            showDateValues += 1;
        }
        return  showDateValues;
    }

}
