package com.btg.funds.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        String password,

        @NotBlank(message = "La preferencia de notificación es obligatoria")
        @Pattern(regexp = "email|sms", message = "La preferencia debe ser 'email' o 'sms'")
        String notificationPreference,

        @NotBlank(message = "El contacto es obligatorio")
        String contactInfo
) {}
