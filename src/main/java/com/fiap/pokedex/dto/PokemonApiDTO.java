package com.fiap.pokedex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PokemonApiDTO(
        int id,
        String name,

        int height,
        int weight,

        @JsonProperty("base_experience")
        int experienciaBase,

        List<StatDTO> stats,
        List<TypeSlotDTO> types,
        List<AbilitySlotDTO> abilities,

        SpritesDTO sprites
) {

    public record StatDTO(
            int base_stat,
            StatInfoDTO stat
    ) {}

    public record StatInfoDTO(
            String name
    ) {}

    public record TypeSlotDTO(
            int slot,
            TypeInfoDTO type
    ) {}

    public record TypeInfoDTO(
            String name
    ) {}

    public record AbilitySlotDTO(
            boolean is_hidden,
            int slot,
            AbilityInfoDTO ability
    ) {}

    public record AbilityInfoDTO(
            String name,
            String url
    ) {}

    public record SpritesDTO(
            OtherSpritesDTO other
    ) {}

    public record OtherSpritesDTO(
            @JsonProperty("official-artwork")
            OfficialArtworkDTO officialArtwork
    ) {}

    public record OfficialArtworkDTO(
            @JsonProperty("front_default")
            String imagem
    ) {}
}