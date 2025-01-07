package com.example.mobil_proje;

import java.io.Serializable;
public class Foods implements Serializable {
    String isim, gida_adi, tel_no, aciklama, resim, konum, userid;
    public String getIsim() {
        return isim;
    }
    public void setIsim(String isim) {
        this.isim = isim;
    }
    public String getGida_adi() {
        return gida_adi;
    }
    public void setGida_adi(String gida_adi) {
        this.gida_adi = gida_adi;
    }
    public String getTel_no() {
        return tel_no;
    }
    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }
    public String getAciklama() {
        return aciklama;
    }
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    public String getResim() {
        return resim;
    }
    public void setResim(String resim) {
        this.resim = resim;
    }
    public String getKonum() {
        return konum;
    }
    public void setKonum(String konum) {
        this.konum = konum;
    }
    public String getUserid() { //
        return userid;
    }
    public void setUserid(String userid) {
        this.userid = userid;
    }
}
