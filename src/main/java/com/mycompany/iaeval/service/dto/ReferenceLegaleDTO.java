package com.mycompany.iaeval.service.dto;

import com.mycompany.iaeval.domain.enumeration.TypeSource;
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

    @NotNull
    private TypeSource typeSource;

    private String version;

    private String qdrantUuid;

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
            ", typeSource='" + getTypeSource() + "'" +
            ", version='" + getVersion() + "'" +
            ", qdrantUuid='" + getQdrantUuid() + "'" +
            "}";
    }
}
