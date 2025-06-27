package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.HelpDesk;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HelpDeskService {
    HelpDesk createHelpDesk(String helpDeskJson, MultipartFile file);
    List<HelpDesk> getAllHelpDesks();
    HelpDesk getHelpDeskById(Long id);
    void deleteHelpDeskById(Long id);

}
