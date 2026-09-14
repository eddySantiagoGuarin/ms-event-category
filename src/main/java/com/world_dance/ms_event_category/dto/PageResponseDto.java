package com.world_dance.ms_event_category.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Envoltorio de paginación para listados grandes (eventos, modalidades).
 * Evita cargar todo el conjunto de datos en una sola respuesta.
 */
@Data
@NoArgsConstructor
public class PageResponseDto<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public PageResponseDto(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
