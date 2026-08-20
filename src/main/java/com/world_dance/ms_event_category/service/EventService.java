package com.world_dance.ms_event_category.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.world_dance.wd_lib_common.dto.EventRequestDto;
import com.world_dance.wd_lib_common.dto.EventResponseDto;
import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.entity.Event;
import com.world_dance.wd_lib_common.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }


    public HttpGlobalResponse<EventResponseDto> createEvent(EventRequestDto request){
       HttpGlobalResponse<EventResponseDto> response = new HttpGlobalResponse<>();
        
        Event event = new Event();
        
        if(eventRepository.existsByName(request.getName()) ){
            throw new RuntimeException("El nombre del evento ya se encuentra registrado.");
        }

        LocalDateTime fechaActual = LocalDateTime.now();
        
        if(request.getStartDate().isBefore(fechaActual)){
            throw new RuntimeException("La fecha de inicio no puede ser anterior a la fecha actual.");
        }
            
        if(request.getEndDate().isBefore(request.getStartDate())){
            throw new RuntimeException("La fecha de finalizacion no puede ser anterior a la fecha de inicio.");
        }
        
        event.setOwnerId(request.getOwnerId());
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setStatus(request.getStatus());
        
        eventRepository.save(event);

        EventResponseDto data = new EventResponseDto();
        data.setOwnerId(event.getOwnerId());
        data.setName(event.getName());
        data.setDescription(event.getDescription());
        data.setStartDate(event.getStartDate() != null ? event.getStartDate().toString() : null);
        data.setEndDate(event.getEndDate() != null ? event.getEndDate().toString() : null);
        data.setLocation(event.getLocation());
        data.setStatus(event.getStatus());
        data.setMessage("El evento fue creado de manera exitosa.");
        
        response.setData(data);
        response.setMessage(data.getMessage());

        return response;
    }

    public HttpGlobalResponse<EventResponseDto> updateEvent (String nameEvent, EventRequestDto eventRequestDto){

        Event event = eventRepository.findByName(nameEvent);
        
        HttpGlobalResponse<EventResponseDto> response = new HttpGlobalResponse<>();
        
        if(event == null){
            response.setMessage("Evento no encontrado por el nombre: " + nameEvent);
            return response;
        }

        event.setName(eventRequestDto.getName());
        event.setDescription(eventRequestDto.getDescription());
        event.setStartDate(eventRequestDto.getStartDate());
        event.setEndDate(eventRequestDto.getEndDate());
        event.setLocation(eventRequestDto.getLocation());
        event.setStatus(eventRequestDto.getStatus());
        
        eventRepository.save(event);

        EventResponseDto eventResponseDto = new EventResponseDto();

        eventResponseDto.setOwnerId(event.getOwnerId());
        eventResponseDto.setName(event.getName());
        eventResponseDto.setDescription(event.getDescription());
        eventResponseDto.setStartDate(event.getStartDate().toString());
        eventResponseDto.setEndDate(event.getEndDate().toString());
        eventResponseDto.setOwnerId(event.getOwnerId());
        eventResponseDto.setOwnerId(event.getOwnerId());

        response.setData(eventResponseDto);
        response.setMessage("Evento actualizado con exito.");
        
        return response;

    }


    public HttpGlobalResponse<?> deleteEvent (long eventId, Long ownerId){

        Event event = eventRepository.findById(eventId);
        LocalDateTime fechaActual = LocalDateTime.now();
        HttpGlobalResponse<?> response = new HttpGlobalResponse<>();

        if(event.getStartDate().isEqual( fechaActual) && event.getEndDate().isAfter(fechaActual)){  
            response.setMessage("Este evento no se puede eliminar debido a que ya inicio");
        }

        if(event.getOwnerId().equals(ownerId)){
            eventRepository.deleteById(event.getId());
            response.setMessage("Evento eliminado con exito.");
        }else{
            response.setMessage("No tienes permiso de eliminar este evento.");
        }
        return response ;
    }

    
   public List<EventResponseDto> getEvents() {
    List<Event> events = eventRepository.findAll();
    List<EventResponseDto> listEvent = new ArrayList<>();
    
    for (Event event : events) {
        EventResponseDto eventResponseDto = new EventResponseDto();
        
       
        eventResponseDto.setIdEvent(event.getId()); 
        eventResponseDto.setOwnerId(event.getOwnerId());
        eventResponseDto.setName(event.getName());
        eventResponseDto.setDescription(event.getDescription());
        eventResponseDto.setStartDate(event.getStartDate() != null ? event.getStartDate().toString() : null);
        eventResponseDto.setEndDate(event.getEndDate() != null ? event.getEndDate().toString() : null);
        eventResponseDto.setLocation(event.getLocation());
        eventResponseDto.setStatus(event.getStatus());
        
        listEvent.add(eventResponseDto);
    }
    
    return listEvent;
    }
    

}
