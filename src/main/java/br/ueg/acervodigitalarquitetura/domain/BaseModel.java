package br.ueg.acervodigitalarquitetura.domain;

public interface BaseModel<TYPE_PK> {
    TYPE_PK getId();
    void setId(TYPE_PK id);
}
