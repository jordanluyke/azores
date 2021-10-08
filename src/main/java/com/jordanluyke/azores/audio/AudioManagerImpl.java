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
import io.reactivex.rxjava3.disposables.Disposable;
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

    private List<AudioContext> audioContexts = new ArrayList<>();
    private Disposable updateInterval = null;
    private boolean isEnabled = false;

    @Override
    public Completable init() {
        return Completable.defer(() -> {
            synth.add(lineOut);
            updateInterval = createUpdateInterval();
            return Completable.complete();
        });
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
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
                    if((frequencyRequest.getFrom().isPresent() || frequencyRequest.getTo().isPresent() || frequencyRequest.getZoneId().isPresent()) &&
                            (frequencyRequest.getFrom().isEmpty() || frequencyRequest.getTo().isEmpty() || frequencyRequest.getZoneId().isEmpty()))
                        throw new WebException(HttpResponseStatus.BAD_REQUEST, "Must include all or none: from, to, and zoneId");
                    if(frequencyRequest.getFrequencyType() == FrequencyType.TONE) {
                        if(frequencyRequest.getFrequency().isEmpty())
                            throw new FieldRequiredException("frequency");
                        return new ToneContext(frequencyRequest.getFrequency().get(), frequencyRequest.getWaveType(), frequencyRequest.getFrom(), frequencyRequest.getTo(), frequencyRequest.getZoneId());
                    }
                    if(frequencyRequest.getFrequencyType() == FrequencyType.AM || frequencyRequest.getFrequencyType() == FrequencyType.FM) {
                        if(frequencyRequest.getCarrierFrequency().isEmpty())
                            throw new FieldRequiredException("carrierFrequency");
                        if(frequencyRequest.getModulatorFrequency().isEmpty())
                            throw new FieldRequiredException("modulatorFrequency");
                        if(frequencyRequest.getFrequencyType() == FrequencyType.AM)
                            return new AmplitudeModulationContext(frequencyRequest.getCarrierFrequency().get(), frequencyRequest.getModulatorFrequency().get(), frequencyRequest.getFrom(), frequencyRequest.getTo(), frequencyRequest.getZoneId());
                        return new FrequencyModulationContext(frequencyRequest.getCarrierFrequency().get(), frequencyRequest.getModulatorFrequency().get(), frequencyRequest.getFrom(), frequencyRequest.getTo(), frequencyRequest.getZoneId());
                    }
                    throw new WebException(HttpResponseStatus.BAD_REQUEST, "frequencyType must be one of: TONE/AM/FM");
                })
                .toList()
                .doOnSuccess(contexts -> {
                    audioContexts = contexts;
                })
                .flatMap(contexts -> startFrequencies())
                .flatMapObservable(Observable::fromIterable);
    }


    @Override
    public Single<List<AudioContext>> startFrequencies() {
        return Single.defer(() -> {
            if(isEnabled)
                return Single.just(audioContexts);
            return Observable.fromIterable(audioContexts)
                    .doOnNext(context -> {
                        if(!context.isActive() && (!context.isTimeframeSet() || (context.isTimeframeSet() && context.isWithinTimeframe())))
                            startContext(context);
                    })
                    .toList()
                    .doOnSuccess(Void -> {
                        synth.start();
                        lineOut.start();
                        isEnabled = true;
                    });
        });
    }

    @Override
    public Single<List<AudioContext>> stopFrequencies() {
        return Single.defer(() -> {
            if(!isEnabled)
                return Single.just(audioContexts);
            return Observable.fromIterable(audioContexts)
                    .doOnNext(context -> {
                        if(context.isActive()) {
                            stopContext(context);
                        }
                    })
                    .toList()
                    .doOnSuccess(Void -> {
                        synth.stop();
                        lineOut.stop();
                        isEnabled = false;
                    });
        });
    }

    private void startContext(AudioContext context) {
        if(!context.isActive()) {
            if(context.getFrequencyType() == FrequencyType.TONE) {
                ToneContext toneContext = (ToneContext) context;
                synth.add(toneContext.getOscillator());
                toneContext.getOscillator().output.connect(0, lineOut.input, 0);
                toneContext.getOscillator().output.connect(0, lineOut.input, 1);
                toneContext.getOscillator().amplitude.set(1);
                toneContext.getOscillator().frequency.set(toneContext.getFrequency());
                toneContext.getOscillator().start();
            } else if(context.getFrequencyType() == FrequencyType.AM || context.getFrequencyType() == FrequencyType.FM) {
                ModulationContext modulationContext = (ModulationContext) context;
                synth.add(modulationContext.getCarrierOscillator());
                synth.add(modulationContext.getModulatorOscillator());
                modulationContext.getCarrierOscillator().amplitude.set(1);
                modulationContext.getCarrierOscillator().frequency.set(modulationContext.getCarrierFrequency());
                modulationContext.getModulatorOscillator().amplitude.set(1);
                if(context.getFrequencyType() == FrequencyType.AM) {
                    modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency() / 2.0);
                    AmplitudeModulationContext amContext = (AmplitudeModulationContext) context;
                    amContext.getCarrierOscillator().output.connect(amContext.getModulatorOscillator().amplitude);
                    amContext.getModulatorOscillator().output.connect(0, lineOut.input, 0);
                    amContext.getModulatorOscillator().output.connect(0, lineOut.input, 1);
                } else if(context.getFrequencyType() == FrequencyType.FM) {
                    modulationContext.getModulatorOscillator().frequency.set(modulationContext.getModulatorFrequency());
                    FrequencyModulationContext fmContext = (FrequencyModulationContext) context;
                    fmContext.getModulatorOscillator().output.connect(fmContext.getCarrierOscillator().modulation);
                    fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 0);
                    fmContext.getCarrierOscillator().output.connect(0, lineOut.input, 1);
                }
                modulationContext.getCarrierOscillator().start();
                modulationContext.getModulatorOscillator().start();
            }
            context.setActive(true);
        }
    }

    private void stopContext(AudioContext context) {
        if(context.isActive()) {
            if(context.getFrequencyType() == FrequencyType.TONE) {
                ToneContext toneContext = (ToneContext) context;
                toneContext.getOscillator().stop();
                toneContext.getOscillator().output.disconnectAll();
                synth.remove(toneContext.getOscillator());
            } else if(context.getFrequencyType() == FrequencyType.AM || context.getFrequencyType() == FrequencyType.FM) {
                ModulationContext modulationContext = (ModulationContext) context;
                modulationContext.getModulatorOscillator().stop();
                modulationContext.getModulatorOscillator().output.disconnectAll();
                modulationContext.getCarrierOscillator().stop();
                modulationContext.getCarrierOscillator().output.disconnectAll();
                synth.remove(modulationContext.getCarrierOscillator());
                synth.remove(modulationContext.getModulatorOscillator());
            }
            context.setActive(false);
        }
    }

    private Disposable createUpdateInterval() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .filter(Void -> isEnabled)
                .flatMap(Void -> Observable.fromIterable(audioContexts)
                        .filter(AudioContext::isTimeframeSet)
                        .doOnNext(context -> {
                            if(!context.isActive() && context.isWithinTimeframe())
                                startContext(context);
                            if(context.isActive() && !context.isWithinTimeframe())
                                stopContext(context);
                        })
                )
                .subscribe(Void -> {}, err -> logger.error(err.getMessage()));
    }

//    private void clearDisposable() {
//        if(timePeriodDisposable.isPresent()) {
//            timePeriodDisposable.get().dispose();
//            timePeriodDisposable = Optional.empty();
//        }
//    }
}
