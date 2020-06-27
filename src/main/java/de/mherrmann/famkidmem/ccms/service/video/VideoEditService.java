package de.mherrmann.famkidmem.ccms.service.video;

import de.mherrmann.famkidmem.ccms.body.ResponseBodyGetVideos;
import de.mherrmann.famkidmem.ccms.exception.WebBackendException;
import de.mherrmann.famkidmem.ccms.item.Person;
import de.mherrmann.famkidmem.ccms.item.Video;
import de.mherrmann.famkidmem.ccms.item.Year;
import de.mherrmann.famkidmem.ccms.service.ConnectionService;
import de.mherrmann.famkidmem.ccms.utils.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Service
public class VideoEditService {

    private final ConnectionService connectionService;
    private final VideoIndexService videoIndexService;
    private final ExceptionUtil exceptionUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoEditService.class);

    public VideoEditService(ConnectionService connectionService, VideoIndexService videoIndexService, ExceptionUtil exceptionUtil){
        this.connectionService = connectionService;
        this.videoIndexService = videoIndexService;
        this.exceptionUtil = exceptionUtil;
    }

    public void fillEditDataModel(Model model, String title){
        model.addAttribute("post", false);
        try {
            Video video = getVideo(title);
            model.addAttribute("video", video);
            model.addAttribute("persons", getPersonsStringifiedList(video.getPersons()));
            model.addAttribute("years", getYearsStringifiedList(video.getYears()));
            model.addAttribute("year", getYearFromTimestamp(video.getTimestamp()));
            model.addAttribute("month", getMonthFromTimestamp(video.getTimestamp(), video.getShowDateValues()));
            model.addAttribute("day", getDayFromTimestamp(video.getTimestamp(), video.getShowDateValues()));
            model.addAttribute("success", true);
        } catch (Exception ex){
            exceptionUtil.handleException(ex, model, LOGGER);
        }

    }

    public void fillReplaceThumbnailModel(Model model){
        /* TODO: fill:
         * post=false
         * title (video.title)
         */
    }

    public void replaceThumbnail(MultipartFile file, Model model, String title){
        //TODO:  save file to files folder locally, encrypt it and then upload it to web backend
    }

    public void editData(HttpServletRequest request, Model model, String title){
        //TODO: save new video attributes (maybe get old via web-backend get video request)
    }

    @SuppressWarnings("unchecked") //we know, the assignment will work
    Video getVideo(String title) throws Exception {
        ResponseEntity<ResponseBodyGetVideos> response = connectionService.doGetSingleVideoRequest(title);
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new WebBackendException(response.getBody());
        }
        return response.getBody().getVideos().get(0);
    }

    private Integer getYearFromTimestamp(Timestamp timestamp){
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.YEAR);
    }

    private Integer getMonthFromTimestamp(Timestamp timestamp, int showDateValues){
        if(showDateValues < 6){
            return 0;
        }
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.MONTH)+1; //month is 0 based
    }

    private Integer getDayFromTimestamp(Timestamp timestamp, int showDateValues){
        if(showDateValues < 7){
            return 0;
        }
        long time = timestamp.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private String getPersonsStringifiedList(List<Person> persons){
        StringBuilder sB = new StringBuilder();
        for(Person person : persons){
            sB.append(",").append(person.getName());
        }
        return sB.toString().substring(1);
    }

    private String getYearsStringifiedList(List<Year> years){
        StringBuilder sB = new StringBuilder();
        for(Year year : years){
            sB.append(",").append(year.getValue());
        }
        return sB.toString().substring(1);
    }
}
