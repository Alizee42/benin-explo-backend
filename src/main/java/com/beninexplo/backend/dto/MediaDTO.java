package com.beninexplo.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MediaDTO {

    private Long id;

    @NotBlank(message = "L'URL du media est obligatoire.")
    @Size(max = 1000, message = "L'URL du media ne doit pas depasser 1000 caracteres.")
    private String url;

    @NotBlank(message = "Le type du media est obligatoire.")
    @Pattern(regexp = "^(?i)(image|video|autre)$", message = "Le type du media doit etre image, video ou autre.")
    private String type;

    @Size(max = 500, message = "La description du media ne doit pas depasser 500 caracteres.")
    private String description;

    public MediaDTO() {
    }

    public MediaDTO(Long id, String url, String type, String description) {
        this.id = id;
        this.url = url;
        this.type = type;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
