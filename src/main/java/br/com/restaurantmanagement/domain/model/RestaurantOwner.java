package br.com.restaurantmanagement.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESTAURANT_OWNER")
@NoArgsConstructor
public class RestaurantOwner extends User {
}
