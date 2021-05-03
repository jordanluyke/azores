package com.jordanluyke.azores.audio.dto;

import com.jordanluyke.azores.audio.model.AudioContext;
import lombok.*;

import java.util.List;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FrequenciesResponse {
    private List<? extends AudioContext> frequencies;
    private boolean enabled;
}
