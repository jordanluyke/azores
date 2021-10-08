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
    protected boolean isActive = false;
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<LocalTime> from = Optional.empty();
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<LocalTime> to = Optional.empty();
    @JsonInclude(JsonInclude.Include.NON_EMPTY) protected Optional<ZoneId> zoneId = Optional.empty();

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

    @JsonIgnore
    public boolean isTimeframeSet() {
        return from.isPresent() && to.isPresent() && zoneId.isPresent();
    }

    @JsonIgnore
    public boolean isWithinTimeframe() {
        if(!isTimeframeSet())
            throw new RuntimeException("Timeframe not set");
        LocalDateTime fromTimeToday = from.get()
                .atDate(LocalDate.now(zoneId.get()))
                .atZone(zoneId.get())
                .toLocalDateTime();
        LocalDateTime toTimeToday = to.get()
                .atDate(LocalDate.now(zoneId.get()))
                .atZone(zoneId.get())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now(zoneId.get());
        if(from.get().isAfter(to.get()))
            return now.isAfter(fromTimeToday) || now.isBefore(toTimeToday);
        return now.isAfter(fromTimeToday) && now.isBefore(toTimeToday);
    }
}
