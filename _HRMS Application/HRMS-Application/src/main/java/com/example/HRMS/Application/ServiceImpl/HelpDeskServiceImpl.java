package com.example.HRMS.Application.ServiceImpl;


import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Repository.HelpDeskRepository;
import com.example.HRMS.Application.Service.HelpDeskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class HelpDeskServiceImpl implements HelpDeskService {

    @Autowired
    private HelpDeskRepository repository;

    @Override
    public HelpDesk createHelpDesk(String helpDeskJson, MultipartFile file) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            HelpDesk helpDesk = mapper.readValue(helpDeskJson, HelpDesk.class);

            if (file != null && !file.isEmpty()) {
                helpDesk.setAttachedFile(file.getBytes());
            }

            return repository.save(helpDesk);
        } catch (Exception e) {
            throw new RuntimeException("Error creating helpdesk entry: " + e.getMessage());
        }
    }

    @Override
    public List<HelpDesk> getAllHelpDesks() {
        return repository.findAll();
    }
    @Override
    public HelpDesk getHelpDeskById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("HelpDesk ticket not found with ID: " + id));
    }
    @Override
    public void deleteHelpDeskById(Long id) {
        HelpDesk helpDesk = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + id));
        repository.delete(helpDesk);
    }

}
