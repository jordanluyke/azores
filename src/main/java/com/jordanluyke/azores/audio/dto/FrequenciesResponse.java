package com.jordanluyke.azores.audio.dto;

import com.jordanluyke.azores.audio.model.AudioContext;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FrequenciesResponse {
    private List<? extends AudioContext> frequencies;
    private boolean isActive;
}
