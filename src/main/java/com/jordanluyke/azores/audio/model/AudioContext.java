package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jsyn.unitgen.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

@Getter
@Setter
@ToString
public abstract class AudioContext {
    protected FrequencyType frequencyType;
    protected WaveType waveType;

    public static UnitOscillator getOscillatorFromWaveType(WaveType waveType) {
        switch(waveType) {
            case SINE:
                return new SineOscillator();
            case SQUARE:
                return new SquareOscillator();
            case TRIANGLE:
                return new TriangleOscillator();
            case SAWTOOTH:
                return new SawtoothOscillator();
            default:
                throw new RuntimeException("wavetype not supported");
        }
    }
//    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<String> from = Optional.empty();
//    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<String> to = Optional.empty();
//    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<ZoneId> zone = Optional.empty();
//
//    @JsonIgnore
//    public boolean isTimeframeSet() {
//        return from.isPresent() && to.isPresent() && zone.isPresent();
//    }
//
//    @JsonIgnore
//    public boolean isWithinTimeframe() {
//        if(!isTimeframeSet())
//            throw new RuntimeException("Timeframe not set");
//        LocalDateTime fromTimeToday = LocalTime.parse(from.get())
//                .atDate(LocalDate.now(zone.get()))
//                .atZone(zone.get())
//                .toLocalDateTime();
//        LocalDateTime toTimeToday = LocalTime.parse(to.get())
//                .atDate(LocalDate.now(zone.get()))
//                .atZone(zone.get())
//                .toLocalDateTime();
//        LocalDateTime now = LocalDateTime.now(zone.get());
//        return now.isAfter(fromTimeToday) && now.isBefore(toTimeToday);
//    }
}
