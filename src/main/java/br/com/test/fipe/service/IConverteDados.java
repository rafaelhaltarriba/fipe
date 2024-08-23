package br.com.test.fipe.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}