package com.fiap.pokedex.mapper;

import com.fiap.pokedex.dto.PokemonApiDTO;
import com.fiap.pokedex.dto.PokemonDTO;
import com.fiap.pokedex.enums.TipoEnum;
import com.fiap.pokedex.models.Habilidade;
import com.fiap.pokedex.models.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class PokemonMapper {

    public static PokemonDTO paraDTO(Pokemon pokemon) {
        return new PokemonDTO(
                pokemon.getId(),
                pokemon.getNome(),
                pokemon.getAltura(),
                pokemon.getPeso(),
                pokemon.getExperienciaBase(),
                pokemon.getHp(),
                pokemon.getAtaque(),
                pokemon.getDefesa(),
                pokemon.getAtaqueEspecial(),
                pokemon.getDefesaEspecial(),
                pokemon.getVelocidade(),
                pokemon.getSpriteUrl(),
                pokemon.getTipos()
        );
    }

    public static Pokemon paraModel(PokemonDTO dto) {
        return new Pokemon(
                dto.id(),
                dto.nome(),
                dto.altura(),
                dto.peso(),
                dto.experienciaBase(),
                dto.hp(),
                dto.ataque(),
                dto.defesa(),
                dto.ataqueEspecial(),
                dto.defesaEspecial(),
                dto.velocidade(),
                dto.spriteUrl(),
                dto.tipos(),
                new ArrayList<>() // o PokemonDTO não carrega habilidades
        );
    }

    public static Pokemon daApiParaModel(PokemonApiDTO dto) {

        int hp = 0;
        int ataque = 0;
        int defesa = 0;
        int ataqueEspecial = 0;
        int defesaEspecial = 0;
        int velocidade = 0;

        for (var stat : dto.stats()) {
            switch (stat.stat().name()) {
                case "hp" -> hp = stat.base_stat();
                case "attack" -> ataque = stat.base_stat();
                case "defense" -> defesa = stat.base_stat();
                case "special-attack" -> ataqueEspecial = stat.base_stat();
                case "special-defense" -> defesaEspecial = stat.base_stat();
                case "speed" -> velocidade = stat.base_stat();
            }
        }

        List<TipoEnum> tipos = dto.types().stream()
                .map(tipo -> TipoEnum.valueOf(
                        tipo.type().name().toUpperCase()
                ))
                .toList();

        List<Habilidade> habilidades = dto.abilities().stream()
                .map(a -> new Habilidade(
                        extrairIdDaUrl(a.ability().url()),
                        a.ability().name(),
                        a.is_hidden()
                ))
                .toList();

        String imagem = dto.sprites()
                .other()
                .officialArtwork()
                .imagem();

        return new Pokemon(
                dto.id(),
                dto.name(),
                dto.height(),
                dto.weight(),
                dto.experienciaBase(),
                hp,
                ataque,
                defesa,
                ataqueEspecial,
                defesaEspecial,
                velocidade,
                imagem,
                tipos,
                habilidades
        );
    }

    // "https://pokeapi.co/api/v2/ability/65/" -> 65
    private static int extrairIdDaUrl(String url) {
        String semBarraFinal = url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
        String id = semBarraFinal.substring(semBarraFinal.lastIndexOf('/') + 1);
        return Integer.parseInt(id);
    }
}