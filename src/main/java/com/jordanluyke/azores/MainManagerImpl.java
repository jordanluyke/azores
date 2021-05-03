package com.jordanluyke.azores;

import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.audio.dto.FrequenciesRequest;
import com.jordanluyke.azores.web.WebManager;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import lombok.AllArgsConstructor;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class MainManagerImpl implements MainManager {

    private WebManager webManager;
    private AudioManager audioManager;

    @Override
    public Completable start() {
        return Observable.defer(() -> {
            var freqencyReq1 = new FrequenciesRequest.FrequencyRequest();
            freqencyReq1.setTone(Optional.of(144d));
            var freqencyReq2 = new FrequenciesRequest.FrequencyRequest();
            freqencyReq2.setTone(Optional.of(145d));
            var frequenciesRequest = new FrequenciesRequest(List.of(freqencyReq1, freqencyReq2));
            return audioManager.setFrequencies(frequenciesRequest);
        })
                .toList()
                .flatMapCompletable(Void -> Completable.complete())
//        }).fromSingle(audioManager.setFrequencies(new ).setTone(1.45, "10:00", "22:00", ZoneId.of("America/Denver")))
                .andThen(webManager.start());
    }
}
