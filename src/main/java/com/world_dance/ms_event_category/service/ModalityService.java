package com.world_dance.ms_event_category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.dto.ModalityRequestDto;
import com.world_dance.wd_lib_common.dto.ModalityResponseDto;
import com.world_dance.wd_lib_common.entity.Modality;
import com.world_dance.wd_lib_common.repository.EventRepository;
import com.world_dance.wd_lib_common.repository.ModalityRepository;

import lombok.RequiredArgsConstructor;

@Service
public class ModalityService {

    private final ModalityRepository modalityRepository ;

    private final EventRepository eventRepository ;

    public ModalityService(ModalityRepository modalityRepository, EventRepository eventRepository) {
        this.modalityRepository = modalityRepository;
        this.eventRepository = eventRepository;
    }
    
    

    public HttpGlobalResponse<ModalityResponseDto> createModality(ModalityRequestDto request, Long eventId){

        HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();

        Modality modality = new Modality() ;

        if(!eventRepository.existsById(eventId)){
            throw  new RuntimeException("El evento enviado por parametro no existe"); 
        }
        if(modalityRepository.existsByCategoryAndDivisionAndMinAgeAndMaxAge(request.getCategory(), request.getDivision(),request.getMinAge(),request.getMaxAge())){
            throw  new RuntimeException("Esta categoria ya existe"); 
        }

        modality.setEventId(eventId);
        modality.setCategory(request.getCategory());
        modality.setDivision(request.getDivision());
        modality.setMinAge(request.getMinAge());
        modality.setMaxAge(request.getMaxAge());
        modality.setStyle(request.getStyle());

        modalityRepository.save(modality);

        ModalityResponseDto modalityResponseDto = new ModalityResponseDto();

        modalityResponseDto.setEventId(eventId);
        modalityResponseDto.setCategory(modality.getCategory());
        modalityResponseDto.setDivision(modality.getDivision());
        modalityResponseDto.setMinAge(modality.getMinAge());
        modalityResponseDto.setMaxAge(modality.getMaxAge());
        modalityResponseDto.setStyle(modality.getStyle());

        response.setData(modalityResponseDto);
        response.setMessage("Modalidad creada con exito");

        return response ;

    }
    public HttpGlobalResponse<ModalityResponseDto> updateModality(ModalityRequestDto request,long modalityId){

        HttpGlobalResponse<ModalityResponseDto> response = new HttpGlobalResponse<>();

        Modality modality = modalityRepository.findById(modalityId);

    
        if(modalityRepository.existsByCategoryAndDivisionAndMinAgeAndMaxAge(request.getCategory(), request.getDivision(),request.getMinAge(),request.getMaxAge())){
            throw  new RuntimeException("Esta categoria ya existe"); 
        }

        modality.setCategory(request.getCategory());
        modality.setDivision(request.getDivision());
        modality.setMinAge(request.getMinAge());
        modality.setMaxAge(request.getMaxAge());
        modality.setStyle(request.getStyle());

        modalityRepository.save(modality);

        ModalityResponseDto modalityResponseDto = new ModalityResponseDto();
        
        modalityResponseDto.setCategory(modality.getCategory());
        modalityResponseDto.setDivision(modality.getDivision());
        modalityResponseDto.setMinAge(modality.getMinAge());
        modalityResponseDto.setMaxAge(modality.getMaxAge());
        modalityResponseDto.setStyle(modality.getStyle());

        response.setData(modalityResponseDto);
        response.setMessage("Modalidad actualizada con exito");

        return response ;

    }

    public HttpGlobalResponse<?> deleteModality (long modalityId){

        Modality modality = modalityRepository.findById(modalityId);
       
        HttpGlobalResponse<?> response = new HttpGlobalResponse<>();
        
        modalityRepository.deleteById(modality.getId());

        response.setMessage("categoria eliminada con exito");

        return response ;
    }

    public List<ModalityResponseDto> getModalitiesByEventId(Long eventId) {
    List<Modality> modalities = modalityRepository.findByEventId(eventId);
    
    if (modalities.isEmpty()) {
        throw new RuntimeException("No se encontraron modalidades registradas para el evento con ID: " + eventId);
    }
    
    List<ModalityResponseDto> listModality = modalities.stream().map(modality -> {
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
    
        return listModality;
    }

}
