package br.ueg.acervodigitalarquitetura.dto;


import java.util.List;

public interface DTOFile {
    List<FileDTO> getFiles();
    void setFiles(List<FileDTO> files);
}
