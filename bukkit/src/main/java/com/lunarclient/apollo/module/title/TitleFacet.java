/*
 * This file is part of Apollo, licensed under the MIT License.
 *
 * Copyright (c) 2023 Moonsworth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.lunarclient.apollo.module.title;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.viaversion.viaversion.api.Via;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.Ticks;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.kyori.adventure.title.Title.DEFAULT_TIMES;

/**
 * Adds support for 1.7 client lunar titles to adventure
 *
 * @since 1.2.0
 */
@SuppressWarnings("UnstableApiUsage")
public class TitleFacet implements net.kyori.adventure.platform.facet.Facet.Title<Player, Component, TitleFacet.TitleContainer, TitleFacet.TitleContainer> {
    private static final int VERSION_1_7_10 = 10;
    private static final TitleModule TITLE_MODULE = Apollo.getModuleManager().getModule(TitleModule.class);

    private final Map<Player, net.kyori.adventure.title.Title.Times> PLAYER_TIMES = new WeakHashMap<>();

    @Override
    public boolean isApplicable(@NotNull Player viewer) {
        return Via.getAPI().getPlayerVersion(viewer.getUniqueId()) <= VERSION_1_7_10;
    }

    @Override
    public @NotNull TitleContainer createTitleCollection() {
        return new TitleContainer();
    }

    @Override
    public void contributeTitle(@NotNull TitleContainer coll, @NotNull Component title) {
        coll.title = title;
    }

    @Override
    public void contributeSubtitle(@NotNull TitleContainer coll, @NotNull Component subtitle) {
        coll.subtitle = subtitle;
    }

    @Override
    public void contributeTimes(@NotNull TitleContainer coll, int inTicks, int stayTicks, int outTicks) {
        coll.times = net.kyori.adventure.title.Title.Times.times(Ticks.duration(inTicks), Ticks.duration(stayTicks), Ticks.duration(outTicks));
    }

    @Override
    public TitleContainer completeTitle(@NotNull TitleContainer coll) {
        return coll;
    }

    @Override
    public void showTitle(@NotNull Player viewer, @NotNull TitleContainer title) {
        BukkitApollo.runForPlayer(viewer, p -> {
            net.kyori.adventure.title.Title.Times times = title.times;
            if (times != null) this.PLAYER_TIMES.put(viewer, times);
            else times = this.PLAYER_TIMES.getOrDefault(viewer, DEFAULT_TIMES);

            if (title.title != null) TITLE_MODULE.displayTitle(p, new com.lunarclient.apollo.module.title.Title(
                    TitleType.TITLE, title.title, 1f,
                    times.fadeIn(), times.stay(), times.fadeOut(),
                    0, 0));

            if (title.subtitle != null) TITLE_MODULE.displayTitle(p, new com.lunarclient.apollo.module.title.Title(
                    TitleType.SUBTITLE, title.subtitle, 1f,
                    times.fadeIn(), times.stay(), times.fadeOut(),
                    0, 0));
        });
    }

    @Override
    public void clearTitle(@NotNull Player viewer) {
        this.showTitle(viewer, new TitleContainer(Component.empty(), Component.empty(), null));
    }

    @Override
    public void resetTitle(@NotNull Player viewer) {
        this.showTitle(viewer, new TitleContainer(Component.empty(), Component.empty(), DEFAULT_TIMES));
    }

    @Override
    public @Nullable Component createMessage(@NotNull Player viewer, @NotNull Component message) {
        return message;
    }

    /**
     * Container for title & subtitle.
     *
     * @since 1.2.0
     */
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TitleContainer {
        private Component title;
        private Component subtitle;
        private net.kyori.adventure.title.Title.Times times;
    }

}
