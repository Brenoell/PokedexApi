package com.fiap.pokedex.service;

import com.fiap.pokedex.dao.PokemonDAO;
import com.fiap.pokedex.dto.PokemonApiDTO;
import com.fiap.pokedex.dto.PokemonDTO;
import com.fiap.pokedex.mapper.PokemonMapper;
import com.fiap.pokedex.models.Pokemon;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {

    private static final String POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/";
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

    private final PokemonDAO dao;
    private final RestTemplate restTemplate = new RestTemplate();

    public PokemonService(PokemonDAO dao) {
        this.dao = dao;
    }

    public PokemonDTO importarDaApi(String nomeOuId) {
        String url = POKEAPI_URL + nomeOuId.trim().toLowerCase();

        PokemonApiDTO apiDto = restTemplate.getForObject(url, PokemonApiDTO.class);
        Pokemon pokemon = PokemonMapper.daApiParaModel(apiDto);

        if (dao.existePorId(pokemon.getId())) {
            throw new IllegalStateException("Pokémon '" + pokemon.getNome() + "' já foi importado.");
        }

        dao.inserir(pokemon);
        return PokemonMapper.paraDTO(pokemon);
    }

    public List<PokemonDTO> listar() {
        return dao.listarTodos()
                .stream()
                .map(PokemonMapper::paraDTO)
                .toList();
    }

    public Optional<PokemonDTO> buscarPorId(int id) {
        return dao.buscarPorId(id).map(PokemonMapper::paraDTO);
    }

    public boolean remover(int id) {
        return dao.deletar(id);
    }
    public PokemonDTO buscarPorNome(String nome) {
        Pokemon pokemon = dao.buscarPorNome(nome);
        if (pokemon == null) {
            return null;
        }
        return PokemonMapper.paraDTO(pokemon);
    }
}