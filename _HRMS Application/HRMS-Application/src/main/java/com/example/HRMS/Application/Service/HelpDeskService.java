package com.example.HRMS.Application.Service;

import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Entity.HelpDeskStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HelpDeskService {
    HelpDesk createHelpDesk(String helpDeskJson, MultipartFile file);
    List<HelpDesk> getAllHelpDesks();
    HelpDesk getHelpDeskById(Long id);
    void deleteHelpDeskById(Long id);
    public List<HelpDesk> getHelpDeskByStatus(HelpDeskStatus helpDeskStatus);
    public HelpDesk updateHelpDeskStatus(Long id,HelpDeskStatus helpDeskStatus);

    public List<HelpDesk> updateStatusByEmployeeId(Long empId, HelpDeskStatus helpDeskStatus);


    public List<HelpDesk> updateStatusByEmployeeName(String firstName, String lastName, HelpDeskStatus helpDeskStatus);

    HelpDesk approveHelpDeskStatus(Long id, String remark);

    HelpDesk rejectHelpDeskStatus(Long id, String remark);

    List<HelpDesk> getHelpDesksByEmployeeId(Long employeeId);
}
