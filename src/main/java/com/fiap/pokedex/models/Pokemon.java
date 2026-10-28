package com.fiap.pokedex.models;

import com.fiap.pokedex.enums.TipoEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pokemon {

    private int id;
    private String nome;
    private int altura;
    private int peso;
    private int experienciaBase;

    private int hp;
    private int ataque;
    private int defesa;
    private int ataqueEspecial;
    private int defesaEspecial;
    private int velocidade;

    private String spriteUrl;

    private List<TipoEnum> tipos;
}
