package com.mycompany.iaeval.service.mapper;

import com.mycompany.iaeval.domain.AppelOffre;
import com.mycompany.iaeval.service.dto.AppelOffreDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
/**
 * Mapper for the entity {@link AppelOffre} and its DTO {@link AppelOffreDTO}.
 */
import org.mapstruct.ReportingPolicy; // Import à ajouter

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppelOffreMapper extends EntityMapper<AppelOffreDTO, AppelOffre> {
    @Override
    @Mapping(source = "description", target = "fichierTemporaire") // DTO byte[] -> Entity
    // @Transient byte[]
    @Mapping(source = "nomFichier", target = "nomFichierTemporaire")
    @Mapping(target = "description", ignore = true) // On ignore le String description de l'entité
    // ici
    AppelOffre toEntity(AppelOffreDTO dto);

    @Override
    @Mapping(target = "description", ignore = true) // Ne pas renvoyer le binaire au client
    AppelOffreDTO toDto(AppelOffre entity);

    @Override
    @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE) // AJOUTEZ CECI
    @Mapping(source = "description", target = "fichierTemporaire")
    @Mapping(target = "description", ignore = true)
    void partialUpdate(@MappingTarget AppelOffre entity, AppelOffreDTO dto);
}
