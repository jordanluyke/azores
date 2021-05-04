package com.jordanluyke.azores.audio;

import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.audio.model.AudioContext;
import com.jordanluyke.azores.audio.model.FrequencyType;
import com.jordanluyke.azores.audio.model.AmplitudeModulationContext;
import com.jordanluyke.azores.audio.model.FrequencyModulationContext;
import com.jordanluyke.azores.audio.model.ModulationContext;
import com.jordanluyke.azores.audio.model.ToneContext;
import com.jordanluyke.azores.web.model.FieldRequiredException;
import com.jordanluyke.azores.web.model.WebException;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
public class AudioManagerImpl implements AudioManager {
    private static final Logger logger = LogManager.getLogger(AudioManager.class);
    private static final Synthesizer synth = JSyn.createSynthesizer();
    private static final LineOut lineOut = new LineOut();
//    private static final int intervalPeriod = 1;
//    private static final TimeUnit intervalUnit = TimeUnit.SECONDS;

//    private Optional<Disposable> timePeriodDisposable = Optional.empty();
    private List<AudioContext> audioContexts = new ArrayList<>();
    private boolean isActive = false;
//    private boolean isEnabled = true;

    @Override
    public Completable init() {
        return Completable.defer(() -> {
            synth.add(lineOut);
            return Completable.complete();
        });
    }

    @Override
    public Observable<? extends AudioContext> getFrequencies() {
        return Observable.fromIterable(audioContexts);
    }

    @Override
    public Observable<? extends AudioContext> setFrequencies(FrequenciesRequest request) {
        return stopFrequencies()
                .flatMapObservable(Void -> Observable.fromIterable(request.getFrequencies()))
                .map(frequencyRequest -> {
                    if(frequencyRequest.getFrequencyType() == FrequencyType.TONE) {
                        if(frequencyRequest.getFrequency().isEmpty())
                            throw new FieldRequiredException("frequency");
                        if(frequencyRequest.getWaveType().isEmpty())
                            throw new FieldRequiredException("waveType");
                        return new ToneContext(frequencyRequest.getFrequency().get(), frequencyRequest.getWaveType().get());
                    }
                    if(frequencyRequest.getFrequencyType() == FrequencyType.AM || frequencyRequest.getFrequencyType() == FrequencyType.FM) {
                        if(frequencyRequest.getCarrierFrequency().isEmpty())
                            throw new FieldRequiredException("carrierFrequency");
                        if(frequencyRequest.getModulatorFrequency().isEmpty())
                            throw new FieldRequiredException("modulatorFrequency");
                        if(frequencyRequest.getFrequencyType() == FrequencyType.AM)
                            return new AmplitudeModulationContext(frequencyRequest.getCarrierFrequency().get(), frequencyRequest.getModulatorFrequency().get());
                        return new FrequencyModulationContext(frequencyRequest.getCarrierFrequency().get(), frequencyRequest.getModulatorFrequency().get());
                    }
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "frequencyType must be one of: TONE/AM/FM");
                })
                .toList()
                .doOnSuccess(contexts -> {
                    isActive = false;
                    audioContexts = contexts;
                })
                .flatMap(contexts -> startFrequencies())
                .flatMapObservable(Observable::fromIterable);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

//    @Override
//    public boolean isEnabled() {
//        return isEnabled;
//    }

    //    @Override
//    public Single<AudioContext> setTone(double frequency) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new ToneContext(frequency);
//                })
//                .flatMap(Void -> startContext());
//    }
//
//    @Override
//    public Single<AudioContext> setTone(double frequency, String from, String to, ZoneId zoneId) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new ToneContext(frequency, from, to, zoneId);
//                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
//                })
//                .flatMap(Void -> {
//                    if(audioContext.isWithinTimeframe())
//                        return startContext();
//                    return Single.just(audioContext);
//                });
//    }
//
//    @Override
//    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency);
//                })
//                .flatMap(Void -> startContext());
//    }
//
//    @Override
//    public Single<AudioContext> setAM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new AmplitudeModulationContext(carrierFrequency, modulatorFrequency, from, to, zoneId);
//                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
//                })
//                .flatMap(Void -> {
//                    if(audioContext.isWithinTimeframe())
//                        return startContext();
//                    return Single.just(audioContext);
//                });
//    }
//
//    @Override
//    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency);
//                })
//                .flatMap(Void -> startContext());
//    }
//
//    @Override
//    public Single<AudioContext> setFM(double carrierFrequency, double modulatorFrequency, String from, String to, ZoneId zoneId) {
//        return stopContext()
//                .doOnSuccess(Void -> {
//                    clearDisposable();
//                    audioContext = new FrequencyModulationContext(carrierFrequency, modulatorFrequency, from, to, zoneId);
//                    timePeriodDisposable = Optional.of(createTimeframeDisposable());
//                })
//                .flatMap(Void -> {
//                    if(audioContext.isWithinTimeframe())
//                        return startContext();
//                    return Single.just(audioContext);
//                });
//    }

//    @Override
//    public Single<AudioContext> enable() {
//        return Single.defer(() -> {
//            isEnabled = true;
//            if(!audioContext.isTimeframeSet() || (audioContext.isTimeframeSet() && audioContext.isWithinTimeframe()))
//                return startContext();
//            return Single.just(audioContext);
//        });
//    }
//
//    @Override
//    public Single<AudioContext> disable() {
//        return Single.defer(() -> {
//            audioContext.setEnabled(false);
//            return stopContext();
//        });
//    }

    public Single<List<AudioContext>> startFrequencies() {
        return Observable.fromIterable(audioContexts)
                .doOnNext(audioContext -> {
                    if(!isActive) {
                        if(audioContext.getFrequencyType() == FrequencyType.TONE) {
                            ToneContext toneContext = (ToneContext) audioContext;
                            synth.add(toneContext.getOscillator());
                            toneContext.getOscillator().output.connect(0, lineOut.input, 0);
                            toneContext.getOscillator().output.connect(0, lineOut.input, 1);
                            toneContext.getOscillator().amplitude.set(1);
                            toneContext.getOscillator().frequency.set(toneContext.getFrequency());
                            toneContext.getOscillator().start();
                        } else if(audioContext.getFrequencyType() == FrequencyType.AM || audioContext.getFrequencyType() == FrequencyType.FM) {
                            ModulationContext modulationContext = (ModulationContext) audioContext;
                            synth.add(modulationContext.getCarrierOscillator());
                            synth.add(modulationContext.getModulatorOscillator());
                            modulationContext.getCarrierOscillator().amplitude.set(1);
                            modulationContext.getCarrierOscillator().frequency.set(modulationContext.getCarrierFrequency());
                            modulationContext.getModulatorOscillator().amplitude.set(1);
                            if(audioContext.getFrequencyType() == FrequencyType.AM) {
                                modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency() / 2.0);
                                AmplitudeModulationContext amContext = (AmplitudeModulationContext) audioContext;
                                amContext.getCarrierOscillator().output.connect(amContext.getModulatorOscillator().amplitude);
                                amContext.getModulatorOscillator().output.connect(0, lineOut.input, 0);
                                amContext.getModulatorOscillator().output.connect(0, lineOut.input, 1);
                            } else if(audioContext.getFrequencyType() == FrequencyType.FM) {
                                modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency());
                                FrequencyModulationContext fmContext = (FrequencyModulationContext) audioContext;
                                fmContext.getModulatorOscillator().output.connect(fmContext.getCarrierOscillator().modulation);
                                fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 0);
                                fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 1);
                            }
                            modulationContext.getCarrierOscillator().start();
                            modulationContext.getModulatorOscillator().start();
                        }
                    }
                })
                .toList()
                .doOnSuccess(contexts -> {
                    if(!contexts.isEmpty() && !isActive) {
                        synth.start();
                        lineOut.start();
                        isActive = true;
                    }
                });
    }

    public Single<List<AudioContext>> stopFrequencies() {
        return Observable.defer(() -> {
            if(isActive) {
                synth.stop();
                lineOut.stop();
            }
            return Observable.fromIterable(audioContexts);
        })
                .doOnNext(audioContext -> {
                    if(isActive) {
                        if(audioContext.getFrequencyType() == FrequencyType.TONE) {
                            ToneContext toneContext = (ToneContext) audioContext;
                            toneContext.getOscillator().stop();
                            toneContext.getOscillator().output.disconnectAll();
                            synth.remove(toneContext.getOscillator());
                        } else if(audioContext.getFrequencyType() == FrequencyType.AM || audioContext.getFrequencyType() == FrequencyType.FM) {
                            ModulationContext modulationContext = (ModulationContext) audioContext;
                            modulationContext.getModulatorOscillator().stop();
                            modulationContext.getModulatorOscillator().output.disconnectAll();
                            modulationContext.getCarrierOscillator().stop();
                            modulationContext.getCarrierOscillator().output.disconnectAll();
                            synth.remove(modulationContext.getCarrierOscillator());
                            synth.remove(modulationContext.getModulatorOscillator());
                        }
                    }
                })
                .toList()
                .doOnSuccess(contexts -> {
                    if(isActive)
                        isActive = false;
                });
    }

//    private void clearDisposable() {
//        if(timePeriodDisposable.isPresent()) {
//            timePeriodDisposable.get().dispose();
//            timePeriodDisposable = Optional.empty();
//        }
//    }
//
//    private Disposable createTimeframeDisposable() {
////        if(!audioContext.isTimeframeSet())
////            throw new RuntimeException("zone/from/to empty");
//        return Observable.interval(intervalPeriod, intervalUnit)
//                .flatMapSingle(Void -> {
//                    if(isEnabled) {
//                        if(audioContext.isWithinTimeframe())
//                            return startContext();
//                        return stopContext();
//                    }
//                    return Single.just(audioContext);
//                })
//                .subscribe(Void -> {}, err -> logger.error(err.getMessage()));
//    }
}
