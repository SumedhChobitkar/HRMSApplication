package com.example.HRMS.Application.Controller;


import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Service.HelpDeskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/helpdesk")
public class HelpDeskController {

    @Autowired
    private HelpDeskService helpDeskService;

    @PostMapping
    public ResponseEntity<?> createHelpDesk(
            @RequestParam("helpDesk") String helpDeskJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            HelpDesk helpDesk = helpDeskService.createHelpDesk(helpDeskJson, file);
            return ResponseEntity.ok("HelpDesk ticket created with ID: " + helpDesk.getId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create helpdesk ticket: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<HelpDesk>> getAllHelpDesks() {
        return ResponseEntity.ok(helpDeskService.getAllHelpDesks());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getHelpDeskById(@PathVariable Long id) {
        try {
            HelpDesk helpDesk = helpDeskService.getHelpDeskById(id);
            return ResponseEntity.ok(helpDesk);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadAttachedFile(@PathVariable Long id) {
        HelpDesk helpDesk = helpDeskService.getHelpDeskById(id);
        if (helpDesk.getAttachedFile() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment().filename("attached_file").build());

        return new ResponseEntity<>(helpDesk.getAttachedFile(), headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHelpDesk(@PathVariable Long id) {
        try {
            helpDeskService.deleteHelpDeskById(id);
            return ResponseEntity.ok("HelpDesk ticket deleted with ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }


}
