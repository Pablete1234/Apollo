package com.moonsworth.apollo.network;

import com.moonsworth.apollo.option.type.IconSpecifications;
import com.moonsworth.apollo.option.type.RenderableIcon;
import com.moonsworth.apollo.option.type.RenderableString;
import com.moonsworth.apollo.world.ApolloBlockLocation;
import com.moonsworth.apollo.world.ApolloLocation;
import java.awt.Color;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lunarclient.apollo.utility.BlockLocationMessage;
import lunarclient.apollo.utility.IconSpecificationMessage;
import lunarclient.apollo.utility.LocationMessage;
import lunarclient.apollo.utility.RenderableIconMessage;
import lunarclient.apollo.utility.RenderableStringMessage;
import lunarclient.apollo.utility.Uuid;

// TODO: temp solution?

@UtilityClass
public class NetworkTypes {

    public Uuid toUuid(final UUID object) {
        return Uuid.newBuilder()
            .setHigh64(object.getMostSignificantBits())
            .setLow64(object.getLeastSignificantBits())
            .build();
    }

    public UUID fromUuid(final Uuid message) {
        return new UUID(message.getHigh64(), message.getLow64());
    }

    public com.google.protobuf.Duration toDuration(final Duration object) {
        return com.google.protobuf.Duration.newBuilder()
            .setSeconds(object.getSeconds())
            .setNanos(object.getNano())
            .build();
    }

    public Duration fromDuration(final com.google.protobuf.Duration message) {
        return Duration.ofSeconds(message.getSeconds()).withNanos(message.getNanos());
    }

    public LocationMessage toLocation(final ApolloLocation object) {
        return LocationMessage.newBuilder()
            .setWorld(object.getWorld())
            .setX(object.getX())
            .setY(object.getY())
            .setZ(object.getZ())
            .build();
    }

    public ApolloLocation fromLocation(final LocationMessage message) {
        return ApolloLocation.of(
            message.getWorld(),
            message.getX(),
            message.getY(),
            message.getZ()
        );
    }

    public BlockLocationMessage toBlockLocation(final ApolloBlockLocation object) {
        return BlockLocationMessage.newBuilder()
            .setWorld(object.getWorld())
            .setX(object.getX())
            .setY(object.getY())
            .setZ(object.getZ())
            .build();
    }

    public ApolloBlockLocation fromBlockLocation(final BlockLocationMessage message) {
        return ApolloBlockLocation.of(
            message.getWorld(),
            message.getX(),
            message.getY(),
            message.getZ()
        );
    }

    public IconSpecificationMessage toIconSpecifications(final IconSpecifications object) {
        return IconSpecificationMessage.newBuilder()
            .setWidth(object.getWidth())
            .setHeight(object.getHeight())
            .setMinU(object.getMinU())
            .setMaxU(object.getMaxU())
            .setMinV(object.getMinV())
            .setMaxV(object.getMaxV())
            .build();
    }

    public IconSpecifications fromIconSpecifications(final IconSpecificationMessage message) {
        return IconSpecifications.of(
            message.getWidth(),
            message.getHeight(),
            message.getMinU(),
            message.getMaxU(),
            message.getMinV(),
            message.getMaxV()
        );
    }

    public RenderableIconMessage toRenderableIcon(final RenderableIcon icon) {
        return RenderableIconMessage.newBuilder()
            .setResourceLocation(icon.getResourceLocation())
            .setSize(icon.getSize())
            .setSpecification(NetworkTypes.toIconSpecifications(icon.getSpecifications()))
            .build();
    }

    public RenderableIcon fromRenderableIcon(final RenderableIconMessage icon) {
        return RenderableIcon.of(
            icon.getResourceLocation(),
            icon.getSize(),
            NetworkTypes.fromIconSpecifications(icon.getSpecification())
        );
    }

    public RenderableStringMessage toRenderableString(final RenderableString object) {
        final List<RenderableStringMessage.TextDecorators> decorators = object.getDecorators()
            .stream().map(decorator -> RenderableStringMessage.TextDecorators.valueOf(decorator.name()))
            .collect(Collectors.toList());

        final List<RenderableStringMessage> children = object.getChildren()
            .stream().map(NetworkTypes::toRenderableString)
            .collect(Collectors.toList());

        return RenderableStringMessage.newBuilder()
            .setContent(object.getContent())
            .setColor(object.getColor().getRGB())
            .addAllDecoration(decorators)
            .addAllChildren(children)
            .build();
    }

    public RenderableString fromRenderableString(final RenderableStringMessage message) {
        final List<RenderableString.TextDecorators> decorators = message.getDecorationList()
            .stream().map(decorator -> RenderableString.TextDecorators.valueOf(decorator.name()))
            .collect(Collectors.toList());

        final List<RenderableString> children = message.getChildrenList()
            .stream().map(NetworkTypes::fromRenderableString)
            .collect(Collectors.toList());

        return RenderableString.of(
            message.getContent(),
            new Color(message.getColor()),
            decorators,
            children
        );
    }
}
