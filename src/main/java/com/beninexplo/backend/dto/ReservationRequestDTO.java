package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ReservationRequestDTO {

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas depasser 100 caracteres.")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire.")
    @Size(max = 100, message = "Le prenom ne doit pas depasser 100 caracteres.")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit etre valide.")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres.")
    private String email;

    @NotBlank(message = "Le telephone est obligatoire.")
    @Pattern(regexp = "^[0-9+()\\-\\s]{8,20}$", message = "Le telephone doit contenir entre 8 et 20 caracteres valides.")
    private String telephone;

    @NotNull(message = "La date de reservation est obligatoire.")
    @FutureOrPresent(message = "La date de reservation ne peut pas etre dans le passe.")
    private LocalDate dateReservation;

    @NotNull(message = "Le circuit est obligatoire.")
    @Positive(message = "Le circuit doit etre un identifiant positif.")
    private Long circuitId;

    public ReservationRequestDTO() {
    }

    public ReservationRequestDTO(String nom, String prenom, String email, String telephone,
                                 LocalDate dateReservation, Long circuitId) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.dateReservation = dateReservation;
        this.circuitId = circuitId;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public Long getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(Long circuitId) {
        this.circuitId = circuitId;
    }
}
