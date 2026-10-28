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

    private final PokemonDAO dao;
    private final PokemonMapper mapper;

    private final RestTemplate restTemplate = new RestTemplate();

    public PokemonService(PokemonDAO dao, PokemonMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    public PokemonDTO importarDaApi(String nomeOuId) {
        String url = POKEAPI_URL + nomeOuId.trim().toLowerCase();
 
        PokemonApiDTO apiDto = restTemplate.getForObject(url, PokemonApiDTO.class);
        Pokemon pokemon = mapper.toModel(apiDto);
 
        if (dao.existePorId(pokemon.getId())) {
            throw new IllegalStateException("Pokémon '" + pokemon.getNome() + "' já foi importado.");
        }
 
        dao.inserir(pokemon);
        return mapper.toDTO(pokemon);
    }

    public List<PokemonDTO> listar() {
        return dao.listarTodos()
            .stream()
            .map(mapper::toDTO)
            .toList();
    }

    public Optional<PokemonDTO> buscarPorId(int id) {
        return dao.buscarPorId(id).map(mapper::toDTO);
    }
 
    public boolean remover(int id) {
        return dao.deletar(id);
    }
}