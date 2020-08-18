package com.jordanluyke.azores.audio.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jordanluyke.azores.util.NodeUtil;
import com.jsyn.unitgen.SineOscillator;
import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.ZoneId;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
@Setter
public class ToneContext extends AudioContext {
    private static final Logger logger = LogManager.getLogger(ToneContext.class);

    private final AudioType audioType = AudioType.TONE;
    private final SineOscillator oscillator = new SineOscillator();
    private double frequency;

    public ToneContext(double frequency) {
        this.frequency = frequency;
    }

    public ToneContext(double frequency, ZoneId zoneId, String from, String to) {
        this.frequency = frequency;
        this.zoneId = Optional.of(zoneId);
        this.from = Optional.of(from);
        this.to = Optional.of(to);
    }

    @Override
    public ObjectNode getInfo() {
        ObjectNode body = NodeUtil.mapper.createObjectNode();
        body.put("frequency", getFrequency());
        body.put("type", audioType.toString());
        zoneId.ifPresent(z -> body.put("zone", z.toString()));
        from.ifPresent(f -> body.put("from", f));
        to.ifPresent(t -> body.put("to", t));
        return body;
    }
}
