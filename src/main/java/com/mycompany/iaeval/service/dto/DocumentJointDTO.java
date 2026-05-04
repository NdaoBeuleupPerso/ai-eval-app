package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.FormatDocument;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.DocumentJoint} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentJointDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private FormatDocument format;

    private String url;

    private String contenuOcr;

    private String idExterne;

    private SoumissionDTO soumission;
    private byte[] contenu;

    public byte[] getContenu() {
        return contenu;
    }

    public void setContenu(byte[] contenu) {
        this.contenu = contenu;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public FormatDocument getFormat() {
        return format;
    }

    public void setFormat(FormatDocument format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContenuOcr() {
        return contenuOcr;
    }

    public void setContenuOcr(String contenuOcr) {
        this.contenuOcr = contenuOcr;
    }

    public String getIdExterne() {
        return idExterne;
    }

    public void setIdExterne(String idExterne) {
        this.idExterne = idExterne;
    }

    public SoumissionDTO getSoumission() {
        return soumission;
    }

    public void setSoumission(SoumissionDTO soumission) {
        this.soumission = soumission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentJointDTO documentJointDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, documentJointDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentJointDTO{" + "id=" + getId() + ", nom='" + getNom() + "'" + ", format='"
                + getFormat() + "'" + ", url='" + getUrl() + "'" + ", contenuOcr='"
                + getContenuOcr() + "'" + ", idExterne='" + getIdExterne() + "'" + ", soumission="
                + getSoumission() + "}";
    }
}
