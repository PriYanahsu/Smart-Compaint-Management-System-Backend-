package com.example.demo.controller;

import com.example.demo.entity.EntityComplaint;
import com.example.demo.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/complaint")
@CrossOrigin(origins = "*")
public class CompaintController {

    @Autowired
    private ComplaintRepository repository;

    @PostMapping
    public ResponseEntity<String> submitComplaint(
            @RequestParam String problemName,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam String time,
            @RequestParam(required = false) MultipartFile image) {

        EntityComplaint complaint = new EntityComplaint();
        complaint.setProblemName(problemName);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setTime(time);

        try {
            if (image != null && !image.isEmpty()) {
                complaint.setImage(image.getBytes());
            }
            repository.save(complaint);
            return ResponseEntity.ok("Complaint submitted successfully.");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Image upload failed.");
        }
    }

    @GetMapping
    public ResponseEntity<List<EntityComplaint>> getAllComplaints() {
        List<EntityComplaint> complaints = repository.findAll();

        // populate Base64 images on the fly
        complaints.forEach(c -> {
            if (c.getImage() != null) {
                c.setImageBase64("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(c.getImage()));
            }
        });
        return ResponseEntity.ok(complaints);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(@PathVariable Long id) {
        EntityComplaint data = repository.findById(id).orElse(null);

        if(data != null){
            repository.deleteById(id);
            return ResponseEntity.ok("File is deleted");
        }
        else return ResponseEntity.notFound().build();
    }


}
