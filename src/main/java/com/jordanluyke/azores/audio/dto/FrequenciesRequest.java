package com.jordanluyke.azores.audio.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jordanluyke.azores.audio.model.FrequencyType;
import com.jordanluyke.azores.audio.model.WaveType;
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
        private FrequencyType frequencyType = FrequencyType.TONE;
        private Optional<WaveType> waveType = Optional.empty();
        private Optional<Double> frequency = Optional.empty();
        private Optional<Double> carrierFrequency = Optional.empty();
        private Optional<Double> modulatorFrequency = Optional.empty();
        private Optional<ZoneId> zoneId = Optional.empty();
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm") private Optional<LocalTime> from = Optional.empty();
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm") private Optional<LocalTime> to = Optional.empty();
    }
}
