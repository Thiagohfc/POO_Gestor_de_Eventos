package dao;

import java.util.List;

public interface InterfaceDAO<T> {
    void inserir(T obj);
    void atualizar(T obj);
    void excluir(int id);
    List<T> listar();
}
