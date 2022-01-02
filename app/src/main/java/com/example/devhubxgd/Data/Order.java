package com.example.devhubxgd.Data;

public class Order {

    public int time_type,status,area,normalFoods,muslimFoods,id;//经度
    public String remark,roomNumber,c_time;//纬度
    public Order(int time_type,int area,int status,int normalFoods,int muslimFoods,String remark,String roomNumber,int id,String c_time){
        this.c_time = c_time;
        this.id = id;
        this.area = area;
        this.time_type = time_type;
        this.status = status;
        this.normalFoods = normalFoods;
        this.muslimFoods = muslimFoods;
        this.remark = remark;
        this.roomNumber = roomNumber;


    }
    public String getC_time() {
        return c_time;
    }

    public void setC_time(String c_time) {
        this.c_time = c_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArea() {
        return area;
    }

    public int getStatus() {
        return status;
    }

    public int getTime_type() {
        return time_type;
    }

    public int getMuslimFoods() {
        return muslimFoods;
    }

    public int getNormalFoods() {
        return normalFoods;
    }

    public String getRemark() {
        return remark;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public void setNormalFoods(int normalFoods) {
        this.normalFoods = normalFoods;
    }

    public void setMuslimFoods(int muslimFoods) {
        this.muslimFoods = muslimFoods;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTime_type(int time_type) {
        this.time_type = time_type;
    }
}
