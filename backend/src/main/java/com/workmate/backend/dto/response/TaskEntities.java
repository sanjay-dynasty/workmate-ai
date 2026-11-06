package com.workmate.backend.dto.response;

import java.time.LocalDate;

import com.workmate.backend.dto.Priority;

public record TaskEntities(

        String taskName,

        Priority priority,

        LocalDate dueDate,

        String projectName

) {

}
