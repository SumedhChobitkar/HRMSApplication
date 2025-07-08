package com.example.HRMS.Application.Controller;


import com.example.HRMS.Application.Entity.HelpDesk;
import com.example.HRMS.Application.Entity.HelpDeskStatus;
import com.example.HRMS.Application.Service.HelpDeskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/helpdesk")
public class HelpDeskController {

    @Autowired
    private HelpDeskService helpDeskService;

//    @PostMapping
//    public ResponseEntity<?> createHelpDesk(
//            @RequestParam("helpDesk") String helpDeskJson,
//            @RequestParam(value = "file", required = false) MultipartFile file) {
//        try {
//            HelpDesk helpDesk = helpDeskService.createHelpDesk(helpDeskJson, file);
//            return ResponseEntity.ok("HelpDesk ticket created with ID: " + helpDesk.getId());
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Failed to create helpdesk ticket: " + e.getMessage());
//        }
//    }

    @PostMapping
    public ResponseEntity<?> createHelpDesk(
            @RequestParam("helpDesk") String helpDeskJson,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            HelpDesk helpDesk = helpDeskService.createHelpDesk(helpDeskJson, file);


            return ResponseEntity.ok(helpDesk);

        } catch (Exception e) {

            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to create helpdesk ticket",
                    "message", e.getMessage()
            ));
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

    @GetMapping("/helpdesk/status/{status}")
    public ResponseEntity<?> getHelpDeskByStatus(@PathVariable("status") HelpDeskStatus helpDeskStatus) {
        try {
            List<HelpDesk> helpDesks = helpDeskService.getHelpDeskByStatus(helpDeskStatus);
            return ResponseEntity.ok(helpDesks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving help desk by status: " + e.getMessage());
        }
    }

    @PutMapping("/helpdesk/{id}/status")
    public ResponseEntity<?> updateHelpDeskStatus(@PathVariable Long id, @RequestParam HelpDeskStatus helpDeskStatus) {
        try {
            HelpDesk updated = helpDeskService.updateHelpDeskStatus(id, helpDeskStatus);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("HelpDesk not found with id: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating status: " + e.getMessage());
        }
    }

    @PutMapping("/helpdesk/update-status/by-employee-id/{empId}")
    public ResponseEntity<?> updateStatusByEmpId(@PathVariable Long empId, @RequestParam HelpDeskStatus helpDeskStatus) {
        try {
            List<HelpDesk> updatedList = helpDeskService.updateStatusByEmployeeId(empId, helpDeskStatus);
            return ResponseEntity.ok(updatedList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/helpdesk/update-status/by-employee-name")
    public ResponseEntity<?> updateStatusByEmpName(@RequestParam String firstName, @RequestParam String lastName,
                                                   @RequestParam HelpDeskStatus helpDeskStatus) {
        try {
            List<HelpDesk> updatedList = helpDeskService.updateStatusByEmployeeName(firstName, lastName, helpDeskStatus);
            return ResponseEntity.ok(updatedList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
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
