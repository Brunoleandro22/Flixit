package br.com.flixit.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Anotação que ignora as propriedade não declaradas
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(
        // Associa a propriedade "Title" do JSON ao campo "titulo" do record
        @JsonAlias("Title") String titulo,

        // Associa a propriedade "totalSeasons" do JSON ao campo "totalTemporadas"
        @JsonAlias("totalSeasons") Integer totalTemporadas,

        // Associa a propriedade "imdbRating" do JSON ao campo "avaliacao"
        @JsonAlias("imdbRating")String avaliacao,
        @JsonAlias("Genre") String genero,
        @JsonAlias("Actors") String atores,
        @JsonAlias ("Plot") String sinopse,
        @JsonAlias("Poster") String poster) {
}
