package br.com.restaurantmanagement.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CLIENT")
@NoArgsConstructor
public class Client extends User {
}
