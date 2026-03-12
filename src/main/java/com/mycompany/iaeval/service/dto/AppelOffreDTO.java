package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.AppelOffre} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AppelOffreDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private String titre;

    @Lob
    private byte[] description;

    private String descriptionContentType;

    private Instant dateCloture;

    private StatutAppel statut;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public String getDescriptionContentType() {
        return descriptionContentType;
    }

    public void setDescriptionContentType(String descriptionContentType) {
        this.descriptionContentType = descriptionContentType;
    }

    public Instant getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(Instant dateCloture) {
        this.dateCloture = dateCloture;
    }

    public StatutAppel getStatut() {
        return statut;
    }

    public void setStatut(StatutAppel statut) {
        this.statut = statut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AppelOffreDTO)) {
            return false;
        }

        AppelOffreDTO appelOffreDTO = (AppelOffreDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, appelOffreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AppelOffreDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateCloture='" + getDateCloture() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
