package com.jordanluyke.azores;

import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.web.WebManager;
import io.reactivex.rxjava3.core.Completable;
import lombok.AllArgsConstructor;

import java.time.ZoneId;

/**
 * @author Jordan Luyke <jordanluyke@gmail.com>
 */
@AllArgsConstructor(onConstructor = @__(@Inject))
public class MainManagerImpl implements MainManager {

    private WebManager webManager;
    private AudioManager audioManager;

    @Override
    public Completable start() {
        return Completable.fromSingle(audioManager.setTone(1.45, ZoneId.of("America/Denver"), "10:00", "22:00"))
                .andThen(webManager.start());
    }
}
