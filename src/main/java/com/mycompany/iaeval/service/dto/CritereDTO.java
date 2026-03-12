package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.TypeCritere;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.Critere} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CritereDTO implements Serializable {

    private Long id;

    @NotNull
    private String nom;

    @NotNull
    private Double ponderation;

    @NotNull
    private TypeCritere categorie;

    private String description;

    private AppelOffreDTO appelOffre;

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

    public Double getPonderation() {
        return ponderation;
    }

    public void setPonderation(Double ponderation) {
        this.ponderation = ponderation;
    }

    public TypeCritere getCategorie() {
        return categorie;
    }

    public void setCategorie(TypeCritere categorie) {
        this.categorie = categorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppelOffreDTO getAppelOffre() {
        return appelOffre;
    }

    public void setAppelOffre(AppelOffreDTO appelOffre) {
        this.appelOffre = appelOffre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CritereDTO)) {
            return false;
        }

        CritereDTO critereDTO = (CritereDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, critereDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CritereDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", ponderation=" + getPonderation() +
            ", categorie='" + getCategorie() + "'" +
            ", description='" + getDescription() + "'" +
            ", appelOffre=" + getAppelOffre() +
            "}";
    }
}
