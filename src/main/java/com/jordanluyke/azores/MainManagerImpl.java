package com.jordanluyke.azores;

import com.google.inject.Inject;
import com.jordanluyke.azores.audio.AudioManager;
import com.jordanluyke.azores.web.WebManager;
import io.reactivex.rxjava3.core.Completable;
import lombok.AllArgsConstructor;

@AllArgsConstructor(onConstructor = @__(@Inject))
public class MainManagerImpl implements MainManager {

    private AudioManager audioManager;
    private WebManager webManager;

    @Override
    public Completable start() {
        return audioManager.init()
                .andThen(webManager.init());
    }
}
