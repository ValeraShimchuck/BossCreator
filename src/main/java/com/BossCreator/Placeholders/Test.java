package com.BossCreator.Placeholders;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

import java.time.Instant;

public class Test implements PlaceholderReplacer {

    @Override
    public String update() {
        return String.valueOf(Instant.now().getEpochSecond());
    }
}
