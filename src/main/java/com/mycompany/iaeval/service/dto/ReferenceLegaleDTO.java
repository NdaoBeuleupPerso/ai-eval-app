package com.mycompany.iaeval.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.iaeval.domain.ReferenceLegale} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReferenceLegaleDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    @Lob
    private String contenu;

    private String qdrantUuid;

    private String source;

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

    public String getQdrantUuid() {
        return qdrantUuid;
    }

    public void setQdrantUuid(String qdrantUuid) {
        this.qdrantUuid = qdrantUuid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReferenceLegaleDTO)) {
            return false;
        }

        ReferenceLegaleDTO referenceLegaleDTO = (ReferenceLegaleDTO) o;
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
        return "ReferenceLegaleDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", contenu='" + getContenu() + "'" +
            ", qdrantUuid='" + getQdrantUuid() + "'" +
            ", source='" + getSource() + "'" +
            "}";
    }
}
