package com.world_dance.ms_event_category.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.world_dance.wd_lib_common.dto.EventRequestDto;
import com.world_dance.wd_lib_common.dto.HttpGlobalResponse;
import com.world_dance.wd_lib_common.dto.ModalityRequestDto;
import com.world_dance.wd_lib_common.entity.Event;
import com.world_dance.wd_lib_common.enums.Category;
import com.world_dance.wd_lib_common.enums.Division;
import com.world_dance.wd_lib_common.enums.Status;
import com.world_dance.wd_lib_common.repository.EventRepository;
import com.world_dance.wd_lib_common.repository.ModalityRepository;

@ExtendWith(MockitoExtension.class)
class EventAndModalityOwnershipServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModalityRepository modalityRepository;

    @InjectMocks
    private EventService eventService;

    @InjectMocks
    private ModalityService modalityService;

    @Test
    void createEventUsesAuthenticatedOwnerIdAndRejectsDuplicateName() {
        EventRequestDto request = new EventRequestDto();
        request.setName("Festival");
        request.setDescription("desc");
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(2));
        request.setLocation("Bogotá");
        request.setStatus(Status.ACTIVE);
        request.setOwnerId(999L);

        when(eventRepository.existsByName("Festival")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(1L);
            return event;
        });

        HttpGlobalResponse<?> response = eventService.createEvent(request, 77L);

        assertEquals("El evento fue creado de manera exitosa.", response.getMessage());
    }

    @Test
    void createModalityRejectsDuplicateWithinSameEventOnly() {
        ModalityRequestDto request = new ModalityRequestDto();
        request.setCategory(Category.LATIN);
        request.setDivision(Division.DUET);
        request.setMinAge(18L);
        request.setMaxAge(30L);
        request.setStyle("Salsa");

        Event event = new Event();
        event.setId(5L);
        event.setOwnerId(10L);

        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
        when(modalityRepository.existsByEventIdAndCategoryAndDivisionAndMinAgeAndMaxAge(5L, Category.LATIN, Division.DUET, 18L, 30L))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> modalityService.createModality(request, 5L, 10L));
        assertEquals("Esta categoria ya existe para este evento", ex.getMessage());
    }
}
