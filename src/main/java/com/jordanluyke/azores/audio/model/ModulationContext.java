package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jordanluyke.azores.util.NodeUtil;
import com.jsyn.unitgen.SineOscillator;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.util.Optional;

@Getter
@Setter
public abstract class ModulationContext extends AudioContext {
    private final SineOscillator modulatorOscillator = new SineOscillator();
    private double carrierFrequency;
    private double modulatorFrequency;

    public ModulationContext(double carrierFrequency, double modulatorFrequency) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
    }

    public ModulationContext(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to) {
        this.carrierFrequency = carrierFrequency;
        this.modulatorFrequency = modulatorFrequency;
        this.zoneId = Optional.of(zoneId);
        this.from = Optional.of(from);
        this.to = Optional.of(to);
    }

    public abstract SineOscillator getCarrierOscillator();
    public abstract AudioType getAudioType();

    @Override
    public ObjectNode getInfo() {
        ObjectNode body = NodeUtil.mapper.createObjectNode();
        body.put("carrierFrequency", carrierFrequency);
        body.put("modulatorFrequency", modulatorFrequency);
        body.put("type", getAudioType().toString());
        zoneId.ifPresent(z -> body.put("zone", z.toString()));
        from.ifPresent(f -> body.put("from", f));
        to.ifPresent(t -> body.put("to", t));
        return body;
    }
}
