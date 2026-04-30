package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.TypeSource;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.ReferenceLegale} entity.
 */
public class ReferenceLegaleDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    private String contenu; // Texte extrait ou saisi manuellement

    @NotNull
    private TypeSource typeSource;

    private String version;

    private String qdrantUuid;

    /**
     * Ce champ reçoit le fichier binaire envoyé par le front-end. Il ne sera pas stocké dans la
     * table ReferenceLegale grâce au @Transient dans l'entité, mais servira à créer le
     * DocumentJoint.
     */
    private byte[] document;

    private String documentContentType;

    private String nomFichier; // Optionnel : pour passer le nom du fichier original

    // --- Getters et Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public TypeSource getTypeSource() {
        return typeSource;
    }

    public void setTypeSource(TypeSource typeSource) {
        this.typeSource = typeSource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getQdrantUuid() {
        return qdrantUuid;
    }

    public void setQdrantUuid(String qdrantUuid) {
        this.qdrantUuid = qdrantUuid;
    }

    public byte[] getDocument() {
        return document;
    }

    public void setDocument(byte[] document) {
        this.document = document;
    }

    public String getDocumentContentType() {
        return documentContentType;
    }

    public void setDocumentContentType(String documentContentType) {
        this.documentContentType = documentContentType;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReferenceLegaleDTO referenceLegaleDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, referenceLegaleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReferenceLegaleDTO{" + "id=" + getId() + ", titre='" + getTitre() + "'"
                + ", contenu='" + getContenu() + "'" + ", typeSource='" + getTypeSource() + "'"
                + ", version='" + getVersion() + "'" + ", qdrantUuid='" + getQdrantUuid() + "'"
                + "}";
    }
}
