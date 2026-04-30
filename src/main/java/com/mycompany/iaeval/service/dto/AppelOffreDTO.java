package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.StatutAppel;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.AppelOffre} entity.
 */
public class AppelOffreDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private String titre;

    /**
     * Ce champ 'description' (byte[]) reçoit le fichier binaire depuis Angular. Il sera mappé vers
     * 'fichierTemporaire' dans l'entité.
     */
    private byte[] description;

    private String descriptionContentType;

    /**
     * AJOUT : Ce champ contiendra le texte extrait (OCR) renvoyé par le serveur pour l'affichage
     * dans l'interface utilisateur.
     */
    private String descriptionTexte;

    /**
     * AJOUT : Pour capturer le nom original du fichier (ex: cahier_des_charges.pdf)
     */
    private String nomFichier;

    private Instant dateCloture;

    private StatutAppel statut;

    // --- Getters et Setters ---

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

    public String getDescriptionTexte() {
        return descriptionTexte;
    }

    public void setDescriptionTexte(String descriptionTexte) {
        this.descriptionTexte = descriptionTexte;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
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
        if (this == o) return true;
        if (!(o instanceof AppelOffreDTO appelOffreDTO)) return false;
        if (this.id == null) return false;
        return Objects.equals(this.id, appelOffreDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "AppelOffreDTO{" + "id=" + getId() + ", reference='" + getReference() + "', titre='" + getTitre() + "'}";
    }
}
