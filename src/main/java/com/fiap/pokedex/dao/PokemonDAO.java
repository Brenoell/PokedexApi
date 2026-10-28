package com.fiap.pokedex.dao;
 
import com.fiap.pokedex.enums.TipoEnum;
import com.fiap.pokedex.models.Habilidade;
import com.fiap.pokedex.models.Pokemon;
import org.springframework.stereotype.Repository;
 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

    public class PokemonDAO {
        public void inserir(Pokemon p) {
            String sql = "INSERT INTO pokemon (id, nome, altura, peso, experiencia_base, hp, ataque, "
                    + "defesa, ataque_especial, defesa_especial, velocidade, sprite_url) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection con = ConnectionFactory.getConnection()) {
                con.setAutoCommit(false);

                try (PreparedStatement ps = con.preparedStatement(sql)) {
                    ps.setInt(1, p.getId());
                    ps.setString(2, p.getNome());
                    ps.setInt(3, p.getAltura());
                    ps.setInt(4, p.getPeso());
                    ps.setInt(5, p.getExperienciaBase());
                    ps.setInt(6, p.getHp());
                    ps.setInt(7, p.getAtaque());
                    ps.setInt(8, p.getDefesa());
                    ps.setInt(9, p.getAtaqueEspecial());
                    ps.setInt(10, p.getDefesaEspecial());
                    ps.setInt(11, p.getVelocidade());
                    ps.setString(12, p.getSpriteUrl());
                    ps.executeUpdate();
    
                    inserirTipos(con, p);
                    inserirHabilidades(con, p);
    
                    con.commit();
                } catch (SQLException e) {
                    con.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao inserir pokémon: " + e.getMessage(), e);
            }
        }
    }

    private void inserirTipos(Connection con, Pokemon p) throws SQLException {
            String sqlTipo = "SELECT id FROM tipo WHERE nome = ?";
            String sqlLink = "INSERT INTO pokemon_tipo (pokemon_id, tipo_id, slot) VALUES (?, ?, ?)";
    
            int slot = 1;
            for (TipoEnum tipo : p.getTipos()) {
                int tipoId;
                try (PreparedStatement ps = con.prepareStatement(sqlTipo)) {
                    ps.setString(1, tipo.name().toLowerCase());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Tipo não cadastrado na tabela 'tipo': " + tipo);
                        }
                        tipoId = rs.getInt("id");
                    }
                }
    
                try (PreparedStatement ps = con.prepareStatement(sqlLink)) {
                    ps.setInt(1, p.getId());
                    ps.setInt(2, tipoId);
                    ps.setInt(3, slot++);
                    ps.executeUpdate();
                }
            }
        }
 
    private void inserirHabilidades(Connection con, Pokemon p) throws SQLException {
        String sqlExiste = "SELECT 1 FROM habilidade WHERE id = ?";
        String sqlHab = "INSERT INTO habilidade (id, nome) VALUES (?, ?)";
        String sqlLink = "INSERT INTO pokemon_habilidade (pokemon_id, habilidade_id, oculta) VALUES (?, ?, ?)";
 
        for (Habilidade h : p.getHabilidades()) {
            boolean existe;
            try (PreparedStatement ps = con.prepareStatement(sqlExiste)) {
                ps.setInt(1, h.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    existe = rs.next();
                }
            }
 
            if (!existe) {
                try (PreparedStatement ps = con.prepareStatement(sqlHab)) {
                    ps.setInt(1, h.getId());
                    ps.setString(2, h.getNome());
                    ps.executeUpdate();
                }
            }
 
            try (PreparedStatement ps = con.prepareStatement(sqlLink)) {
                ps.setInt(1, p.getId());
                ps.setInt(2, h.getId());
                ps.setBoolean(3, h.isOculta());
                ps.executeUpdate();
            }
        }
    }
 
    public Optional<Pokemon> buscarPorId(int id) {
        String sql = "SELECT * FROM pokemon WHERE id = ?";
 
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pokemon p = mapearPokemon(rs);
                    p.setTipos(buscarTipos(con, id));
                    p.setHabilidades(buscarHabilidades(con, id));
                    return Optional.of(p);
                }
            }
            return Optional.empty();
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pokémon: " + e.getMessage(), e);
        }
    }
 
    public List<Pokemon> listarTodos() {
        String sql = "SELECT * FROM pokemon ORDER BY id";
        List<Pokemon> lista = new ArrayList<>();
 
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                Pokemon p = mapearPokemon(rs);
                p.setTipos(buscarTipos(con, p.getId()));
                p.setHabilidades(buscarHabilidades(con, p.getId()));
                lista.add(p);
            }
            return lista;
 
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pokémon: " + e.getMessage(), e);
        }
    }
 
    public boolean existePorId(int id) {
        String sql = "SELECT 1 FROM pokemon WHERE id = ?";
 
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
 
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar pokémon: " + e.getMessage(), e);
        }
    }

    public boolean deletar(int id) {
        try (Connection con = ConnectionFactory.getConnection()) {
            con.setAutoCommit(false);
 
            try {
                executarDelete(con, "DELETE FROM pokemon_tipo WHERE pokemon_id = ?", id);
                executarDelete(con, "DELETE FROM pokemon_habilidade WHERE pokemon_id = ?", id);
                int linhas = executarDelete(con, "DELETE FROM pokemon WHERE id = ?", id);
 
                con.commit();
                return linhas > 0;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pokémon: " + e.getMessage(), e);
        }
    }
 
    private int executarDelete(Connection con, String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    private Pokemon mapearPokemon(ResultSet rs) throws SQLException {
        Pokemon p = new Pokemon();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setAltura(rs.getInt("altura"));
        p.setPeso(rs.getInt("peso"));
        p.setExperienciaBase(rs.getInt("experiencia_base"));
        p.setHp(rs.getInt("hp"));
        p.setAtaque(rs.getInt("ataque"));
        p.setDefesa(rs.getInt("defesa"));
        p.setAtaqueEspecial(rs.getInt("ataque_especial"));
        p.setDefesaEspecial(rs.getInt("defesa_especial"));
        p.setVelocidade(rs.getInt("velocidade"));
        p.setSpriteUrl(rs.getString("sprite_url"));
        return p;
    }
 
    private List<TipoEnum> buscarTipos(Connection con, int pokemonId) throws SQLException {
        String sql = "SELECT t.nome FROM pokemon_tipo pt "
                + "JOIN tipo t ON t.id = pt.tipo_id "
                + "WHERE pt.pokemon_id = ? ORDER BY pt.slot";
 
        List<TipoEnum> tipos = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pokemonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // "fire" (banco) -> FIRE (enum)
                    tipos.add(TipoEnum.valueOf(rs.getString("nome").toUpperCase()));
                }
            }
        }
        return tipos;
    }
 
    private List<Habilidade> buscarHabilidades(Connection con, int pokemonId) throws SQLException {
        String sql = "SELECT h.id, h.nome, ph.oculta FROM pokemon_habilidade ph "
                + "JOIN habilidade h ON h.id = ph.habilidade_id "
                + "WHERE ph.pokemon_id = ?";
 
        List<Habilidade> habilidades = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pokemonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Habilidade h = new Habilidade();
                    h.setId(rs.getInt("id"));
                    h.setNome(rs.getString("nome"));
                    h.setOculta(rs.getBoolean("oculta"));
                    habilidades.add(h);
                }
            }
        }
        return habilidades;
    }
}
 