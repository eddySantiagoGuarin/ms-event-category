package com.world_dance.ms_event_category.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.world_dance.ms_event_category.dto.PageResponseDto;
import com.world_dance.wd_lib_common.dto.EventRequestDto;
import com.world_dance.wd_lib_common.dto.EventResponseDto;
import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.entity.Event;
import com.world_dance.wd_lib_common.enums.Status;
import com.world_dance.wd_lib_common.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public HttpGlobalResponse<EventResponseDto> createEvent(EventRequestDto request, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        HttpGlobalResponse<EventResponseDto> response = new HttpGlobalResponse<>();

        Event event = new Event();

        if (eventRepository.existsByName(request.getName())) {
            throw new RuntimeException("El nombre del evento ya se encuentra registrado.");
        }

        LocalDateTime fechaActual = LocalDateTime.now();

        if (request.getStartDate().isBefore(fechaActual)) {
            throw new RuntimeException("La fecha de inicio no puede ser anterior a la fecha actual.");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("La fecha de finalizacion no puede ser anterior a la fecha de inicio.");
        }

        event.setOwnerId(authenticatedUserId);
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setStatus(request.getStatus());

        eventRepository.save(event);

        EventResponseDto data = new EventResponseDto();
        data.setIdEvent(event.getId());
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

    public HttpGlobalResponse<EventResponseDto> updateEvent(String nameEvent, EventRequestDto eventRequestDto, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        Event event = eventRepository.findByName(nameEvent);
        HttpGlobalResponse<EventResponseDto> response = new HttpGlobalResponse<>();

        if (event == null) {
            response.setMessage("Evento no encontrado por el nombre: " + nameEvent);
            return response;
        }

        if (!Objects.equals(event.getOwnerId(), authenticatedUserId)) {
            throw new SecurityException("No tienes permiso para actualizar este evento.");
        }

        event.setName(eventRequestDto.getName());
        event.setDescription(eventRequestDto.getDescription());
        event.setStartDate(eventRequestDto.getStartDate());
        event.setEndDate(eventRequestDto.getEndDate());
        event.setLocation(eventRequestDto.getLocation());
        event.setStatus(eventRequestDto.getStatus());

        eventRepository.save(event);

        EventResponseDto eventResponseDto = new EventResponseDto();
        eventResponseDto.setIdEvent(event.getId());
        eventResponseDto.setOwnerId(event.getOwnerId());
        eventResponseDto.setName(event.getName());
        eventResponseDto.setDescription(event.getDescription());
        eventResponseDto.setStartDate(event.getStartDate() != null ? event.getStartDate().toString() : null);
        eventResponseDto.setEndDate(event.getEndDate() != null ? event.getEndDate().toString() : null);
        eventResponseDto.setLocation(event.getLocation());
        eventResponseDto.setStatus(event.getStatus());

        response.setData(eventResponseDto);
        response.setMessage("Evento actualizado con exito.");

        return response;
    }

    public HttpGlobalResponse<?> deleteEvent(long eventId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        Event event = eventRepository.findById(eventId).orElse(null);
        LocalDateTime fechaActual = LocalDateTime.now();
        HttpGlobalResponse<?> response = new HttpGlobalResponse<>();

        if (event == null) {
            response.setMessage("Evento no encontrado por el id: " + eventId);
            return response;
        }

        if (!Objects.equals(event.getOwnerId(), authenticatedUserId)) {
            throw new SecurityException("No tienes permiso de eliminar este evento.");
        }

        if (event.getStartDate() != null && event.getEndDate() != null && event.getStartDate().isEqual(fechaActual) && event.getEndDate().isAfter(fechaActual)) {
            response.setMessage("Este evento no se puede eliminar debido a que ya inicio");
            return response;
        }

        eventRepository.deleteById(event.getId());
        response.setMessage("Evento eliminado con exito.");
        return response;
    }

    public List<EventResponseDto> getEvents() {
        return eventRepository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public HttpGlobalResponse<EventResponseDto> getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + eventId));

        HttpGlobalResponse<EventResponseDto> response = new HttpGlobalResponse<>();
        response.setData(toResponseDto(event));
        response.setMessage("Evento obtenido con éxito.");
        return response;
    }

    /**
     * Listado paginado para optimizar la carga del catálogo de eventos.
     * - filter "ACTIVE": solo eventos activos (catálogo público).
     * - filter "INACTIVE": solo los eventos propios que no estén activos ("Mis Eventos" > Borradores/Inactivos).
     * - filter "MINE": todos los eventos propios, sin importar estado ("Mis Eventos").
     * - cualquier otro valor ("ALL"): activos de cualquiera + todos los propios ("Mis Eventos" > Todos).
     * Sin usuario autenticado, siempre se limita a eventos activos (catálogo público).
     */
    public HttpGlobalResponse<PageResponseDto<EventResponseDto>> getEventsPage(int page, int size, String filter, Long authenticatedUserId) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "id"));

        Page<Event> result;
        if (authenticatedUserId == null) {
            result = eventRepository.findByStatus(Status.ACTIVE, pageable);
        } else if ("ACTIVE".equalsIgnoreCase(filter)) {
            result = eventRepository.findByStatus(Status.ACTIVE, pageable);
        } else if ("INACTIVE".equalsIgnoreCase(filter)) {
            result = eventRepository.findByStatusNotAndOwnerId(Status.ACTIVE, authenticatedUserId, pageable);
        } else if ("MINE".equalsIgnoreCase(filter)) {
            result = eventRepository.findByOwnerId(authenticatedUserId, pageable);
        } else {
            result = eventRepository.findByStatusOrOwnerId(Status.ACTIVE, authenticatedUserId, pageable);
        }

        List<EventResponseDto> content = result.getContent().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());

        PageResponseDto<EventResponseDto> pageDto = new PageResponseDto<>(
                content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages(), result.isLast());

        HttpGlobalResponse<PageResponseDto<EventResponseDto>> response = new HttpGlobalResponse<>();
        response.setData(pageDto);
        response.setMessage("Eventos obtenidos con éxito.");
        return response;
    }

    private EventResponseDto toResponseDto(Event event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setIdEvent(event.getId());
        dto.setOwnerId(event.getOwnerId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setStartDate(event.getStartDate() != null ? event.getStartDate().toString() : null);
        dto.setEndDate(event.getEndDate() != null ? event.getEndDate().toString() : null);
        dto.setLocation(event.getLocation());
        dto.setStatus(event.getStatus());
        return dto;
    }
}

