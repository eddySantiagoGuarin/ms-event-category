package com.world_dance.ms_event_category.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.world_dance.ms_event_category.service.EventService;
import com.world_dance.wd_lib_common.dto.EventRequestDto;
import com.world_dance.wd_lib_common.dto.EventResponseDto;
import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/events")
public class EventController {
    
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<HttpGlobalResponse<EventResponseDto>> createEvent(@Valid @RequestBody EventRequestDto request){

        try {
            HttpGlobalResponse<EventResponseDto> response = eventService.createEvent(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch(Exception e) {
            HttpGlobalResponse<EventResponseDto> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
        
    } 
    
    @PatchMapping("/update")
    public ResponseEntity<HttpGlobalResponse<EventResponseDto>> updateEvent(@RequestParam("nameEvent") String nameEvent, @Valid @RequestBody EventRequestDto request){
        
        try{
            HttpGlobalResponse<EventResponseDto> response = eventService.updateEvent(nameEvent , request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e ) {
            HttpGlobalResponse<EventResponseDto> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpGlobalResponse<?>> deleteEvent(@RequestParam("eventId") long eventId, @RequestParam("ownerId") long ownerId){

        try{
            HttpGlobalResponse<?> response = eventService.deleteEvent(eventId, ownerId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            HttpGlobalResponse<EventResponseDto> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

    }


    @GetMapping("/getEvents")
    public ResponseEntity<HttpGlobalResponse<List<EventResponseDto>>> getEvents() {
        try {
            List<EventResponseDto> response = eventService.getEvents();
            HttpGlobalResponse<List<EventResponseDto>> globalResponse = new HttpGlobalResponse<>();
            globalResponse.setData(response);
            globalResponse.setMessage("Eventos obtenidos con éxito.");
            
            return ResponseEntity.status(HttpStatus.OK).body(globalResponse);
        } catch (Exception e) {
            HttpGlobalResponse<List<EventResponseDto>> errorResponse = new HttpGlobalResponse<>();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
