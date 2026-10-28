
package com.fiap.pokedex.dto;

import com.fiap.pokedex.enums.TipoEnum;
import java.util.List;

public record PokemonDTO(
        int id,
        String nome,
        int altura,
        int peso,
        int experienciaBase,
        int hp,
        int ataque,
        int defesa,
        int ataqueEspecial,
        int defesaEspecial,
        int velocidade,
        String spriteUrl,
        List<TipoEnum> tipos
) {
}
