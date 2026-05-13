package com.turny.ApiTurny.domain.dto.review;

import java.math.BigDecimal;
import java.util.Map;

public record RatingResumen(
        BigDecimal promedio,
        Integer total,
        Map<Integer, Integer> distribucion  // { 5: 10, 4: 5, 3: 2, 2: 1, 1: 0 }
) {}
