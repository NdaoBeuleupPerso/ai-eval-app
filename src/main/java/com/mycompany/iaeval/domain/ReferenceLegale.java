package com.mycompany.iaeval.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mycompany.iaeval.domain.enumeration.TypeSource;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * A ReferenceLegale.
 */
@Entity
@Table(name = "reference_legale")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ReferenceLegale implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "contenu", nullable = true, columnDefinition = "TEXT")
    private String contenu;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_source", nullable = false)
    private TypeSource typeSource;

    @Column(name = "version")
    private String version;

    @Column(name = "qdrant_uuid")
    private String qdrantUuid;

    /**
     * RELATION RÉELLE : Liaison vers la table DocumentJoint. CascadeType.ALL permet de sauvegarder
     * le document en même temps que la référence.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "document_joint_id", referencedColumnName = "id")
    private DocumentJoint document;

    @Column(name = "document_content_type")
    private String documentContentType;

    /**
     * CHAMP TEMPORAIRE : Contenu binaire du fichier pour traitement (Tika/OCR).
     *
     * @Transient : N'est JAMAIS sauvegardé dans Postgres (évite l'erreur OID).
     * @JsonIgnore : N'est JAMAIS renvoyé au client (évite de saturer le réseau).
     */
    @Transient
    @JsonIgnore
    private byte[] fichierTemporaire;

    @Transient
    @JsonIgnore
    private String nomFichierTemporaire;

    // --- Getters et Setters ---

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return this.contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public TypeSource getTypeSource() {
        return this.typeSource;
    }

    public void setTypeSource(TypeSource typeSource) {
        this.typeSource = typeSource;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getQdrantUuid() {
        return this.qdrantUuid;
    }

    public void setQdrantUuid(String qdrantUuid) {
        this.qdrantUuid = qdrantUuid;
    }

    public DocumentJoint getDocument() {
        return this.document;
    }

    public void setDocument(DocumentJoint document) {
        this.document = document;
    }

    public String getDocumentContentType() {
        return this.documentContentType;
    }

    public void setDocumentContentType(String documentContentType) {
        this.documentContentType = documentContentType;
    }

    public byte[] getFichierTemporaire() {
        return fichierTemporaire;
    }

    public void setFichierTemporaire(byte[] fichierTemporaire) {
        this.fichierTemporaire = fichierTemporaire;
    }

    public String getNomFichierTemporaire() {
        return nomFichierTemporaire;
    }

    public void setNomFichierTemporaire(String nomFichierTemporaire) {
        this.nomFichierTemporaire = nomFichierTemporaire;
    }

    // Méthodes fluides (Fluent API)
    public ReferenceLegale titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public ReferenceLegale contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public ReferenceLegale typeSource(TypeSource typeSource) {
        this.setTypeSource(typeSource);
        return this;
    }

    public ReferenceLegale id(Long id) {
        this.setId(id);
        return this;
    }

    public ReferenceLegale version(String version) {
        this.setVersion(version);
        return this;
    }

    public ReferenceLegale qdrantUuid(String qdrantUuid) {
        this.setQdrantUuid(qdrantUuid);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceLegale)) return false;
        return id != null && id.equals(((ReferenceLegale) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ReferenceLegale{" + "id=" + getId() + ", titre='" + getTitre() + "'" + "}";
    }
}
