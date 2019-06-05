package Entity;

import java.util.Objects;

public class PaymentOrder {
    private String merchantNO;
    private String price;
    private String orderNO;
    private String buyer;

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentOrder that = (PaymentOrder) o;
        return merchantNO.equals(that.merchantNO) &&
                price.equals(that.price) &&
                orderNO.equals(that.orderNO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchantNO, price, orderNO);
    }

    public String getMerchantNO() {
        return merchantNO;
    }

    public void setMerchantNO(String merchantNO) {
        this.merchantNO = merchantNO;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(String orderNO) {
        this.orderNO = orderNO;
    }
}
