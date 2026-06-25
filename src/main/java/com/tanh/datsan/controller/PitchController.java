package com.tanh.datsan.controller;

import com.tanh.datsan.dto.PitchCreateRequest;
import com.tanh.datsan.dto.PitchDTO;
import com.tanh.datsan.service.PitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pitches")
@RequiredArgsConstructor
public class PitchController {

    private final PitchService pitchService;

    @PostMapping
    public ResponseEntity<PitchDTO> createPitch(@RequestBody PitchCreateRequest request) {
        PitchDTO pitch = pitchService.createPitch(request);
        return ResponseEntity.ok(pitch);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PitchDTO> getPitchById(@PathVariable Long id) {
        PitchDTO pitch = pitchService.getPitchById(id);
        return ResponseEntity.ok(pitch);
    }

    @GetMapping
    public ResponseEntity<List<PitchDTO>> getAllPitches() {
        List<PitchDTO> pitches = pitchService.getAllPitches();
        return ResponseEntity.ok(pitches);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PitchDTO>> getActivePitches() {
        List<PitchDTO> pitches = pitchService.getActivePitches();
        return ResponseEntity.ok(pitches);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PitchDTO> updatePitch(
            @PathVariable Long id,
            @RequestBody PitchCreateRequest request) {
        PitchDTO pitch = pitchService.updatePitch(id, request);
        return ResponseEntity.ok(pitch);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePitch(@PathVariable Long id) {
        pitchService.deletePitch(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activatePitch(@PathVariable Long id) {
        pitchService.activatePitch(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivatePitch(@PathVariable Long id) {
        pitchService.deactivatePitch(id);
        return ResponseEntity.noContent().build();
    }
}
