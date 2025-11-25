package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.entity.Vehicule;
import com.beninexplo.backend.repository.VehiculeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiculeService {

    @Autowired
    private VehiculeRepository vehiculeRepository;

    /* ----------------------
       CONVERT ENTITY → DTO
    ----------------------- */
    public VehiculeDTO toDTO(Vehicule v) {
        return new VehiculeDTO(
                v.getId(),
                v.getMarque(),
                v.getModele(),
                v.getMatricule(),
                v.getAnnee(),
                v.isDisponible()
        );
    }

    /* ----------------------
       CRUD LOGIQUE
    ----------------------- */

    public VehiculeDTO create(VehiculeDTO dto) {
        Vehicule v = new Vehicule();
        v.setMarque(dto.getMarque());
        v.setModele(dto.getModele());
        v.setMatricule(dto.getMatricule());
        v.setAnnee(dto.getAnnee());
        v.setDisponible(dto.isDisponible());

        vehiculeRepository.save(v);
        return toDTO(v);
    }

    public List<VehiculeDTO> getAll() {
        return vehiculeRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VehiculeDTO getById(Long id) {
        Vehicule v = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        return toDTO(v);
    }

    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        Vehicule v = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        v.setMarque(dto.getMarque());
        v.setModele(dto.getModele());
        v.setMatricule(dto.getMatricule());
        v.setAnnee(dto.getAnnee());
        v.setDisponible(dto.isDisponible());

        vehiculeRepository.save(v);
        return toDTO(v);
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}
