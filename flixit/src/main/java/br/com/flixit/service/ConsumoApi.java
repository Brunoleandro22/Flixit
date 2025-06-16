package br.com.flixit.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoApi {
    public String obterDados(String endereco) {
        // Cria um cliente HTTP utilizando a API HttpClient do Java (disponível desde o Java 11)
        HttpClient client = HttpClient.newHttpClient();

        // Constrói uma requisição HTTP do tipo GET para o endereço informado
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endereco)) // Converte a String do endereço para um objeto URI
                .build(); // Finaliza a construção da requisição

        // Declara a variável que armazenará a resposta da requisição
        HttpResponse<String> response = null;

        try {
            // Envia a requisição e aguarda a resposta
            // O BodyHandlers.ofString() especifica que o corpo da resposta será tratado como uma String
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            // Trata erros relacionados a falhas de entrada/saída (ex.: problemas de conexão)
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // Trata erros relacionados à interrupção da execução da requisição
            throw new RuntimeException(e);
        }

        // Obtém o corpo da resposta (em formato JSON, se a API retornar um JSON)
        String json = response.body();

        // Retorna a resposta como uma String
        return json;
    }
}