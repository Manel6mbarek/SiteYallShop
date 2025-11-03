package com.facturation.facture.service;

import com.facturation.facture.dto.CategorieDTO;
import com.facturation.facture.model.Categorie;
import com.facturation.facture.repository.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    // Convertir entité -> DTO
    private CategorieDTO toDTO(Categorie categorie) {
        CategorieDTO dto = new CategorieDTO();
        dto.setId(categorie.getId());
        dto.setNom(categorie.getNom());
        dto.setDescription(categorie.getDescription());
        dto.setActif(categorie.getActif());
        return dto;
    }

    // Convertir DTO -> entité
    private Categorie toEntity(CategorieDTO dto) {
        Categorie categorie = new Categorie();
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        categorie.setActif(dto.getActif() != null ? dto.getActif() : true);
        return categorie;
    }

    public List<CategorieDTO> getAllCategories() {
        return categorieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<CategorieDTO> getCategorieById(Long id) {
        return categorieRepository.findById(id).map(this::toDTO);
    }

    public CategorieDTO createCategorie(CategorieDTO dto) {
        Categorie categorie = toEntity(dto);
        return toDTO(categorieRepository.save(categorie));
    }

    public Optional<CategorieDTO> updateCategorie(Long id, CategorieDTO dto) {
        return categorieRepository.findById(id).map(c -> {
            c.setNom(dto.getNom());
            c.setDescription(dto.getDescription());
            c.setActif(dto.getActif());
            return toDTO(categorieRepository.save(c));
        });
    }

    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}
