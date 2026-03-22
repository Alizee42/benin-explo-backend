package com.beninexplo.backend.service;

import com.beninexplo.backend.dto.VehiculeDTO;
import com.beninexplo.backend.entity.Vehicule;
import com.beninexplo.backend.exception.ResourceNotFoundException;
import com.beninexplo.backend.repository.VehiculeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;

    public VehiculeService(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public VehiculeDTO toDTO(Vehicule vehicule) {
        return new VehiculeDTO(
                vehicule.getId(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getMatricule(),
                vehicule.getAnnee(),
                vehicule.isDisponible()
        );
    }

    public VehiculeDTO create(VehiculeDTO dto) {
        Vehicule vehicule = new Vehicule();
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setMatricule(dto.getMatricule());
        vehicule.setAnnee(dto.getAnnee());
        vehicule.setDisponible(dto.isDisponible());
        return toDTO(vehiculeRepository.save(vehicule));
    }

    public List<VehiculeDTO> getAll() {
        return vehiculeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VehiculeDTO getById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable."));
        return toDTO(vehicule);
    }

    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule introuvable."));
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setMatricule(dto.getMatricule());
        vehicule.setAnnee(dto.getAnnee());
        vehicule.setDisponible(dto.isDisponible());
        return toDTO(vehiculeRepository.save(vehicule));
    }

    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}
