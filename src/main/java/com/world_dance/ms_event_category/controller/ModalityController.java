package com.world_dance.ms_event_category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.world_dance.ms_event_category.service.ModalityService;
import com.world_dance.wd_lib_common.dto.EventResponseDto;
import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.dto.ModalityRequestDto;
import com.world_dance.wd_lib_common.dto.ModalityResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/modality")
public class ModalityController {

    private final ModalityService modalityService;

    @PostMapping("/create/{eventId}")
    public ResponseEntity<HttpGlobalResponse<ModalityResponseDto>> createModality(
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Valid @RequestBody ModalityRequestDto request,
            @PathVariable Long eventId) {
        try {
            HttpGlobalResponse<ModalityResponseDto> response = modalityService.createModality(request, eventId, authenticatedUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/update/{modalityId}")
    public ResponseEntity<HttpGlobalResponse<ModalityResponseDto>> updateModality(
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Valid @RequestBody ModalityRequestDto request,
            @PathVariable Long modalityId) {
        try {
            HttpGlobalResponse<ModalityResponseDto> response = modalityService.updateModality(request, modalityId, authenticatedUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();
            response.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/delete/{modalityId}")
    public ResponseEntity<HttpGlobalResponse<?>> deleteCategory(
            @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @PathVariable Long modalityId) {

        try {
            HttpGlobalResponse<?> response = modalityService.deleteModality(modalityId, authenticatedUserId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            HttpGlobalResponse<EventResponseDto> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/getModalitiesByEventId/{eventId}")
    public ResponseEntity<HttpGlobalResponse<List<ModalityResponseDto>>> getModalitiesByEventId(
            @PathVariable Long eventId) {
        try {
            List<ModalityResponseDto> response = modalityService.getModalitiesByEventId(eventId);
            HttpGlobalResponse<List<ModalityResponseDto>> globalResponse = new HttpGlobalResponse<>();
            globalResponse.setData(response);
            globalResponse.setMessage("Modalidades obtenidas con éxito.");

            return ResponseEntity.status(HttpStatus.OK).body(globalResponse);
        } catch (Exception e) {
            HttpGlobalResponse<List<ModalityResponseDto>> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

   
}
