package br.com.flixit.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.time.DateTimeException;
import java.time.LocalDate;

@Entity
@Table(name = "episodio")
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dalaLancamento;

    @ManyToOne
    private Serie serie;

    public Episodio(){}

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodio) {

        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();
        try{
            this.avaliacao = Double.valueOf( dadosEpisodio.avaliacao());
        }catch (NumberFormatException ex){
            this.avaliacao = 0.0;
        }
        try {
            this.dalaLancamento = LocalDate.parse(dadosEpisodio.dalaLancamento());
        }catch (DateTimeException ex){
            dalaLancamento = dalaLancamento;
        }

    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public LocalDate getDalaLancamento() {
        return dalaLancamento;
    }

    public void setDalaLancamento(LocalDate dalaLancamento) {
        this.dalaLancamento = dalaLancamento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumero() {
        return numeroEpisodio;
    }

    public void setNumero(Integer numero) {
        this.numeroEpisodio = numero;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    @Override
    public String toString() {
        return "Episodio{" +
                "temporada=" + temporada +
                ", titulo='" + titulo + '\'' +
                ", numeroEpisodio=" + numeroEpisodio +
                ", avaliacao=" + avaliacao +
                ", dalaLancamento=" + dalaLancamento +
                '}';
    }
}