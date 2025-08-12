package br.com.flixit.repository;

import br.com.flixit.model.Categoria;
import br.com.flixit.model.Episodio;
import br.com.flixit.model.Serie;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {


    boolean existsByTitulo(String titulo);

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCase(String nomeAtor);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);

    // select s"parâmetro" from Series"classe a ser usada" WHERE s.totalTemporadas"parâmetro.atributo a ser buscado".
    @Query( "select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacoes(int totalTemporadas, double avaliacao);

    @Query("SELECT e from Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC")
    List<Episodio> buscarTopEpisodiosPorSerie(@Param("serie") Serie serie, Pageable pageable);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND e.dataLancamento >= :dataInicio")
    List<Episodio> epidodiosPorSerieEAno(@Param("serie") Serie serie, @Param("dataInicio") LocalDate dataInicio);

}
