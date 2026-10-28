package com.fiap.pokedex.controller;
 
import com.fiap.pokedex.dto.PokemonDTO;
import com.fiap.pokedex.service.PokemonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
 
import java.util.List;

@RestController
@RequestMapping("/pokemons") 
public class PokemonController {
    private final PokemonService service;

    public PokemonController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PokemonDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PokemonDTO> buscarPorId(@PathVariable int id) {
        return service.buscarPorId(id)
            .map(ResponseEntity::ok)                        
            .orElse(ResponseEntity.notFound().build()); 

    @PostMapping("/importar/{nome}")
        public ResponseEntity<?> importar(@PathVariable String nome) {
            try {
                PokemonDTO salvo = service.importarDaApi(nome);
                return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    
            } catch (HttpClientErrorException.NotFound e) {
                // A PokeAPI respondeu 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pokémon '" + nome + "' não encontrado na PokeAPI.");
    
            } catch (IllegalStateException e) {
                // Regra de negócio do Service: pokémon duplicado
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        }
        
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        return service.remover(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}