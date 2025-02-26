package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * @author onegines
 * @date 11.11.2024
 */
@Entity
@Table(name = "manager_enterprise")
public class ManagerEnterprise implements Serializable {
    @EmbeddedId
    private ManagerEnterpriseId id;

    @ManyToOne
    @MapsId("enterpriseId")  // Указываем, что поле enterprise связано с полем из составного ключа
    @JoinColumn(name = "enterprise_id")  // Название колонки в таблице
    private Enterprise enterprise;

    @ManyToOne
    @MapsId("managerId")  // Указываем, что поле manager связано с полем из составного ключа
    @JoinColumn(name = "manager_id")  // Название колонки в таблице
    private Manager manager;

    @Embeddable
    public static class ManagerEnterpriseId implements Serializable {

        @Column(name = "manager_id")
        private UUID managerId;

        @Column(name = "enterprise_id")
        private UUID enterpriseId;

        // Конструкторы, геттеры и сеттеры

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ManagerEnterpriseId that = (ManagerEnterpriseId) o;
            return Objects.equals(managerId, that.managerId) &&
                    Objects.equals(enterpriseId, that.enterpriseId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(managerId, enterpriseId);
        }
    }
}
