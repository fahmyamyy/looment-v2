package com.looment.coreservice.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PostCategory {
    RANDOM("RANDOM"),
    MATH("MATH"),
    SCIENCE("SCIENCE"),
    HISTORY("HISTORY"),
    BIOLOGY("BIOLOGY"),
    PHYSICS("PHYSICS"),
    LANGUAGES("LANGUAGES"),
    ;
    private final String name;
}
