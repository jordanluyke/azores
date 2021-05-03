package com.jordanluyke.azores.audio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jordanluyke.azores.audio.model.AudioType;
import lombok.*;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FrequenciesRequest {
    private List<FrequencyRequest> frequencies;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class FrequencyRequest {
        private AudioType audioType = AudioType.TONE;
        private Optional<Double> tone = Optional.empty();
        private Optional<Double> carrierFrequency = Optional.empty();
        private Optional<Double> modulatorFrequency = Optional.empty();
        private Optional<ZoneId> zoneId = Optional.empty();
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm") private Optional<LocalTime> from = Optional.empty();
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm") private Optional<LocalTime> to = Optional.empty();
    }
}
