package br.ueg.genericarchitecture.domain;

public interface GenericModel<TYPE_PK> {
    TYPE_PK getId();
    void setId(TYPE_PK id);
}
