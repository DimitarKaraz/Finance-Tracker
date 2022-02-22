package com.example.financetracker.model.dto.specialStatisticsDTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Component
@Getter
@Setter
@NoArgsConstructor
public class FilterByDatesRequestDTO {

    @NotNull(message = "Start date cannot be null.")
    @PastOrPresent(message = "Start date cannot be null.")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    @PastOrPresent(message = "Start date cannot be null.")
    private LocalDate endDate;
}
