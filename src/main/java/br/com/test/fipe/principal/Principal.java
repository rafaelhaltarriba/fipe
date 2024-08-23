package br.com.test.fipe.principal;

import br.com.test.fipe.model.DadosModelo;
import br.com.test.fipe.model.DadosVeiculo;
import br.com.test.fipe.service.ConsumoAPI;
import br.com.test.fipe.service.ConverteDados;

import java.util.Map;
import java.util.Scanner;

public class Principal {

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);

    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    private final String MARCAS = "/marcas";
    private final String MODELOS = "/modelos";
    private final String ANOS = "/anos";


    public void exibeMenu() {
        System.out.println("Digite o tipo de veículo:\nCarro\nMoto\nCaminhao");
        String tipoVeiculo = leitura.nextLine().toLowerCase();

        Map<String, String> veiculoMap = Map.of(
                "carro", "carros",
                "moto", "motos",
                "caminhao", "caminhoes"
        );

        String tipoVeiculoFormatado = veiculoMap.get(tipoVeiculo);
        if (tipoVeiculoFormatado == null) {
            System.out.println("Tipo de veículo inválido. Por favor, digite 'Carro', 'Moto' ou 'Caminhao'.");
            return;
        }

        String json = consumo.obterDados(URL_BASE + tipoVeiculoFormatado + MARCAS);
        DadosVeiculo[] dadosMarca = conversor.obterDados(json, DadosVeiculo[].class);

        for (DadosVeiculo dados : dadosMarca) {
            System.out.println(dados);
        }

        System.out.println("Digite o código da marca:");
        String codigoMarca = leitura.nextLine();
        json = consumo.obterDados(URL_BASE + tipoVeiculoFormatado + MARCAS + "/" + codigoMarca + MODELOS);
        DadosModelo dadosModelo = conversor.obterDados(json, DadosModelo.class);

        for (DadosVeiculo dados : dadosModelo.modelos()) {
            System.out.println(dados);
        }

        System.out.println("Digite o código do modelo:");
        String codigoModelo = leitura.nextLine();
        json = consumo.obterDados(URL_BASE + tipoVeiculoFormatado + MARCAS + "/" + codigoMarca + MODELOS + "/" + codigoModelo + ANOS);
        DadosVeiculo[] dadosAnos = conversor.obterDados(json, DadosVeiculo[].class);

        for (DadosVeiculo dados : dadosAnos) {
            System.out.println(dados);
        }
    }
}
