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
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new ToneContext(frequency);
                })
                .flatMap(Void -> startContext());
    }

    @Override
    public Single<AudioContext> setTone(double frequency, String from, String to, ZoneId zoneId) {
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new ToneContext(frequency, from, to, zoneId);
                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
                })
                .flatMap(Void -> {
                    if(audioContext.isWithinTimeframe())
                        return startContext();
                    return Single.just(audioContext);
                });
    }

    @Override
    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency) {
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency);
                })
                .flatMap(Void -> startContext());
    }

    @Override
    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency, from, to, zoneId);
                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
                })
                .flatMap(Void -> {
                    if(audioContext.isWithinTimeframe())
                        return startContext();
                    return Single.just(audioContext);
                });
    }

    @Override
    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency) {
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency);
                })
                .flatMap(Void -> startContext());
    }

    @Override
    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
        return stopContext()
                .doOnSuccess(Void -> {
                    clearDisposable();
                    audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency, from, to, zoneId);
                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
                })
                .flatMap(Void -> {
                    if(audioContext.isWithinTimeframe())
                        return startContext();
                    return Single.just(audioContext);
                });
    }

    @Override
    public Single<AudioContext> enable() {
        return Single.defer(() -> {
            audioContext.setEnabled(true);
            if(!audioContext.isTimeframeSet() || (audioContext.isTimeframeSet() && audioContext.isWithinTimeframe()))
                return startContext();
            return Single.just(audioContext);
        });
    }

    @Override
    public Single<AudioContext> disable() {
        return Single.defer(() -> {
            audioContext.setEnabled(false);
            return stopContext();
        });
    }

    private Single<AudioContext> startContext() {
        return Single.defer(() -> {
            if(!audioContext.isOn()) {
                audioContext.setOn(true);
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
            }
            return Single.just(audioContext);
        });
    }

    private Single<AudioContext> stopContext() {
        return Single.defer(() -> {
            if(audioContext.isOn()) {
                audioContext.setOn(false);
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

    private Disposable createTimeframeDisposable() {
        if(!audioContext.isTimeframeSet())
            throw new RuntimeException("zone/from/to empty");
        return Observable.interval(intervalPeriod, intervalUnit)
                .flatMapSingle(Void -> {
                    if(audioContext.isEnabled()) {
                        if(audioContext.isWithinTimeframe())
                            return startContext();
                        return stopContext();
                    }
                    return Single.just(audioContext);
                })
                .subscribe(Void -> {}, err -> logger.error(err.getMessage()));
    }
}
