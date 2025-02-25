package ru.onegines.carpark.CarPark.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author onegines
 * @date 31.10.2024
 */
public class CarDTO {
    private Long carId; // ID автомобиля
    private Long brandId; // ID бренда
    private Integer mileage;
    private Integer годВыпуска;
    private Integer reserve;
    private String number;
    private Long activeDriverId;
    private List<Long> allDriversId;
    private UUID enterpriseId;
    private LocalDateTime purchaseDateTime;
    private LocalDateTime enterprisePurchaseDateTime;
    private String purchaseDateTimeInEnterpriseTimeZone;


    public CarDTO(Long carId, Long brandId, Integer mileage, Integer годВыпуска, Integer reserve, String number,
                  Long activeDriverId, List<Long> allDriversId, UUID enterpriseId, LocalDateTime purchaseDateTime,
                  String purchaseDateTimeInEnterpriseTimeZone) {
        this.carId = carId;
        this.brandId = brandId;
        this.mileage = mileage;
        this.годВыпуска = годВыпуска;
        this.reserve = reserve;
        this.number = number;
        this.activeDriverId = activeDriverId;
        this.allDriversId = allDriversId;
        this.enterpriseId = enterpriseId;
        this.purchaseDateTime = purchaseDateTime;
        this.purchaseDateTimeInEnterpriseTimeZone = purchaseDateTimeInEnterpriseTimeZone;

    }

    public CarDTO() {

    }

    public CarDTO(Long carId,
                  Long brandId,
                  int mileage,
                  int годВыпуска,
                  int reserve,
                  String number,
                  Long activeDriverId,
                  List<Long> allDriversId,
                  UUID enterpriseId,
                  LocalDateTime purchaseDateTime) {
        this.carId = carId;
        this.brandId = brandId;
        this.mileage = mileage;
        this.годВыпуска = годВыпуска;
        this.reserve = reserve;
        this.number = number;
        this.activeDriverId = activeDriverId;
        this.allDriversId = allDriversId;
        this.enterpriseId = enterpriseId;
        this.purchaseDateTime = purchaseDateTime;

    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getГодВыпуска() {
        return годВыпуска;
    }

    public void setГодВыпуска(Integer годВыпуска) {
        this.годВыпуска = годВыпуска;
    }

    public Integer getReserve() {
        return reserve;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getActiveDriverId() {
        return activeDriverId;
    }

    public void setActiveDriverId(Long activeDriverId) {
        this.activeDriverId = activeDriverId;
    }

    public List<Long> getAllDriversId() {
        return allDriversId;
    }

    public void setAllDriversId(List<Long> allDriversId) {
        this.allDriversId = allDriversId;
    }

    public UUID getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(UUID enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public String getPurchaseDateTimeInEnterpriseTimeZone() {
        return purchaseDateTimeInEnterpriseTimeZone;
    }

    public void setPurchaseDateTimeInEnterpriseTimeZone(String purchaseDateTimeInEnterpriseTimeZone) {
        this.purchaseDateTimeInEnterpriseTimeZone = purchaseDateTimeInEnterpriseTimeZone;
    }

    public void setEnterprisePurchaseDateTime(LocalDateTime toLocalDateTime) {
        this.enterprisePurchaseDateTime = toLocalDateTime;
    }

    public LocalDateTime getEnterprisePurchaseDateTime() {
        return enterprisePurchaseDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarDTO carDTO = (CarDTO) o;
        return mileage == carDTO.mileage && годВыпуска == carDTO.годВыпуска && reserve == carDTO.reserve && Objects.equals(brandId, carDTO.brandId) && Objects.equals(number, carDTO.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brandId, mileage, годВыпуска, reserve, number);
    }


    @Override
    public String toString() {
        return "CarDTO{" +
                "carId=" + carId +
                ", brandId=" + brandId +
                ", mileage=" + mileage +
                ", годВыпуска=" + годВыпуска +
                ", reserve=" + reserve +
                ", number='" + number + '\'' +
                ", activeDriverId=" + activeDriverId +
                ", allDriversId=" + allDriversId +
                ", enterpriseId=" + enterpriseId +
                ", purchaseDateTime=" + purchaseDateTime +
                ", purchaseDateTimeInEnterpriseTimeZone='" + purchaseDateTimeInEnterpriseTimeZone + '\'' +
                '}';
    }
}
