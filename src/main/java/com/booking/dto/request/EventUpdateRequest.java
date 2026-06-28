package com.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventUpdateRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String city;

    @NotBlank
    private String venue;

    @NotBlank
    private String language;

    @NotBlank
    private String type;

    @NotNull
    private Double rating;

    @NotNull
    private Double price;
}
