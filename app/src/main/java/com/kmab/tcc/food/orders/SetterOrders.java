package com.kmab.tcc.food.orders;

public class SetterOrders {

    /*public SetterOrders() {
    }*/

    private String uid, key, orderCode, deliveryAreaKey, notes, name, surname, confirmationCode, address;
    private double orderCharge, deliveryCharge, processingCharge, serviceCharge, takeawayCharge;
    private long timestamp, deliveryStart, deliverBefore, deliveredOn;
    private boolean isPaid, isPreparing, isPreparingFinished, isDelivering, isDelivered, isTakeaway;

    public SetterOrders(String uid, String key, String orderCode, String deliveryAreaKey, String notes, String name, String surname, String confirmationCode, String address,
                        double orderCharge, double deliveryCharge, double processingCharge, double serviceCharge, double takeawayCharge,
                        long timestamp, long deliveryStart, long deliverBefore, long deliveredOn,
                        boolean isPaid, boolean isPreparing, boolean isPreparingFinished,
                        boolean isDelivering, boolean isDelivered, boolean isTakeaway) {
        this.uid = uid;
        this.key = key;
        this.orderCode = orderCode;
        this.deliveryAreaKey = deliveryAreaKey;
        this.notes = notes;
        this.name = name;
        this.surname = surname;
        this.confirmationCode = confirmationCode;
        this.address = address;
        this.orderCharge = orderCharge;
        this.deliveryCharge = deliveryCharge;
        this.processingCharge = processingCharge;
        this.serviceCharge = serviceCharge;
        this.takeawayCharge = takeawayCharge;
        this.timestamp = timestamp;
        this.deliveryStart = deliveryStart;
        this.deliverBefore = deliverBefore;
        this.deliveredOn = deliveredOn;
        this.isPaid = isPaid;
        this.isPreparing = isPreparing;
        this.isPreparingFinished = isPreparingFinished;
        this.isDelivering = isDelivering;
        this.isDelivered = isDelivered;
        this.isTakeaway = isTakeaway;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDeliveryAreaKey() {
        return deliveryAreaKey;
    }

    public void setDeliveryAreaKey(String deliveryAreaKey) {
        this.deliveryAreaKey = deliveryAreaKey;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDeliveryStart() {
        return deliveryStart;
    }

    public void setDeliveryStart(long deliveryStart) {
        this.deliveryStart = deliveryStart;
    }

    public double getTakeawayCharge() {
        return takeawayCharge;
    }

    public void setTakeawayCharge(double takeawayCharge) {
        this.takeawayCharge = takeawayCharge;
    }

    public boolean isTakeaway() {
        return isTakeaway;
    }

    public void setTakeaway(boolean takeaway) {
        isTakeaway = takeaway;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getOrderCharge() {
        return orderCharge;
    }

    public void setOrderCharge(double orderCharge) {
        this.orderCharge = orderCharge;
    }

    public double getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(double deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getProcessingCharge() {
        return processingCharge;
    }

    public void setProcessingCharge(double processingCharge) {
        this.processingCharge = processingCharge;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDeliverBefore() {
        return deliverBefore;
    }

    public void setDeliverBefore(long deliverBefore) {
        this.deliverBefore = deliverBefore;
    }

    public long getDeliveredOn() {
        return deliveredOn;
    }

    public void setDeliveredOn(long deliveredOn) {
        this.deliveredOn = deliveredOn;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    public void setPreparing(boolean preparing) {
        isPreparing = preparing;
    }

    public boolean isPreparingFinished() {
        return isPreparingFinished;
    }

    public void setPreparingFinished(boolean preparingFinished) {
        isPreparingFinished = preparingFinished;
    }

    public boolean isDelivering() {
        return isDelivering;
    }

    public void setDelivering(boolean delivering) {
        isDelivering = delivering;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }
}
