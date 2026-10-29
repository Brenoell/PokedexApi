package com.fiap.pokedex.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * API utilizada: PokéAPI (https://pokeapi.co/)
 *
 * O que ela faz:
 * É uma API pública e gratuita, no padrão REST, que devolve em JSON dados
 * da franquia Pokémon. Não exige chave de acesso nem autenticação.
 *
 * Requisição utilizada:
 * GET https://pokeapi.co/api/v2/pokemon/{nome ou id}
 * Exemplo: https://pokeapi.co/api/v2/pokemon/pikachu
 *
 * Dados utilizados da resposta:
 * - id: identificador do pokémon (usado como chave primária no banco)
 * - name: nome
 * - height: altura (em decímetros)
 * - weight: peso (em hectogramas)
 * - base_experience: experiência base
 * - stats: atributos base (hp, attack, defense, special-attack,
 *          special-defense e speed)
 * - types: tipos do pokémon (ex.: fire, water), com a ordem (slot)
 * - abilities: habilidades, com o nome, o id (extraído da URL) e
 *              a indicação se é uma habilidade oculta (is_hidden)
 * - sprites.other.official-artwork.front_default: URL da imagem oficial
 *
 * Integrantes: Breno Elisio Leal Lobato - RM569961 e Gustavo Lizier Pontes - RM573234
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Habilidade {

    private int id;
    private String nome;
    private boolean oculta;
}