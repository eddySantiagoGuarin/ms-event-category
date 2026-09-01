package com.world_dance.ms_event_category.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.dto.ModalityRequestDto;
import com.world_dance.wd_lib_common.dto.ModalityResponseDto;
import com.world_dance.wd_lib_common.entity.Event;
import com.world_dance.wd_lib_common.entity.Modality;
import com.world_dance.wd_lib_common.repository.EventRepository;
import com.world_dance.wd_lib_common.repository.ModalityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModalityService {

    private final ModalityRepository modalityRepository;
    private final EventRepository eventRepository;

    public HttpGlobalResponse<ModalityResponseDto> createModality(ModalityRequestDto request, Long eventId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new RuntimeException("El evento enviado por parametro no existe");
        }

        if (!Objects.equals(event.getOwnerId(), authenticatedUserId)) {
            throw new SecurityException("No tienes permiso para crear modalidades en este evento.");
        }

        if (modalityRepository.existsByEventIdAndCategoryAndDivisionAndMinAgeAndMaxAge(
                eventId, request.getCategory(), request.getDivision(), request.getMinAge(), request.getMaxAge())) {
            throw new RuntimeException("Esta categoria ya existe para este evento");
        }

        Modality modality = new Modality();
        modality.setEventId(eventId);
        modality.setCategory(request.getCategory());
        modality.setDivision(request.getDivision());
        modality.setMinAge(request.getMinAge());
        modality.setMaxAge(request.getMaxAge());
        modality.setStyle(request.getStyle());

        modalityRepository.save(modality);

        ModalityResponseDto modalityResponseDto = new ModalityResponseDto();
        modalityResponseDto.setId(modality.getId());
        modalityResponseDto.setEventId(eventId);
        modalityResponseDto.setCategory(modality.getCategory());
        modalityResponseDto.setDivision(modality.getDivision());
        modalityResponseDto.setMinAge(modality.getMinAge());
        modalityResponseDto.setMaxAge(modality.getMaxAge());
        modalityResponseDto.setStyle(modality.getStyle());

        response.setData(modalityResponseDto);
        response.setMessage("Modalidad creada con exito");

        return response;
    }

    public HttpGlobalResponse<ModalityResponseDto> updateModality(ModalityRequestDto request, long modalityId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();

        Modality modality = modalityRepository.findById(modalityId).orElse(null);
        if (modality == null) {
            throw new RuntimeException("No se encontró la modalidad especificada.");
        }

        Event event = eventRepository.findById(modality.getEventId()).orElse(null);
        if (event == null) {
            throw new RuntimeException("El evento asociado a la modalidad no existe.");
        }

        if (!Objects.equals(event.getOwnerId(), authenticatedUserId)) {
            throw new SecurityException("No tienes permiso para actualizar esta modalidad.");
        }

        if (modalityRepository.existsByEventIdAndCategoryAndDivisionAndMinAgeAndMaxAge(
                modality.getEventId(), request.getCategory(), request.getDivision(), request.getMinAge(), request.getMaxAge())
                && !(Objects.equals(modality.getCategory(), request.getCategory())
                        && Objects.equals(modality.getDivision(), request.getDivision())
                        && Objects.equals(modality.getMinAge(), request.getMinAge())
                        && Objects.equals(modality.getMaxAge(), request.getMaxAge()))) {
            throw new RuntimeException("Esta categoria ya existe para este evento");
        }

        modality.setCategory(request.getCategory());
        modality.setDivision(request.getDivision());
        modality.setMinAge(request.getMinAge());
        modality.setMaxAge(request.getMaxAge());
        modality.setStyle(request.getStyle());

        modalityRepository.save(modality);

        ModalityResponseDto modalityResponseDto = new ModalityResponseDto();
        modalityResponseDto.setId(modality.getId());
        modalityResponseDto.setEventId(modality.getEventId());
        modalityResponseDto.setCategory(modality.getCategory());
        modalityResponseDto.setDivision(modality.getDivision());
        modalityResponseDto.setMinAge(modality.getMinAge());
        modalityResponseDto.setMaxAge(modality.getMaxAge());
        modalityResponseDto.setStyle(modality.getStyle());

        response.setData(modalityResponseDto);
        response.setMessage("Modalidad actualizada con exito");

        return response;
    }

    public HttpGlobalResponse<?> deleteModality(long modalityId, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            throw new SecurityException("No se pudo identificar al usuario autenticado.");
        }

        Modality modality = modalityRepository.findById(modalityId).orElse(null);
        HttpGlobalResponse<?> response = new HttpGlobalResponse<>();

        if (modality == null) {
            response.setMessage("Modalidad no encontrada.");
            return response;
        }

        Event event = eventRepository.findById(modality.getEventId()).orElse(null);
        if (event == null) {
            throw new RuntimeException("El evento asociado a la modalidad no existe.");
        }

        if (!Objects.equals(event.getOwnerId(), authenticatedUserId)) {
            throw new SecurityException("No tienes permiso para eliminar esta modalidad.");
        }

        modalityRepository.deleteById(modality.getId());
        response.setMessage("categoria eliminada con exito");

        return response;
    }

    public List<ModalityResponseDto> getModalitiesByEventId(Long eventId) {
        List<Modality> modalities = modalityRepository.findByEventId(eventId);

        if (modalities.isEmpty()) {
            throw new RuntimeException("No se encontraron modalidades registradas para el evento con ID: " + eventId);
        }

        return modalities.stream().map(modality -> {
            ModalityResponseDto modalityResponseDto = new ModalityResponseDto();
            modalityResponseDto.setId(modality.getId());
            modalityResponseDto.setEventId(modality.getEventId());
            modalityResponseDto.setCategory(modality.getCategory());
            modalityResponseDto.setDivision(modality.getDivision());
            modalityResponseDto.setMinAge(modality.getMinAge());
            modalityResponseDto.setMaxAge(modality.getMaxAge());
            modalityResponseDto.setStyle(modality.getStyle());
            return modalityResponseDto;
        }).toList();
    }
}

