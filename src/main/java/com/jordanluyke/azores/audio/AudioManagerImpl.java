package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.AudioType;
import com.jordanluyke.azores.audio.model.AmplitudeModulationContext;
import com.jordanluyke.azores.audio.model.FrequencyModulationContext;
import com.jordanluyke.azores.audio.model.ModulationContext;
import com.jordanluyke.azores.audio.model.ToneContext;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@Getter
public class AudioManagerImpl implements AudioManager {
    private static final Logger logger = LogManager.getLogger(AudioManager.class);

    private static final Synthesizer synth = JSyn.createSynthesizer();
    private static final LineOut lineOut = new LineOut();
    private static final int intervalPeriod = 1;
    private static final TimeUnit intervalUnit = TimeUnit.SECONDS;
    public final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private Optional<Disposable> timePeriodDisposable = Optional.empty();
    private AudioContext audioContext = new ToneContext(1.45);

    public AudioManagerImpl() {
        synth.add(lineOut);
    }

    @Override
    public Single<AudioContext> setTone(double frequency) {
        return stop()
                .doOnSuccess(Void -> audioContext = new ToneContext(frequency))
                .flatMap(Void -> start());
    }

    @Override
    public Single<AudioContext> setTone(double frequency, ZoneId zoneId, String from, String to) {
        return stop()
                .doOnSuccess(a -> logger.info("1 {}", a))
                .doOnSuccess(Void -> {
                    audioContext = new ToneContext(frequency, zoneId, from, to);
                    timePeriodDisposable = Optional.of(createTimePeriodDisposable());
                })
                .doOnSuccess(a -> logger.info("2 {}", a))
                .flatMap(Void -> start())
                .doOnSuccess(a -> logger.info("3 {}", a));
    }

    @Override
    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency) {
        return stop()
                .doOnSuccess(Void -> audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency))
                .flatMap(Void -> start());
    }

    @Override
    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to) {
        return stop()
                .doOnSuccess(Void -> {
                    audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency, zoneId, from, to);
                    timePeriodDisposable = Optional.of(createTimePeriodDisposable());
                })
                .flatMap(Void -> start());
    }

    @Override
    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency) {
        return stop()
                .doOnSuccess(Void -> audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency))
                .flatMap(Void -> start());
    }

    @Override
    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency, ZoneId zoneId, String from, String to) {
        return stop()
                .doOnSuccess(Void -> {
                    audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency, zoneId, from, to);
                    timePeriodDisposable = Optional.of(createTimePeriodDisposable());
                })
                .flatMap(Void -> start());
    }

    @Override
    public Single<AudioContext> start() {
        return Single.defer(() -> {
            if(!audioContext.isOn()) {
                if(audioContext.getType() == AudioType.TONE) {
                    ToneContext toneContext = (ToneContext) audioContext;
                    synth.add(toneContext.getOscillator());
                    toneContext.getOscillator().output.connect(0, lineOut.input, 0);
                    toneContext.getOscillator().output.connect(0, lineOut.input, 1);
                    toneContext.getOscillator().amplitude.set(1);
                    toneContext.getOscillator().frequency.set(toneContext.getFrequency());
                    toneContext.getOscillator().start();
                } else if(audioContext.getType() == AudioType.AM || audioContext.getType() == AudioType.FM) {
                    ModulationContext modulationContext = (ModulationContext) audioContext;
                    synth.add(modulationContext.getCarrierOscillator());
                    synth.add(modulationContext.getModulatorOscillator());
                    modulationContext.getCarrierOscillator().amplitude.set(1);
                    modulationContext.getCarrierOscillator().frequency.set(modulationContext.getCarrierFrequency());
                    modulationContext.getModulatorOscillator().amplitude.set(1);
                    if(audioContext.getType() == AudioType.AM) {
                        modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency() / 2.0);
                        AmplitudeModulationContext amContext = (AmplitudeModulationContext) audioContext;
                        amContext.getCarrierOscillator().output.connect(amContext.getModulatorOscillator().amplitude);
                        amContext.getModulatorOscillator().output.connect(0, lineOut.input, 0);
                        amContext.getModulatorOscillator().output.connect(0, lineOut.input, 1);
                    } else if(audioContext.getType() == AudioType.FM) {
                        modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency());
                        FrequencyModulationContext fmContext = (FrequencyModulationContext) audioContext;
                        fmContext.getModulatorOscillator().output.connect(fmContext.getCarrierOscillator().modulation);
                        fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 0);
                        fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 1);
                    }
                    modulationContext.getCarrierOscillator().start();
                    modulationContext.getModulatorOscillator().start();
                }

                synth.start();
                lineOut.start();
                audioContext.setOn(true);
            }
            return Single.just(audioContext);
        });
    }

    @Override
    public Single<AudioContext> stop() {
        return Single.defer(() -> {
            clearDisposable();
            if(audioContext.isOn()) {
                synth.stop();
                lineOut.stop();
                if(audioContext.getType() == AudioType.TONE) {
                    ToneContext toneContext = (ToneContext) audioContext;
                    toneContext.getOscillator().stop();
                    toneContext.getOscillator().output.disconnectAll();
                    synth.remove(toneContext.getOscillator());
                } else if(audioContext.getType() == AudioType.AM || audioContext.getType() == AudioType.FM) {
                    ModulationContext modulationContext = (ModulationContext) audioContext;
                    modulationContext.getModulatorOscillator().stop();
                    modulationContext.getModulatorOscillator().output.disconnectAll();
                    modulationContext.getCarrierOscillator().stop();
                    modulationContext.getCarrierOscillator().output.disconnectAll();
                    synth.remove(modulationContext.getCarrierOscillator());
                    synth.remove(modulationContext.getModulatorOscillator());
                }
                audioContext.setOn(false);
            }
            return Single.just(audioContext);
        });
    }

    private void clearDisposable() {
        if(timePeriodDisposable.isPresent()) {
            timePeriodDisposable.get().dispose();
            timePeriodDisposable = Optional.empty();
        }
    }

    private Disposable createTimePeriodDisposable() {
        if(audioContext.getZone().isEmpty() || audioContext.getFrom().isEmpty() || audioContext.getTo().isEmpty())
            throw new RuntimeException("zone/from/to empty");
        return Observable.interval(0, intervalPeriod, intervalUnit)
                .doOnNext(Void -> {
                    if(isWithinTimePeriod(audioContext.getZone().get(), audioContext.getFrom().get(), audioContext.getTo().get())) {
                        if(!audioContext.isOn())
                            start();
                    } else {
                        if(audioContext.isOn())
                            stop();
                    }
                })
                .subscribe(Void -> {}, err -> logger.error(err.getMessage()));
    }

    private boolean isWithinTimePeriod(ZoneId zoneId, String from, String to) {
        LocalDateTime fromTimeToday = LocalTime.parse(from)
                .atDate(LocalDate.now(zoneId))
                .atZone(zoneId)
                .toLocalDateTime();
        LocalDateTime toTimeToday = LocalTime.parse(to)
                .atDate(LocalDate.now(zoneId))
                .atZone(zoneId)
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now(zoneId);
        return now.isAfter(fromTimeToday) && now.isBefore(toTimeToday);
    }
}
